# Topology
![Topology](docs/images/bookinfo-samples-topology.png)

# Build

* For config-service, discovery-server, api-gateway, ratings, reviews, details, use mvn to build that
	```shell
	mvn clean package
	```
	It will generate Spring Boot fat *jar* file in each dir targets subdir

* For productpage, it's written in python, no need to build

	Then run the following command to build docker images:
	```shell
	./docker-build-push.sh 
	```



# Deploy to Kubernetes/K3s & Test

## Deploy pipy-operator
Check out [pipy-operator](https://github.com/flomesh-io/pipy-operator) code, enter the root folder of this project.
* Install Cert Manager v1.1.0
  	```shell
	kubectl apply -f etc/cert-manager-v1.1.0.yaml
	```

	Wait for the pods in **cert-manager** namespace are all running.
	```shell
	[root@crd ~]# kubectl get pods -n cert-manager
	NAMESPACE      NAME                                      READY   STATUS      RESTARTS   AGE
	cert-manager   cert-manager-6865f45f85-7gjcb             1/1     Running     0          9h
	cert-manager   cert-manager-cainjector-fdbc9f44-8xv27    1/1     Running     0          9h
	cert-manager   cert-manager-webhook-5d59497545-vdchs     1/1     Running     0          9h
	```
* Install Operator
  	```shell
	kubectl apply -f artifact/pipy-operator.yaml
	```

 	You should see the output like this：
	```shell
	[root@crd pipy-operator]# kubectl apply -f artifact/pipy-operator.yaml
	namespace/flomesh-system created
	Warning: apiextensions.k8s.io/v1beta1 CustomResourceDefinition is deprecated in v1.16+, unavailable in v1.22+; use apiextensions.k8s.io/v1 CustomResourceDefinition
	customresourcedefinition.apiextensions.k8s.io/proxies.flomesh.io created
	customresourcedefinition.apiextensions.k8s.io/proxyprofiles.flomesh.io created
	role.rbac.authorization.k8s.io/flomesh-leader-election-role created
	clusterrole.rbac.authorization.k8s.io/flomesh-manager-role created
	clusterrole.rbac.authorization.k8s.io/flomesh-proxy-role created
	Warning: rbac.authorization.k8s.io/v1beta1 ClusterRole is deprecated in v1.17+, unavailable in v1.22+; use rbac.authorization.k8s.io/v1 ClusterRole
	clusterrole.rbac.authorization.k8s.io/flomesh-metrics-reader created
	rolebinding.rbac.authorization.k8s.io/flomesh-leader-election-rolebinding created
	clusterrolebinding.rbac.authorization.k8s.io/flomesh-manager-rolebinding created
	clusterrolebinding.rbac.authorization.k8s.io/flomesh-proxy-rolebinding created
	service/flomesh-controller-manager-metrics-service created
	service/flomesh-pipy-sidecar-injector-service created
	service/flomesh-webhook-service created
	deployment.apps/flomesh-controller-manager created
	deployment.apps/flomesh-pipy-sidecar-injector created
	certificate.cert-manager.io/flomesh-serving-cert created
	issuer.cert-manager.io/flomesh-selfsigned-issuer created
	Warning: admissionregistration.k8s.io/v1beta1 MutatingWebhookConfiguration is deprecated in v1.16+, unavailable in v1.22+; use admissionregistration.k8s.io/v1 MutatingWebhookConfiguration
	mutatingwebhookconfiguration.admissionregistration.k8s.io/flomesh-mutating-webhook-configuration created
	mutatingwebhookconfiguration.admissionregistration.k8s.io/flomesh-sidecar-injector-webhook-configuration created
	Warning: admissionregistration.k8s.io/v1beta1 ValidatingWebhookConfiguration is deprecated in v1.16+, unavailable in v1.22+; use admissionregistration.k8s.io/v1 ValidatingWebhookConfiguration
	validatingwebhookconfiguration.admissionregistration.k8s.io/flomesh-validating-webhook-configuration created
	```

	Check the status of pods in **flomesh-system** namespace, ensure all pods are running：
	```shell
	[root@crd pipy-operator]# kubectl get pods -n flomesh-system
	NAMESPACE        NAME                                               READY   STATUS            RESTARTS   AGE
	flomesh-system   flomesh-pipy-sidecar-injector-69bb969f57-78n2z     1/1     Running           0          16m
	flomesh-system   flomesh-controller-manager-55fb9565bb-rrhqq        2/2     Running           0          16m
	```

## Deploy demo
All YAMLs are in the [kubernetes](kubernetes/) folder.

**First of All**, create a **ProxyProfile** for the demo. A ProxyProfile is a CRD which defines the configuration and routing rules for the [PIPY](https://github.com/flomesh-io/pipy) sidecar, please see [proxy-profile.yaml](kubernetes/proxy-profile.yaml) for more details.
```shell
kubectl apply -f proxy-profile.yaml
```

Check if it's created successfully:
```shell
root@k3s:~/flomesh-bookinfo-demo/kubernetes# kubectl get pf
NAME                         AGE
proxy-profile-002-bookinfo   32m
```


**Second**, you need to have ClickHouse installed somewhere, and create the log table by [init.sql](scripts/init.sql) in default schema:
```SQL
CREATE TABLE default.log
(
`uuid` String DEFAULT JSONExtractString(message, 'uuid'),
`type` String DEFAULT JSONExtractString(message, 'type'),
`message` String,
`timestamp` DateTime DEFAULT now()
)
ENGINE = MergeTree
ORDER BY uuid
SETTINGS index_granularity = 8192;
```

As the services has startup dependencies, you need to deploy it one by one following the strict sequence. Before starting, check the **Endpoints** section of **clickhouse.yaml**

```yaml
apiVersion: v1
kind: Endpoints
metadata:
  name: samples-clickhouse
  labels:
    app: clickhouse
    service: clickhouse
subsets:
  - addresses:
    - ip: 172.19.182.213
    ports:
    - name: chdb
      port: 8123
      protocol: TCP
```

Change the IP address and port according to your environment, then save and go to deploy:
```shell
kubectl apply -f discovery-server.yaml
kubectl apply -f clickhouse.yaml
```

Check the status and log to ensure the discovery server starts successfully and is UP.
```shell
root@k3s:~/flomesh-bookinfo-demo/kubernetes# kubectl get po
NAMESPACE        NAME                                             READY   STATUS              RESTARTS   AGE
default          samples-discovery-server-v1-56c79689c6-7n7kk     2/2     Running             0          4m14s
```

Deploy the Config Service:
```shell
kubectl apply -f config-service.yaml
```

Check the status and log to ensure the config server starts successfully and is UP.
```shell
root@k3s:~/flomesh-bookinfo-demo/kubernetes# kubectl get po
NAMESPACE        NAME                                             READY   STATUS      RESTARTS   AGE
default          samples-discovery-server-v1-56c79689c6-7n7kk     2/2     Running     0          4m42s
default          samples-config-service-v1-65ff699755-2fckg       1/1     Running     0          31s
```

After that deploy the API gateway, sample services and Ingress.

```shell
kubectl apply -f bookinfo.yaml
kubectl apply -f ingress.yaml
```

Check the status of all pods, ensure all are running:
```shell
root@k3s:~/flomesh-bookinfo-demo/kubernetes# kubectl get po
NAMESPACE        NAME                                               READY   STATUS      RESTARTS   AGE
default          samples-config-service-v1-65ff699755-8g2gv         1/1     Running     0          2m4s
default          svclb-samples-bookinfo-productpage-ql9h2           2/2     Running     0          89s
default          samples-api-gateway-v1-58674c965f-ph7t8            2/2     Running     0          90s
default          samples-bookinfo-details-v1-6cd4bd97fc-lzddv       2/2     Running     0          90s
default          samples-bookinfo-reviews-v1-bb6647cf6-dkf7x        2/2     Running     0          89s
default          samples-bookinfo-ratings-v1-755f99b955-k59zf       2/2     Running     0          90s
default          samples-bookinfo-productpage-v1-7bfcd6995c-ww7g5   2/2     Running     0          89s
default          samples-pipy-ingress-ds-56dvb                      1/1     Running     0          25s
```

Take a note of your **Ingress Host IP**, and remember the Ingress listens on port **8080** by default.
```shell
root@k3s:~# kubectl get nodes -o wide
NAME   STATUS   ROLES    AGE   VERSION        INTERNAL-IP   EXTERNAL-IP   OS-IMAGE             KERNEL-VERSION     CONTAINER-RUNTIME
k3s    Ready    master   18h   v1.19.7+k3s1   10.0.2.15     <none>        Ubuntu 20.04.2 LTS   5.4.0-65-generic   containerd://1.4.3-k3s1
```

If you're not sure about the Ingress port, you can check it by `kubectl get px`:
```shell
root@k3s~/flomesh-bookinfo-demo/kubernetes# kubectl get px
NAME                   MODE      PORT   IMAGE                 AGE
samples-pipy-ingress   Ingress   8080   flomesh/pipy:latest   13m
```

## Test rating service:

create ratings in k8s, replace the ***ingress-ip*** with your real Ingress IP address(or valid DNS name):

~~~~~bash
curl -X POST http://ingress-ip:8080/bookinfo-ratings/ratings \
	-H "Content-Type: application/json" \
	-d '{"reviewerId":"9bc908be-0717-4eab-bb51-ea14f669ef20","productId":"a071c269-369c-4f79-be03-6a41f27d6b5f","rating":3}' 
~~~~~

query ratings by product_id in kubernetes, replace the ***ingress-ip*** with your real Ingress IP address(or valid DNS name):

~~~~~bash
curl http://ingress-ip:8080/bookinfo-ratings/ratings/a071c269-369c-4f79-be03-6a41f27d6b5f
~~~~~

## Test review service:

create review in k8s, replace the ***ingress-ip*** with your real Ingress IP address(or valid DNS name):

~~~~~bash
curl -X POST http://ingress-ip:8080/bookinfo-reviews/reviews \
	-H "Content-Type: application/json" \
	-d '{"reviewerId":"9bc908be-0717-4eab-bb51-ea14f669ef20","productId":"a071c269-369c-4f79-be03-6a41f27d6b5f","review":"This was OK.","rating":3}'
~~~~~

query review by product_id in k8s, replace the ***ingress-ip*** with your real Ingress IP address(or valid DNS name):

~~~~~bash
curl http://ingress-ip:8080/bookinfo-reviews/reviews/a071c269-369c-4f79-be03-6a41f27d6b5f
~~~~~

## Test detail service

 query detail by isbn in k8s, replace the ***ingress-ip*** with your real Ingress IP address(or valid DNS name):

~~~~~bash
curl http://ingress-ip:8080/bookinfo-details/details/1234567890
~~~~~



# Run locally:

## Start Eureka service:
In the project root folder:
```shell
cd discovery-server
mvn spring-boot:run
```

## Start Config service:
In the project root folder:
```shell
cd config-service
mvn spring-boot:run
```

## Start API Gateway:
In the project root folder:
```shell
cd api-gateway
mvn spring-boot:run -Dspring.profiles.active=local
```

## Start rating service: 
In the project root folder:
~~~~~bash
cd ratings
mvn spring-boot:run
~~~~~
In VM environment, it will listen on localhost:8101. 

create rating in VM, visit the service directly:
~~~~~bash
curl -X POST http://localhost:8101/ratings \
	-H "Content-Type: application/json" \
	-d '{"reviewerId":"9bc908be-0717-4eab-bb51-ea14f669ef20","productId":"a071c269-369c-4f79-be03-6a41f27d6b5f","rating":3}' 
~~~~~

Through API Gateway:
~~~~~bash
curl -X POST http://localhost:10000/bookinfo-ratings/ratings \
	-H "Content-Type: application/json" \
	-d '{"reviewerId":"9bc908be-0717-4eab-bb51-ea14f669ef20","productId":"a071c269-369c-4f79-be03-6a41f27d6b5f","rating":3}' 
~~~~~

query ratings by product_id in vm:
~~~~~bash
curl http://localhost:8101/ratings/a071c269-369c-4f79-be03-6a41f27d6b5f
~~~~~

Through API Gateway:
~~~~~bash
curl http://localhost:10000/bookinfo-ratings/ratings/a071c269-369c-4f79-be03-6a41f27d6b5f
~~~~~

## Start review service:
In the project root folder:
~~~~~bash
cd reviews
mvn spring-boot:run
~~~~~
It will listen on localhost:8102 and will call localhost:8101 for rating query in vm environment.


create review in VM:
~~~~~bash
curl -X POST http://localhost:8102/reviews \
	-H "Content-Type: application/json" \
	-d '{"reviewerId":"9bc908be-0717-4eab-bb51-ea14f669ef20","productId":"a071c269-369c-4f79-be03-6a41f27d6b5f","review":"This was OK.","rating":3}' 
~~~~~

Through API Gateway:
~~~~~bash	
curl -X POST http://localhost:10000/bookinfo-reviews/reviews \
	-H "Content-Type: application/json" \
	-d '{"reviewerId":"9bc908be-0717-4eab-bb51-ea14f669ef20","productId":"a071c269-369c-4f79-be03-6a41f27d6b5f","review":"This was OK.","rating":3}'
~~~~~

query review by product_id in VM:
~~~~~bash
curl http://localhost:8102/reviews/a071c269-369c-4f79-be03-6a41f27d6b5f
~~~~~

Through API Gateway:
~~~~~bash
curl http://localhost:10000/bookinfo-reviews/reviews/a071c269-369c-4f79-be03-6a41f27d6b5f
~~~~~

## Start detail service:

In the project root folder:
~~~~~bash
cd details
mvn spring-boot:run
~~~~~
It will listen on localhost:8103

query detail by isbn:
~~~~~bash
curl http://localhost:8103/details/1234567890
~~~~~

Through API Gateway:
~~~~~bash
curl http://localhost:10000/bookinfo-details/details/1234567890
~~~~~
