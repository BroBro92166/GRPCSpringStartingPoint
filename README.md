# GRPCSpringStartingPoint
Based upon https://github.com/sajeerzeji/SpringBoot-GRPC Commands for preparing the enviornment (Assuming you are in the main folder e.g. the one with the pom.xml file in it)

sudo apt update
sudo apt install default-jdk maven
(From grpc-server folder) mvn clean package install
(From grpc-server folder) mvn package -Dmaven.test.skip=true
(From grpc-server folder) chmod 777 mvnw
(From grpc-server folder) ./mvnw spring-boot:run -Dmaven.test.skip=true
(From grpc-client folder e.g. seperate ssh connection) mvn clean package install
(From grpc-client folder e.g. seperate ssh connection) mvn package -Dmaven.test.skip=true
(From grpc-client folder e.g. seperate ssh connection) chmod 777 mvnw
(From grpc-client folder e.g. seperate ssh connection) ./mvnw spring-boot:run -Dmaven.test.skip=true
