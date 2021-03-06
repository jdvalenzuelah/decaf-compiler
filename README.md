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


## CLI usage
```sh
$ git clone git@github.com:jdvalenzuelah/decaf-compiler.git ${YOUR_PROJECT_NAME}
$ cd ${YOUR_PROJECT_NAME}
$ ./gradlew clean :dcc-cli:build
```

Extract the dist
```sh
$ tar -xf dcc-cli/build/distributions/dcc-cli-1.0.0.tar
```

Run
```sh
$ ./dcc-cli-1.0.0/bin/dcc-cli -h
Usage: dcc [OPTIONS] FILE

Options:
  -E, --parser-only        Runs just the parser
  -dt, --dump-tree         Prints parse tree in unix's tree utility style
  -ds, --dump-symbols      Prints symbol table in plain text
  -dtt, --dump-types       Prints struct table in plain text
  -ir, --dump-ir           Dump intermediate representation to file
  --target [JASMIN|JAVA8]  Generate code for the given target
  -o, --output-dir PATH    Directory to output result
  -h, --help               Show this message and exit

Arguments:
  FILE  file to compile
```