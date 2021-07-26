# Java SDK for Bolt

This Java Solution provides Java Client Library for Bolt.

It can be built using any of the standard Java IDEs (including IntelliJ IDEA and Eclipse IDE for Java Developers)
using the included project files.

## Installation

### Requirements

- Java 8.0 or later
- Apache Maven (3.0 or higher) / Gradle (5.0 or higher)

### Using Java SDK for Bolt

#### Maven
* Import the SDK into your project by adding the following dependency to your project's POM:

```xml
<dependency>
  <groupId>com.gitlab.projectn-oss</groupId>
  <artifactId>projectn-bolt-aws-java</artifactId>
  <version>1.0.0</version>
</dependency>
```

#### Gradle
* Import the SDK into your project by adding the following dependency to your project's `build.gradle` file:

```groovy
implementation 'com.gitlab.projectn-oss:projectn-bolt-aws-java:1.0.0'
```

### Usage

Please refer [ProjectN Bolt Java Sample](https://gitlab.com/projectn-oss/projectn-bolt-java-sample) for a sample AWS Lambda Application in Java that utilizes Java SDK for Bolt