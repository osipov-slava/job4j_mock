FROM maven:3.9.9

RUN mkdir desc

WORKDIR desc

COPY . .

RUN mvn package -Dmaven.test.skip=true

CMD ["mvn", "liquibase:update", "-Pdocker"]

CMD ["java", "-jar", "target/desc-1.0.0.jar"]