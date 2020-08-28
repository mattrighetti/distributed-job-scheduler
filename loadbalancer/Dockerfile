FROM fabric8/java-alpine-openjdk11-jre
COPY target/loadbalancer-0.3-loadbalancer.jar /loadbalancer.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "/loadbalancer.jar"]
CMD ["port"]