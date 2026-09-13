# Deployment-Artefakt fuer den ticket-service.
# Das JAR wird nicht im Image gebaut, sondern aus der CI uebernommen
# (getestetes Build-Artefakt aus dem Job "Build & Test ticket-service").
#
# Lokal bauen (aus Code/Ticket_System/ticket-service, nach mvn package):
#   docker build -t ticket-service:local .
FROM eclipse-temurin:21-jre-alpine-3.22

WORKDIR /app

# Nicht als root laufen
RUN addgroup -S spring && adduser -S spring -G spring

COPY target/ticket-service-*.jar app.jar

USER spring

EXPOSE 8082

ENTRYPOINT ["java", "-jar", "/app/app.jar"]
