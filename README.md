# Topology
![Topology](docs/images/bookinfo-samples-topology.png)


# Build

* For config-service, discovery-server, api-gateway, ratings, reviews, details, use mvn to build that
	```shell
	mvn clean package
	```
	It will generate Spring Boot fat *jar* file in each dir targets subdir

* For productpage, it's written in Node.js, you need to have node installed in advance.
	```shell
	cd productpage
    npm install
	```




# Deploy to Kubernetes/K3s & Test

## Deploy pipy-operator
Check out [pipy-operator](https://github.com/flomesh-io/pipy-operator) code, enter the root folder of this project.
* Install Cert Manager v1.3.1
  	```shell
	root@bookinfo:~/pipy-operator# kubectl apply -f etc/cert-manager-v1.3.1.yaml
	
	customresourcedefinition.apiextensions.k8s.io/certificaterequests.cert-manager.io created
	customresourcedefinition.apiextensions.k8s.io/certificates.cert-manager.io created
	customresourcedefinition.apiextensions.k8s.io/challenges.acme.cert-manager.io created
	customresourcedefinition.apiextensions.k8s.io/clusterissuers.cert-manager.io created
	customresourcedefinition.apiextensions.k8s.io/issuers.cert-manager.io created
	customresourcedefinition.apiextensions.k8s.io/orders.acme.cert-manager.io created
	namespace/cert-manager created
	serviceaccount/cert-manager-cainjector created
	serviceaccount/cert-manager created
	serviceaccount/cert-manager-webhook created
	clusterrole.rbac.authorization.k8s.io/cert-manager-cainjector created
	clusterrole.rbac.authorization.k8s.io/cert-manager-controller-issuers created
	clusterrole.rbac.authorization.k8s.io/cert-manager-controller-clusterissuers created
	clusterrole.rbac.authorization.k8s.io/cert-manager-controller-certificates created
	clusterrole.rbac.authorization.k8s.io/cert-manager-controller-orders created
	clusterrole.rbac.authorization.k8s.io/cert-manager-controller-challenges created
	clusterrole.rbac.authorization.k8s.io/cert-manager-controller-ingress-shim created
	clusterrole.rbac.authorization.k8s.io/cert-manager-view created
	clusterrole.rbac.authorization.k8s.io/cert-manager-edit created
	clusterrole.rbac.authorization.k8s.io/cert-manager-controller-approve:cert-manager-io created
	clusterrole.rbac.authorization.k8s.io/cert-manager-webhook:subjectaccessreviews created
	clusterrolebinding.rbac.authorization.k8s.io/cert-manager-cainjector created
	clusterrolebinding.rbac.authorization.k8s.io/cert-manager-controller-issuers created
	clusterrolebinding.rbac.authorization.k8s.io/cert-manager-controller-clusterissuers created
	clusterrolebinding.rbac.authorization.k8s.io/cert-manager-controller-certificates created
	clusterrolebinding.rbac.authorization.k8s.io/cert-manager-controller-orders created
	clusterrolebinding.rbac.authorization.k8s.io/cert-manager-controller-challenges created
	clusterrolebinding.rbac.authorization.k8s.io/cert-manager-controller-ingress-shim created
	clusterrolebinding.rbac.authorization.k8s.io/cert-manager-controller-approve:cert-manager-io created
	clusterrolebinding.rbac.authorization.k8s.io/cert-manager-webhook:subjectaccessreviews created
	role.rbac.authorization.k8s.io/cert-manager-cainjector:leaderelection created
	role.rbac.authorization.k8s.io/cert-manager:leaderelection created
	role.rbac.authorization.k8s.io/cert-manager-webhook:dynamic-serving created
	rolebinding.rbac.authorization.k8s.io/cert-manager-cainjector:leaderelection created
	rolebinding.rbac.authorization.k8s.io/cert-manager:leaderelection created
	rolebinding.rbac.authorization.k8s.io/cert-manager-webhook:dynamic-serving created
	service/cert-manager created
	service/cert-manager-webhook created
	deployment.apps/cert-manager-cainjector created
	deployment.apps/cert-manager created
	deployment.apps/cert-manager-webhook created
	mutatingwebhookconfiguration.admissionregistration.k8s.io/cert-manager-webhook created
	validatingwebhookconfiguration.admissionregistration.k8s.io/cert-manager-webhook created
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
	namespace/flomesh-system created
	customresourcedefinition.apiextensions.k8s.io/proxies.flomesh.io created
	customresourcedefinition.apiextensions.k8s.io/proxyprofiles.flomesh.io created
	serviceaccount/flomesh-controller-manager created
	role.rbac.authorization.k8s.io/flomesh-leader-election-role created
	clusterrole.rbac.authorization.k8s.io/flomesh-manager-role created
	clusterrole.rbac.authorization.k8s.io/flomesh-metrics-reader created
	clusterrole.rbac.authorization.k8s.io/flomesh-proxy-role created
	rolebinding.rbac.authorization.k8s.io/flomesh-leader-election-rolebinding created
	clusterrolebinding.rbac.authorization.k8s.io/flomesh-manager-rolebinding created
	clusterrolebinding.rbac.authorization.k8s.io/flomesh-proxy-rolebinding created
	configmap/flomesh-manager-config created
	configmap/flomesh-proxy-injector-tpl created
	service/flomesh-controller-manager-metrics-service created
	service/flomesh-pipy-sidecar-injector-service created
	service/flomesh-webhook-service created
	deployment.apps/flomesh-controller-manager created
	deployment.apps/flomesh-pipy-sidecar-injector created
	certificate.cert-manager.io/flomesh-serving-cert created
	issuer.cert-manager.io/flomesh-selfsigned-issuer created
	mutatingwebhookconfiguration.admissionregistration.k8s.io/flomesh-mutating-webhook-configuration created
	mutatingwebhookconfiguration.admissionregistration.k8s.io/flomesh-sidecar-injector-webhook-configuration created
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
All YAMLs are in the [kubernetes](kubernetes/) folder, please `cd kubernetes/` in advance.

**First of All**, create a **ProxyProfile** for the demo. A ProxyProfile is a CRD which defines the configuration and routing rules for the [pipy](https://github.com/flomesh-io/pipy) sidecar, please see [proxy-profile.yaml](kubernetes/sidecar/proxy-profile.yaml) for more details.
```shell
kubectl apply -f sidecar/proxy-profile.yaml
```

Check if it's created successfully:
```shell
root@k3s:~/flomesh-bookinfo-demo/kubernetes# kubectl get pf
NAME                         SELECTOR                                                    NAMESPACE   AGE
proxy-profile-002-bookinfo   {"matchLabels":{"sys":"bookinfo-samples","version":"v1"}}   default     43m
```

**Second**, install ingress controller(ingress-pipy).
```shell
root@bookinfo:/vagrant/kubernetes# kubectl apply -f ingress/ingress-pipy.yaml 

namespace/ingress-pipy created
customresourcedefinition.apiextensions.k8s.io/ingressparameters.flomesh.io created
serviceaccount/ingress-pipy created
role.rbac.authorization.k8s.io/ingress-pipy-leader-election-role created
clusterrole.rbac.authorization.k8s.io/ingress-pipy-role created
rolebinding.rbac.authorization.k8s.io/ingress-pipy-leader-election-rolebinding created
clusterrolebinding.rbac.authorization.k8s.io/ingress-pipy-rolebinding created
configmap/ingress-config created
service/ingress-pipy-cfg created
service/ingress-pipy-controller created
service/ingress-pipy-defaultbackend created
service/webhook-service created
deployment.apps/ingress-pipy-cfg created
deployment.apps/ingress-pipy-controller created
deployment.apps/ingress-pipy-manager created
certificate.cert-manager.io/serving-cert created
issuer.cert-manager.io/selfsigned-issuer created
mutatingwebhookconfiguration.admissionregistration.k8s.io/mutating-webhook-configuration created
validatingwebhookconfiguration.admissionregistration.k8s.io/validating-webhook-configuration created
```

Check the status of pods in **ingress-pipy** namespace, ensure all pods are running：
```shell
root@bookinfo:/vagrant/kubernetes# kubectl get po -n ingress-pipy 
NAME                                       READY   STATUS    RESTARTS   AGE
svclb-ingress-pipy-controller-p8h9g        1/1     Running   0          2m58s
ingress-pipy-cfg-6856d674f7-zcgbr          1/1     Running   0          2m58s
ingress-pipy-controller-76cd866d78-bcmb5   1/1     Running   0          2m58s
ingress-pipy-manager-6dddc98484-q4ls5      1/1     Running   0          2m58s
```

**Third**, you need to have ClickHouse installed somewhere, and create the log table by [init-log.sql](scripts/init-log.sql) in default schema:
```SQL
CREATE TABLE default.log
(
	`startTime` Int64 DEFAULT JSONExtractInt(message, 'startTime'),
	`endTime` Int64 DEFAULT JSONExtractInt(message, 'endTime'),
	`latency` Int64 DEFAULT JSONExtractInt(message, 'latency'),
	`status` Int16 DEFAULT JSONExtractInt(response, 'status'),
	`statusText` String DEFAULT JSONExtractString(response, 'statusText'),
	`protocol` String DEFAULT JSONExtractString(message, 'protocol'),
	`method` String DEFAULT JSONExtractString(message, 'method'),
	`path` String DEFAULT JSONExtractString(message, 'path'),
	`headers` String DEFAULT JSONExtractRaw(message, 'headers'),
	`body` String DEFAULT JSONExtractString(message, 'body'),
	`response` String DEFAULT JSONExtractRaw(message, 'response'),
	`response.protocol` String DEFAULT JSONExtractString(response, 'protocol'),
	`message` String
)
ENGINE = MergeTree
PARTITION BY (toYYYYMM(toDateTime(startTime / 1000)))
ORDER BY (status, startTime)
SETTINGS index_granularity = 8192;
```

As the services has startup dependencies, you need to deploy it one by one following the strict order. Before starting, check the **Endpoints** section of **base/clickhouse.yaml**

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
kubectl apply -f base/discovery-server.yaml
kubectl apply -f base/clickhouse.yaml
```

Check the status and log to ensure the discovery server starts successfully and is UP.
```shell
root@k3s:~/flomesh-bookinfo-demo/kubernetes# kubectl get po
NAMESPACE        NAME                                             READY   STATUS              RESTARTS   AGE
default          samples-discovery-server-v1-56c79689c6-7n7kk     2/2     Running             0          4m14s
```

Deploy the Config Service:
```shell
kubectl apply -f base/config-service.yaml
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
kubectl apply -f base/bookinfo.yaml
kubectl apply -f ingress/ingress.yaml
```

Check the status of all pods, ensure all are running:
```shell
root@k3s:~/flomesh-bookinfo-demo/kubernetes# kubectl get po
NAMESPACE        NAME                                               READY   STATUS      RESTARTS   AGE
default          samples-config-service-v1-65ff699755-8g2gv         1/1     Running     0          2m4s
default          samples-api-gateway-v1-58674c965f-ph7t8            2/2     Running     0          90s
default          samples-bookinfo-details-v1-6cd4bd97fc-lzddv       2/2     Running     0          90s
default          samples-bookinfo-reviews-v1-bb6647cf6-dkf7x        2/2     Running     0          89s
default          samples-bookinfo-ratings-v1-755f99b955-k59zf       2/2     Running     0          90s
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
curl -X POST http://10.0.2.15/bookinfo-ratings/ratings \
	-H "Content-Type: application/json" \
	-d '{"reviewerId":"9bc908be-0717-4eab-bb51-ea14f669ef20","productId":"a071c269-369c-4f79-be03-6a41f27d6b5f","rating":3}' 
~~~~~

query ratings by product_id in kubernetes, replace the ***ingress-ip*** with your real Ingress IP address(or valid DNS name):

~~~~~bash
curl http://10.0.2.15/bookinfo-ratings/ratings/a071c269-369c-4f79-be03-6a41f27d6b5f
~~~~~

## Test review service:

create review in k8s, replace the ***ingress-ip*** with your real Ingress IP address(or valid DNS name):

~~~~~bash
curl -X POST http://10.0.2.15/bookinfo-reviews/reviews \
	-H "Content-Type: application/json" \
	-d '{"reviewerId":"9bc908be-0717-4eab-bb51-ea14f669ef20","productId":"a071c269-369c-4f79-be03-6a41f27d6b5f","review":"This was OK.","rating":3}'
~~~~~

query review by product_id in k8s, replace the ***ingress-ip*** with your real Ingress IP address(or valid DNS name):

~~~~~bash
curl http://10.0.2.15/bookinfo-reviews/reviews/a071c269-369c-4f79-be03-6a41f27d6b5f
~~~~~

## Test detail service

 query detail by isbn in k8s, replace the ***ingress-ip*** with your real Ingress IP address(or valid DNS name):

~~~~~bash
curl http://10.0.2.15/bookinfo-details/details/1234567890
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

## Start Product Page
```shell
cd productpage
npm start
```

Open a Web Browser, and navigate to `http://localhost:9080`