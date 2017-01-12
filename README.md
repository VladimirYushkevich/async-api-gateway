Asynchronous API Gateway
=
An API gateway (preferable Spring/Playframwork) that proxies two different REST end points and outputs the results into 
a merged (also JSON) response.
For example you could use these two endpoints:
http://jsonplaceholder.typicode.com/users/1 to obtain a userResponse's data
http://jsonplaceholder.typicode.com/posts?userId=1 to obtain all comments written by that userResponse

### Run service:
```
./gradlew clean build -i && java -jar build/libs/async-api-gateway-0.0.1-SNAPSHOT.jar
```
### Usage:
```
curl localhost:8080/gateway/users/1 | jq
```

