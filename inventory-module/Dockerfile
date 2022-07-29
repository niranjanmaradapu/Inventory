FROM openjdk:8
COPY  target/  target/
RUN  mv target/*.jar   target/inventory-module.jar
EXPOSE 9091

RUN apt install curl unzip -y 
RUN curl -O https://download.newrelic.com/newrelic/java-agent/newrelic-agent/current/newrelic-java.zip
#RUN unzip newrelic-java.zip  
#RUN mv newrelic  target/newrelic
#COPY newrelic.yml  target/newrelic/newrelic.yml

#ENV NEW_RELIC_APP_NAME="Inventory"
#ENV NEW_RELIC_LICENSE_KEY="4dacb6025e2eadaedd6562a3ea9f48d5FFFFNRAL"
#ENV NEW_RELIC_LOG_FILE_NAME="STDOUT"

ENTRYPOINT ["java", "-Dspring.profiles.active=cloud", "-jar", "target/inventory-module.jar"]

