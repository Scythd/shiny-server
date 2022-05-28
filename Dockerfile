FROM openjdk:17-alpine
COPY . /app
WORKDIR /app
ENV DB_URL=$POSTGRES_URL
ENV DB_USER=$POSTGRES_USER
ENV DB_PASS=$POSTGRES_PASS
ENV KAFKA_CON=$KAFKA_CONNECTION
RUN ["./mvnw", "clean", "install", "-Dmaven.test.skip=true"]
ENTRYPOINT ["./mvnw", "spring-boot:run"]