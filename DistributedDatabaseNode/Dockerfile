FROM java:openjdk-8

MAINTAINER Jozef Zatko <jozefzatko.sk@gmail.com>

# Install maven
RUN apt-get update
RUN apt-get install -y maven

WORKDIR /workdir

# Add fileabeat for logs

ENV CONSUL_URL 192.168.99.100:8500

ADD start.sh /usr/bin/start.sh
RUN chmod +x /usr/bin/start.sh

RUN apt-get install -y wget
RUN apt-get install -y curl
RUN apt-get install -y sudo

ADD filebeat /usr/bin/filebeat
ADD filebeat.template.json /usr/bin/filebeat.template.json
ADD filebeat.yml /usr/bin/filebeat.yml

RUN chmod +x /usr/bin/filebeat
RUN chmod +x /usr/bin/filebeat.template.json
RUN chmod +x /usr/bin/filebeat.yml

RUN wget http://stedolan.github.io/jq/download/linux64/jq
RUN chmod +x ./jq
RUN sudo cp jq /usr/bin 

# Prepare by downloading dependencies
ADD pom.xml /workdir/pom.xml
RUN ["mvn", "dependency:resolve"]
RUN ["mvn", "verify"]

# Adding source, compile and package into a fat jar
ADD src /workdir/src
RUN ["mvn", "package"]

# Run application on port 4567
EXPOSE 4567
ENTRYPOINT ["/usr/lib/jvm/java-8-openjdk-amd64/bin/java", "-jar","/usr/bin/start.sh"]


