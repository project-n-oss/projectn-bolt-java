granica-java-sdk# Java SDK for Granica

This Java Solution provides Java Client Library for Granica.

It can be built using any of the standard Java IDEs (including IntelliJ IDEA and Eclipse IDE for Java Developers)
using the included project files.

## Installation

### Requirements
- Java 8.0 or later
- Apache Maven (3.0 or higher) / Gradle (5.0 or higher)

### Building the Jar from source code
- Clone/Download [Java SDK for Granica](https://github.com/project-n-oss/projectn-bolt-java) repository
- cd into `projectn-bolt-java` folder
- Run `mvn clean package` 
- After successful completion of last command, you should have the jar in `projectn-bolt-java/target/granica-java-sdk-X.Y.Z.jar`
- Your project `pom.xml` file should point to this local jar file as explained below

### Using Java SDK for Granica

#### Maven
* Import the SDK from local directory into your project by adding the following dependency to your project's POM

```xml
<dependency>
  <groupId>ai.granica</groupId>
  <artifactId>granica-java-sdk</artifactId>
  <scope>system</scope>
  <version>1.0.3</version>
</dependency>
```

### Usage

Java SDK must have knowledge of Granica's *custom domain*.

If java SDK is not able able to figure out the region, it can be configured with an environment variable
An *availability zone ID* can also be provided for AZ-aware routing.

**Configure the Granica custom domain:**
Declare the ENV variable: `GRANICA_CUSTOM_DOMAIN`, which constructs Granica endpoint and hostname based on default naming, and AWS region.
```bash
export GRANICA_CUSTOM_DOMAIN="example.com"
```

**Configure preferred region and availability zone:**

If running on an EC2 instance the SDK will use that instance's region and availability zone by default

If you want a specific region you can set with the environment variable `AWS_REGION`

If you want a specific availability zone you can set it with the a environment variable `AWS_ZONE_ID`.

```bash
export AWS_REGION='<region>'
export AWS_ZONE_ID='<az-id>'
```
### Notes:
- Passthrough is not configurable from SDK
- Java SDK for Granica is built on AWS Java-SDK 2.18.16. Granica client's method signatures are the same as AWS Java SDK's signature. 

