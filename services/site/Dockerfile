FROM maven:3.9.9

RUN mkdir site

WORKDIR site

COPY . .

RUN mvn package -Dmaven.test.skip=true

CMD ["java", "-jar", "target/site-1.0.0.jar"]
