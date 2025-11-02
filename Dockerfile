FROM eclipse-temurin:21-jre-jammy AS final
WORKDIR /app
COPY src/main/resources/application.yml /app/application.yml
#COPY ./target/*.jar *.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "*.jar"]