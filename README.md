# Java SDK for Bolt

This Java Solution provides Java Client Library for Bolt.

It can be built using any of the standard Java IDEs (including IntelliJ IDEA and Eclipse IDE for Java Developers)
using the included project files.

## Installation

### Requirements

- Java 8.0 or later
- Apache Maven (3.0 or higher)

### Build From Source

#### Maven
* Maven is the recommended way to build and consume Java SDK for Bolt.

* Download the source, build and install the package (jar) into the local repository:

```bash
git clone https://gitlab.com/projectn-oss/projectn-bolt-java.git
cd projectn-bolt-java
mvn clean install
```

### Using Java SDK for Bolt

* Import the SDK into your project by adding the following dependency to your project's POM:

```xml
<dependency>
    <groupId>com.projectn.bolt</groupId>
    <artifactId>projectn-bolt-java</artifactId>
    <version>1.0</version>
</dependency>
```

### Usage

Please refer [ProjectN Bolt Java Sample](https://gitlab.com/projectn-oss/projectn-bolt-java-sample) for a sample AWS Lambda Application in Java that utilizes Java SDK for Bolt