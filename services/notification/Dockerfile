FROM maven:3.9.9

RUN mkdir notification

WORKDIR notification

COPY . .

RUN mvn package -Dmaven.test.skip=true

CMD ["mvn", "liquibase:update", "-Pdocker"]

CMD ["java", "-jar", "target/notification-1.0.0.jar"]