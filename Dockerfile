# Create the jar file inside a docker container
FROM maven:3.6-jdk-13 as build
COPY src /usr/src/workflow-graph/src
COPY pom.xml /usr/src/workflow-graph
RUN mvn -f /usr/src/workflow-graph/pom.xml clean package -Dmaven.test.skip package

# Create the docker image
FROM openjdk:13-jdk-alpine3.10
COPY --from=build /usr/src/workflow-graph/target/workflow-to-graph-*.jar /usr/workflow-graph/workflow-to-graph.jar
EXPOSE 8080
ENTRYPOINT [ "java", "-jar", "/usr/workflow-graph/workflow-to-graph.jar" ]