FROM fabric8/java-alpine-openjdk11-jre
WORKDIR /usr/src/app
COPY target/loadbalancer-0.3-loadbalancer.jar ./loadbalancer.jar
EXPOSE 8080
ENV MAX_NUM_NODES 10
ENV DISPATCH_PERIOD 5000
ENV MAX_NUM_JOBS_DISPATCH 30
ENV REQUEST_RESULT_PERIOD 3000
ENTRYPOINT ["java", "-jar", "loadbalancer.jar"]
CMD ["port"]