# Java SDK for Bolt

This Java Solution provides Java Client Library for Bolt.

It can be built using any of the standard Java IDEs (including IntelliJ IDEA and Eclipse IDE for Java Developers)
using the included project files.

## Installation

### Requirements

- Java 8.0 or later
- Apache Maven (3.0 or higher) / Gradle (5.0 or higher)

### Build From Source

#### Maven
* Maven is the recommended way to build and consume Java SDK for Bolt.

* Download the source, build and install the package (jar) into the local repository:

```bash
git clone https://gitlab.com/projectn-oss/projectn-bolt-java.git
cd projectn-bolt-java
mvn clean install
```

#### Gradle
* Download the source, build and create a local package(jar):

```bash
git clone https://gitlab.com/projectn-oss/projectn-bolt-java.git
cd projectn-bolt-java
gradle uberJar
```

### Using Java SDK for Bolt

#### Maven
* Import the SDK into your project by adding the following dependency to your project's POM:

```xml
<dependency>
    <groupId>com.projectn.bolt</groupId>
    <artifactId>projectn-bolt-java</artifactId>
    <version>1.0</version>
</dependency>
```

#### Gradle
* Add the local package as dependency to your project's `build.gradle` file:
  * In the following case, your project and source have been cloned in the same root directory

```groovy
implementation files('../projectn-bolt-java/build/libs/bolt-java-sdk-1.0.jar')
```


### Usage

Please refer [ProjectN Bolt Java Sample](https://gitlab.com/projectn-oss/projectn-bolt-java-sample) for a sample AWS Lambda Application in Java that utilizes Java SDK for Bolt