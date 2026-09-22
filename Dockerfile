FROM amazoncorretto:25-alpine

WORKDIR /app

RUN addgroup -S spring && adduser -S spring -G spring

# TODO(PI): mantenha este padrão alinhado ao artifactId definido no pom.xml.
COPY --chown=spring:spring target/template-pi-*.jar app.jar

USER spring:spring

EXPOSE 8080

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
