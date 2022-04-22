FROM openjdk:8
COPY  target/  target/
RUN  mv target/*.jar   target/inventory-module.jar
EXPOSE 9091
ENTRYPOINT ["java", "-Dspring.profiles.active=cloud", "-jar", "target/inventory-module.jar"]
