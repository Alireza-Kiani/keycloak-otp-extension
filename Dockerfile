FROM maven:3.9.9-eclipse-temurin-23-noble AS build
WORKDIR /app
COPY . .

RUN mvn -X clean package -DskipTests

RUN ls

FROM quay.io/keycloak/keycloak:latest

COPY --from=build /app/phone-number-only-authenticator/target/*.jar /opt/keycloak/providers
COPY --from=build /app/otp-authenticator/target/*.jar /opt/keycloak/providers
RUN /opt/keycloak/bin/kc.sh build

ENTRYPOINT ["/opt/keycloak/bin/kc.sh"]
