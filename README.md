# Next Generation Engineering (NGE) Example Server

The NGE example server app provides a Spring Boot backend for the TodoMVC application. Below you can find some information on the application and how to use it.

# Design
The backend is structured by the use of multiple design patterns (layering and component structures). The structure is defined in the following chapters.
## Layers

## Patterns...

# Database
The backend makes use of a database and can handle (should handle soon) multiple databases. By default a H2 in memory database is applied, but you can also specify a PostgreSQL database by adjusting some configuration values.

Switching the configuration is possible by overriding the values provided in the application.yml through environment variables (e.g. in the docker container) or by switching to another stage specific application-(stage).yml. The stage to use can be set in an environment variable or can be passed on application startup.
## H2
Describe the H2 configuration.
## PostgreSQL
Descibe the PostgreSQL configuration
# Build and Deploy
The backend is build using maven for the Java part and uses Docker to create a container. In case somethign is not working smooth locally please also have a look at the .gitlab-ci.yml as there the whole build and deployment process (to kubernetes) is specified.
## Maven
You can just run `mvn clean install` and you will find the resulting fat-jar in the target folder.
## Docker
Using `docker build -t <tag-name> .` you can build a docker container from the root folder of this application (where the Dockerfile is located). 
## GitLab-CI

## Run locally
You can run the Application locally by using multiple technologies. How to use them is describes further down.
### Java Application
You can run the backend by just running the jar file that is created by the maven build. 
`java -jar /target/*.jar` 
### Docker Container
In case you did also run the docker build you can also run the docker container locally with your local docker installation
`docker run -p 8080:8080 <container tag>`
### Minikube
In case you also have minikube installed you can deploy your application to kubernetes
`kubectl apply -f deployment.yml` (The deployment.yml maybe needs to be adjusted, or the minikube setup needs to be scripted or described in another place)