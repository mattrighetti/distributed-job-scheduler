FROM fabric8/java-alpine-openjdk11-jre
WORKDIR /usr/src/app
COPY target/cluster-0.3-cluster.jar ./cluster.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "cluster.jar"]
CMD ["ip", "port"]