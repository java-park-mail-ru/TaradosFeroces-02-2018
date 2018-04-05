


install:
	mvn -Dmaven.test.skip=true install

deadlinez-in-docker:
	service postgresql start && mvn clean package && java -Xmx500M -jar target/deadlinez-1.0.0.jar

run:
	java -Xmx500M -jar ./target/deadlinez-1.0.0.jar

tests:
	service postgresql start && mvn test -B
