FROM eclipse-temurin:21-jdk AS build
WORKDIR /app
RUN curl -fsSL https://github.com/sbt/sbt/releases/download/v1.10.1/sbt-1.10.1.tgz | tar -xz -C /opt \
    && ln -s /opt/sbt/bin/sbt /usr/local/bin/sbt
COPY project ./project
COPY build.sbt ./
RUN sbt update
COPY src ./src
RUN sbt test stage

FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/target/universal/stage ./stage
ENTRYPOINT ["/app/stage/bin/scala-stakeholder"]
