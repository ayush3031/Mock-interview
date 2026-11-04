# Step 1: Use Maven image to build the app
FROM maven:3.9.9-eclipse-temurin-21 AS build
WORKDIR /build
# Copy pom.xml alone first. Its hash is what determines whether the
# dependency-resolution layer below is reused.
COPY pom.xml .
# -B is batch mode, which avoids some interactive prompts and makes the build more reproducible.
# -ntp is "no transfer progress", which avoids some output that can vary from run to run. like progress bars and download speeds.
# -q is quiet mode, which avoids printing INFO messages about downloading dependencies.
#Not just RUN — every instruction. FROM, WORKDIR, ENV, COPY, ADD, RUN, USER, EXPOSE, ENTRYPOINT, CMD, LABEL, HEALTHCHECK, ARG — each one produces a layer
# The layer doesn't hold a full filesystem copy; it holds what changed. The runtime image is the union of all layers, read in order. When you docker run the image, Docker assembles a read-only union view across all layers.
RUN --mount=type=cache,target=/root/.m2 \
    mvn -B -ntp -q dependency:go-offline
COPY src ./src
# Build. -ntp = no transfer progress (cleaner CI logs).
# -DskipTests because tests run in CI, not in the image build.
RUN --mount=type=cache,target=/root/.m2 \
    mvn -B -ntp -q clean package -DskipTests
#RUN mvn clean package -DskipTests

# Step 2: Run the app
FROM eclipse-temurin:21-jdk
#attaches key-value metadata to the image, visible via docker inspect.OCI standards for Kubernetes
LABEL org.opencontainers.image.title="mock-interview-backend" \
      org.opencontainers.image.description="AI mock interview backend"\
      org.opencontainers.image.source="https://github.com/ayush3031/Mock-interview"
#creating new group and user for running the app, instead of running as root. This is a security best practice.
#--no-create-home prevents creating anything in /home or bashrc for safety. --shell /usr/sbin/nologin prevents logging in through shell.
#standard for Kubernetes and Docker containers is to run as a non-root user. This reduces the attack surface and limits the potential damage if the application is compromised.
#apt-get update
#Downloads Packages.xz index files from Ubuntu's apt mirrors → writes them to /var/lib/apt/lists/ (~25 MB)
#These are text catalogs, not the packages themselves
#Install only Depends, skip Recommends and say yes to defaut interactive download questions so installation doesn't hang. --no-install-recommends reduces image size by not installing extra packages that are not strictly necessary for the application to run.
#apt-get install curl has no internet search capability. It looks up curl in the local index files to find its version, its dependencies, and the URL to download from. Without an index, curl "doesn't exist" as far as apt is concerned.
#install curl command for health check.
#removing index as we don't need it anymore, reducing image size. apt-get clean removes the local repository of retrieved package files. rm -rf /var/lib/apt/lists/* removes the index files themselves.
RUN groupadd --system --gid 1001 app \
 && useradd  --system --uid 1001 --gid app --no-create-home --shell /usr/sbin/nologin app \
    && apt-get update \
    && apt-get install -y --no-install-recommends curl \
    && rm -rf /var/lib/apt/lists/*

WORKDIR /app
# Copy JAR from the build stage, owned by the non-root user.
# --from=build refers to the "AS build" alias above.
COPY --from=build --chown=app:app /build/target/*.jar app.jar
# Drop privileges BEFORE the entrypoint runs.
USER app
# JVM tuning for containers:
#   MaxRAMPercentage=75       → use 75% of the container's RAM limit for heap
#                                (defaults to 25% of HOST RAM otherwise — wrong in containers)
#   UseContainerSupport       → on by default since JDK 11; explicit for clarity
#   ExitOnOutOfMemoryError    → die cleanly on OOM instead of limping with broken state;
#                                restart policy brings it back
ENV JAVA_OPTS="-XX:MaxRAMPercentage=75 \
               -XX:+UseContainerSupport \
               -XX:+ExitOnOutOfMemoryError"
EXPOSE 8080
# Docker probes this every 30s. Failing 3 in a row marks the container "unhealthy".
# This is what `depends_on: condition: service_healthy` in compose waits on.
HEALTHCHECK --interval=30s --timeout=5s --start-period=45s --retries=3 \
    CMD curl -fsS http://localhost:8080/actuator/health || exit 1

# sh -c + exec: without exec, `sh` stays PID 1 and does NOT forward SIGTERM
# to java on `docker stop` — java gets SIGKILLed after 10s → abrupt shutdown.
# With exec, sh is replaced by java, java becomes PID 1, SIGTERM is delivered
# directly, and Spring Boot's graceful-shutdown can run.
ENTRYPOINT ["sh","-c","exec java $JAVA_OPTS -jar app.jar"]