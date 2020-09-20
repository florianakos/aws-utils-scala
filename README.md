# ssm-helper-scala

This is a small project I did to learn more about Scala by using the AWS Java SDK v2 to make calls to AWS Cloudformation and AWS Systems Manager.

## Usage

The program can be compiled with `mvn package` and then run via `java -jar ...`. It requires some command line arguments to specify:

* the AWS Systems Manager Automation document name via `--documentname` flag
* the additional parameters needed for the execution of the SSM document

## TODO

...
