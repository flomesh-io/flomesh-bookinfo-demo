# Build

* For config-service, discovery-server, api-gateway, ratings, reviews, details, use mvn to build that
~~~~~bash
mvn clean package
~~~~~
It will generate Spring Boot fat *jar* file in each dir targets subdir

* For productpage, it's written in python, no need to build

Then run the following command to build docker images:
```bash
./docker-build-push.sh 
```



# Deploy to Kubernetes/K3s & Test

## Deploy

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
    - ip: 127.0.0.1
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

Then check the running status and logs to ensure the discovery server starts successfully and is UP.

```shell
kubectl apply -f config-service.yaml
```

Then check the running status and logs to ensure the config server starts successfully and is UP.

After that deploy the sample services and Ingress.

```shell
kubectl apply -f bookinfo.yaml
kubectl apply -f ingress.yaml
```

Take a note of your **Ingress public IP**, and the Ingress listens on port 8080 by default.

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
