# Use an official OpenJDK runtime as a parent image
FROM openjdk:17-jdk-slim

# Set the working directory in the container
WORKDIR /usr/src/app

# Copy the jar file from the target directory to the working directory in the container
COPY target/mockproject-0.0.1-SNAPSHOT.jar mockproject-0.0.1-SNAPSHOT.jar

# Expose the port the application runs on
EXPOSE 8080

# Run the jar file
ENTRYPOINT ["java", "-jar", "mockproject-0.0.1-SNAPSHOT.jar"]
