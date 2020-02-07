### AMEXAssignment
To run Application Java 13 is required.

There is two options how to run Application:
build and run as Java self executable container or using gradle.

Using gradle:
```shell script
gradle run
```

Using standalone Jar

1. build application using command:
```shell script
gradle clean build
```

2. run application
2.1. run application with mocked endpoints
```shell script
java -jar build/libs/AMEX-0.0.1-SNAPSHOT.jar -Dspring.profiles.active=demo
```
2.2. run application without mocked endpoints
```shell script
java -jar build/libs/AMEX-0.0.1-SNAPSHOT.jar
```

# Mocked value
1. Tax number
1.1. 123 -> score 800
1.2. 321 -> score 700
1.3. any other -> score 0

2. Address
All addresses are invalid except one
```json
{
    "address": "Test",
    "address2": "Test",
    "city": "Test",
    "state": "Test",
    "zipCode": "Test"
}
```

