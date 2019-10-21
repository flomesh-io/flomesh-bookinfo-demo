# Build

* For ratings, reviews, details, use mvn to run that
~~~~~bash
mvn clean package
~~~~~
It will generated springboot fat *jar* file in each dir targets subdir

* For productpage, it's written in python. It's not need to build

# Run

## Run in vm:

1. start rating service: 
~~~~~bash
java -jar bookinfo-ratings-1.0.0-SNAPSHOT.jar --opentracing.jaeger.http-sender.url=http://jaeger-collector.default.svc:14268/api/traces
~~~~~
In VM environment, it will listen on localhost:8101. 

create rating in VM:
~~~~~bash
curl -d '{"reviewerId":"9bc908be-0717-4eab-bb51-ea14f669ef20","productId":"a071c269-369c-4f79-be03-6a41f27d6b5f","rating":3}' -H "Content-Type: application/json" -X POST http://localhost:8101/ratings
~~~~~

or, create ratings in k8s:
~~~~~bash
curl -d '{"reviewerId":"9bc908be-0717-4eab-bb51-ea14f669ef20","productId":"a071c269-369c-4f79-be03-6a41f27d6b5f","rating":3}' -H "Content-Type: application/json" -X POST http://bookinfo-ratings.default.svc:9080/ratings
~~~~~

query ratings by product_id in vm:
~~~~~bash
curl http://localhost:8101/ratings/a071c269-369c-4f79-be03-6a41f27d6b5f
~~~~~

or, query ratings by product_id in kubernetes:
~~~~~bash
curl http://bookinfo-ratings.default.svc:9080/ratings/a071c269-369c-4f79-be03-6a41f27d6b5f
~~~~~

2. start review service:
~~~~~bash
java -jar bookinfo-reviews-1.0.0-SNAPSHOT.jar --bookinfo-ratings.url=localhost:8101 --opentracing.jaeger.http-sender.url=http://jaeger-collector.default.svc:14268/api/traces
~~~~~
It will listen on localhost:8102 and will call localhost:8101 for rating query in vm environment.

or, start review service in k8s:
~~~~~bash
java -jar bookinfo-reviews-1.0.0-SNAPSHOT.jar --bookinfo-ratings.url=bookinfo-ratings.default.svc:9080 --opentracing.jaeger.http-sender.url=http://jaeger-collector.default.svc:14268/api/traces
~~~~~

create review:
~~~~~bash
curl -d '{"reviewerId":"9bc908be-0717-4eab-bb51-ea14f669ef20","productId":"a071c269-369c-4f79-be03-6a41f27d6b5f","review":"This was OK.","rating":3}' -H "Content-Type: application/json" -X POST http://localhost:8102/reviews
~~~~~

or, create review in k8s:
~~~~~bash
curl -d '{"reviewerId":"9bc908be-0717-4eab-bb51-ea14f669ef20","productId":"a071c269-369c-4f79-be03-6a41f27d6b5f","review":"This was OK.","rating":3}' -H "Content-Type: application/json" -X POST http://bookinfo-reviews.default.svc:9080/reviews
~~~~~

query review by product_id:
~~~~~bash
curl http://localhost:8102/reviews/a071c269-369c-4f79-be03-6a41f27d6b5f
~~~~~

or, query review by product_id in k8s:
~~~~~bash
curl http://bookinfo-reviews.default.svc:9080/reviews/a071c269-369c-4f79-be03-6a41f27d6b5f
~~~~~

3. start detail service:
~~~~~bash
java -jar bookinfo-details-1.0.0-SNAPSHOT.jar --opentracing.jaeger.http-sender.url=http://jaeger-collector.default.svc:14268/api/traces
~~~~~
It will listen on localhost:8103

query detail by isbn:
~~~~~bash
curl http://localhost:8103/details/1234567890
~~~~~

or, query detail by isbn in k8s:
~~~~~bash
curl http://bookinfo-details.default.svc/details/1234567890
~~~~~


