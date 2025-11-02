FROM eclipse-temurin:21-jre-jammy AS final
WORKDIR /app
COPY application.yml /app/application.yml
#COPY ./target/*.jar *.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "*.jar"]