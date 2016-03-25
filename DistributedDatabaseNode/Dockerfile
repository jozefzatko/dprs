FROM java:openjdk-8

MAINTAINER Jozef Zatko <jozefzatko.sk@gmail.com>

# Install maven
RUN apt-get update
RUN apt-get install -y maven

WORKDIR /workdir

# Prepare by downloading dependencies
ADD pom.xml /workdir/pom.xml
RUN ["mvn", "dependency:resolve"]
RUN ["mvn", "verify"]

# Adding source, compile and package into a fat jar
ADD src /workdir/src
RUN ["mvn", "package"]

# Run application on port 4567
EXPOSE 4567
CMD ["/usr/lib/jvm/java-8-openjdk-amd64/bin/java", "-jar", "/workdir/target/distributed-database-node-jar-with-dependencies.jar"] 
