FROM eclipse-temurin:22-jdk-alpine

WORKDIR /app

COPY target/mahashaktiBE.jar /app/mahashaktiBE.jar

EXPOSE 8080

# Define environment variables for JVM options (optional)
ENV JAVA_OPTS=""

CMD ["sh", "-c", "java $JAVA_OPTS -jar /app/mahashaktiBE.jar"]