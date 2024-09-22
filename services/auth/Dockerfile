FROM maven:3.9.9

RUN mkdir auth

WORKDIR auth

COPY . .

RUN mvn package -Dmaven.test.skip=true

CMD ["mvn", "liquibase:update", "-Pdocker"]

CMD ["java", "-jar", "target/auth-1.0.0.jar"]