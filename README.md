# Jobberwocky

How to start the Jobberwocky application
---

1. Run `mvn clean install` to build your application
1. Start application with `java -jar target/jobberwocky-1.0-SNAPSHOT.jar server config.yml`
1. To check that your application is running enter url `http://localhost:8080`

Health Check
---

To see your applications health enter url `http://localhost:8081/healthcheck`


curl -X POST -H "Content-Type: application/json" -d "{\"title\":\"Software Engineer\"}" http://localhost:8080/jobs

curl ttp://localhost:8080/jobs?search=Software