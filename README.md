# Java SDK for Bolt

This Java Solution provides Java Client Library for Bolt.

It can be built using any of the standard Java IDEs (including IntelliJ IDEA and Eclipse IDE for Java Developers)
using the included project files.

## Installation

### Requirements
- Java 8.0 or later
- Apache Maven (3.0 or higher) / Gradle (5.0 or higher)

### Building the Jar from source code
- Clone/Download [Java SDK for Bolt](https://github.com/project-n-oss/projectn-bolt-java) repository
- cd into `projectn-bolt-java` folder
- Run `mvn clean package` 
- After successful completion of last command, you should have the jar in `projectn-bolt-java/target/projectn-bolt-aws-java-1.0.0.jar`
- Your project `pom.xml` file should point to this local jar file as explained below

### Using Java SDK for Bolt

#### Maven
* Import the SDK from local directory into your project by adding the following dependency to your project's POM

```xml
<dependency>
  <groupId>com.gitlab.projectn-oss</groupId>
  <artifactId>projectn-bolt-aws-java</artifactId>
  <scope>system</scope>
  <version>1.0.0</version>
  <systemPath>Path to local bolt-aws-sdk jar file/projectn-bolt-aws-java-1.0.0.jar</systemPath>
</dependency>
```

### Usage
Please refer [ProjectN Bolt Java Sample](https://gitlab.com/projectnn/krypton/-/blob/master/cluster-tests/aws_java_test/) for a sample AWS Lambda Application in Java that utilizes Java SDK for Bolt

### Notes:
- Passthrough is disabled and not configurable
- Java SDK for Bolt is built on AWS Java-SDK 2.18.16, bolt client hase methods signature same as AWS Java SDK signature. 