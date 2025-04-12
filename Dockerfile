# ✅ Multi-platform base image (Debian-based)
FROM eclipse-temurin:18-jdk

# Install required dependencies for build
RUN apt-get update && \
    apt-get install -y git curl maven && \
    apt-get clean

# Set working directory
WORKDIR /app

# Clone and build bankingapp-common
RUN git clone https://github.com/IvanHomziak/bankingapp-common.git && \
    cd bankingapp-common && \
    mvn clean install -DskipTests=true

# Go back to /app directory
WORKDIR /app

# Copy pom.xml and resolve dependencies (enables caching)
COPY pom.xml .
RUN mvn dependency:resolve

# Copy full source
COPY src src

# Build the project
RUN mvn clean package -DskipTests=true

# Run the application
ENTRYPOINT ["java", "-jar", "target/bankingapp-users-ms.jar"]
