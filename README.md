# EeISI

Quick
[Getting started](src/site/markdown/getting-started.md)

## Build

Java 8, Maven and a working Internet connection are needed.
Build with:

    mvn install -Prelease

## Documentation

Would you like to read the documentation?
Just generate it from the code with:

    mvn site site:stage
    
Then point your browser to `target/staging/index.html`.

