FROM openjdk:17-jdk
COPY ./build/libs/apigateway-service.jar apigateway-service.jar
ENTRYPOINT ["java", "-XX:+UseG1GC", "-XX:MaxGCPauseMillis=200", "-jar", "apigateway-service.jar"]