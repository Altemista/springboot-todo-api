# image that will only be utilized to build the code
FROM maven:3.6-jdk-11-slim AS builder

# copying all information and code relevant to build the application
COPY src /usr/src/app/src  
COPY pom.xml /usr/src/app  

# build the code with maven
WORKDIR /usr/src/app
RUN mvn package


# base image to run our application
FROM harbor.nge.altemista.cloud/base/openjdk:11

# copy the compiled .jar file from the build step to our runtime container
COPY --from=builder /usr/src/app/target/*.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom","-jar","/app.jar"]
