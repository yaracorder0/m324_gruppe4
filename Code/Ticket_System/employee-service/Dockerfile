# Deployment-Artefakt fuer den employee-service.
# Das JAR wird nicht im Image gebaut, sondern aus der CI uebernommen
# (getestetes Build-Artefakt aus dem Job "Build & Test employee-service").
#
# Lokal bauen (aus Code/Ticket_System/employee-service, nach mvn package):
#   docker build -t employee-service:local .
FROM eclipse-temurin:21-jre-alpine-3.22

WORKDIR /app

# Nicht als root laufen
RUN addgroup -S spring && adduser -S spring -G spring

COPY target/employee-service-*.jar app.jar

USER spring

EXPOSE 8081

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
