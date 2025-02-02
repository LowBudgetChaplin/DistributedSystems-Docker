FROM openjdk:17-jdk-slim
WORKDIR /app
RUN apt-get update && apt-get install -y net-tools iputils-ping procps
COPY C03.jar /app/
RUN mkdir -p /app/processed-images
EXPOSE 8091
CMD ["java", "-Djava.rmi.server.hostname=0.0.0.0", "-cp", "/app/C03.jar:/app/libs/SharedInterfaces.jar", "rmi.ZoomTopicListener"]
