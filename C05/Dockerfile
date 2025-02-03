FROM openjdk:17-jdk-slim
WORKDIR /app
RUN apt-get update && apt-get install -y net-tools iputils-ping procps
COPY C05-zoomout.jar /app/
EXPOSE 1098
CMD ["java", "-Djava.rmi.server.hostname=0.0.0.0", "-cp", "/app/C05-zoomout.jar:/app/libs/SharedInterfaces.jar", "rmi.ZoomOutServer"]
