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
java -jar bookinfo-ratings-1.0.0-SNAPSHOT.jar
~~~~~
It will listen on localhost:8101

create rating:
~~~~~bash
curl -d '{"reviewerId":"9bc908be-0717-4eab-bb51-ea14f669ef20","productId":"a071c269-369c-4f79-be03-6a41f27d6b5f","rating":3}' -H "Content-Type: application/json" -X POST http://localhost:8101/ratings
~~~~~

query ratings by product_id:
~~~~~bash
curl http://localhost:8101/ratings/a071c269-369c-4f79-be03-6a41f27d6b5f
~~~~~

2. start review service:
~~~~~bash
java -jar bookinfo-reviews-1.0.0-SNAPSHOT.jar --bookinfo-ratings.url=localhost:8101
~~~~~
It will listen on localhost:8102 and will call localhost:8101 for rating query

create review:
~~~~~bash
curl -d '{"reviewerId":"9bc908be-0717-4eab-bb51-ea14f669ef20","productId":"a071c269-369c-4f79-be03-6a41f27d6b5f","review":"This was OK.","rating":3}' -H "Content-Type: application/json" -X POST http://localhost:8102/reviews
~~~~~

query review by product_id:
~~~~~bash
curl http://localhost:8102/reviews/a071c269-369c-4f79-be03-6a41f27d6b5f
~~~~~

3. start detail service:
~~~~~bash
java -jar bookinfo-details-1.0.0-SNAPSHOT.jar
~~~~~
It will listen on localhost:8103

query detail by isbn:
~~~~~bash
curl http://localhost:8103/details/1234567890
~~~~~
