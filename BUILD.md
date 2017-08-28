Eigor Build Guide
=================

Prerequisites
-------------

### JDK 7
Please be sure to have a _Java Development Kit 7_ properly installed.
_Eigor_ won't compile with other versions (i.e. _JDK 8_).

Since _JDK 7_ can be downloaded from the [Oracle archive](http://www.oracle.com/technetwork/java/javase/downloads/java-archive-downloads-javase7-521261.html). 

### Internet Connection
The machine where _Eigor_ will be built needs to have access to the [Maven Central Repository](https://search.maven.org/) to download dependencies when needed.
 
### GIT
_Git_ should be properly installed to be able to check out the source code from _GitLab_. You can download _Git_
 from [_Git_ web site](https://git-scm.com/downloads).

Build
------------

### JDK 7 Toolchain Set Up

***THIS STEP IS MANDATORY EVEN IF THE JDK 7 IS YOUR ONLY INSTALLED JDK***

Create the `.m2` folder in your home directory if not yet available.

*Unix (MacOS, Linux, BSD...)*

    mkdir -p $HOME/.m2/

*Windows CMD*

    mkdir %Homedrive%%Homepath%\.m2

Create a `toolchains.xml` file in the `.m2` folder you've just created.
Fill the file with the following content replacing the `{path to your JAVA_HOME}` placeholder
 with the full path to the _JDK 7_ available on the machine.


    <toolchains>
        <toolchain>
            <type>jdk</type>
            <provides>
                <version>1.7.0</version>
                <vendor>Oracle</vendor>
                <id>jdk-1.7.0</id>
            </provides>
            <configuration>
                <jdkHome>{path to your JAVA_HOME}</jdkHome>
            </configuration>
        </toolchain>
    </toolchains>


### Clone the Sources

Clone the source repository in your preferred folder.

```
git clone https://gitlab.com/tgi-infocert-eigor/eigor.git
```

### Build
The project uses Maven as its build system. A convenient wrapper is provided to avoid the need
to install Maven on each system. It accepts all the normal Maven commands and uses the standard configuration
files and folders (`.m2/`, `settings.xml`, `toolchains.xml` etc...).

You can build the project with the following instructions:
   
*Unix (MacOS, Linux, BSD...)*

    ./mvnw clean install -Prelease
    
*Windows Powershell*
    
    ./mvnw.cmd clean install -Prelease
    
*Windows CMD*
    
    mvnw clean install -Prelease
    