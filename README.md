# Decaf compiler

dcc implementation

## Requirements
### Install Java SDK 11
- Use [sdkman](http://sdkman.io/)
```sh
$ curl -s "https://get.sdkman.io" | bash
$ source "$HOME/.sdkman/bin/sdkman-init.sh"
$ sdk version
$ sdk install java
```

### Install Gradle 5.3 or higher
```sh
$ sdk update
$ sdk install gradle
```

## Usage
```sh
$ git clone git@github.com:jasoet/kotlin-gradle-boilerplate.git ${YOUR_PROJECT_NAME}
$ cd ${YOUR_PROJECT_NAME}
$ ./gradlew clean build
```

## Generate antlr4 parser and lexer
```sh
$ ./gradlew generateGrammarSource
```

