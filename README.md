# aws-scala-utils

This project contains a collection of Scala classes that can be used as Helpers to access certain AWS servives such as SSM, Secrets Manager, KMS and CloudFormation. For now this is the full list, I may add more later on.

## Usage

The way this project is set up, it does not contain any runnable classes or apps. Instead if should be imported as a project dependency via maven's pom.xml as below:

```
  <dependencies>
    <dependency>
      <groupId>com.flrnks</groupId>
      <artifactId>aws-utils-scala</artifactId>
      <version>0.0.2</version>
    </dependency>
    ...
  </dependencies>
```

For a full example on how this can be used I refer the reader to my other Git [repository](https://github.com/florianakos/aws-ssm-scala-app) where I use the SSM part to call Automation documents and wait for the execution to finish.

Currently, the version is set to be v0.0.2 so when `mvn install` is called in this repository's root directory, it will install it into local maven cache using that version.

## TODO

* Add more AWS services