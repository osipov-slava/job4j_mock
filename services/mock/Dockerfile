FROM maven:3.9.9

RUN mkdir mock

WORKDIR mock

COPY . .

RUN mvn package -Dmaven.test.skip=true

CMD ["mvn", "liquibase:update", "-Pdocker"]

CMD ["java", "-jar", "target/mock-1.0.0.jar"]