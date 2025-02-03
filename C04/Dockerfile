FROM openjdk:17-jdk-slim
WORKDIR /app
RUN apt-get update && apt-get install -y net-tools iputils-ping procps
COPY C04-zoomin.jar /app/
EXPOSE 1099
CMD ["java", "-Djava.rmi.server.hostname=0.0.0.0", "-cp", "/app/C04-zoomin.jar:/app/libs/SharedInterfaces.jar", "rmi.ZoomInServer"]
