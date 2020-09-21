FROM openjdk:14-alpine
COPY build/libs/reactive-retry-issue-*-all.jar reactive-retry-issue.jar
EXPOSE 8080
CMD ["java", "-Dcom.sun.management.jmxremote", "-Xmx128m", "-jar", "reactive-retry-issue.jar"]