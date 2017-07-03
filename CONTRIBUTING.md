## Contributing

### GitFlow

* Please be aware that we are following the 
[git flow branching model](http://nvie.com/posts/a-successful-git-branching-model/)   

* We use the gitflow model with this setup:
  * Branch name for production releases: `master` 
  * Branch name for "next release" development: `develop` 
  * Feature branches: `feature/` 
  * Bugfix branches: `bugfix/` 
  * Release branches: `release/` 
  * Hotfix branches: `hotfix/` 
  * Support branches: `support/` 
  * Version tag prefix: `eigor-`
 
### Development Environment
This project requires Maven 3 and Java 7. To avoid messing around with `JAVA_HOME` or break other project, you must set a Maven Toolchain that makes use of the right JDK.
Create a `toolchains.xml` file in your `$HOME/.m2/` folder and copy the following snippet:

```
<toolchains>
    <toolchain>
        <type>jdk</type>
        <provides>
            <version>1.7.0</version>
            <vendor>YOUR_VENDOR (openjdk or oracle)</vendor>
            <id>jdk-1.7.0</id>
        </provides>
        <configuration>
            <jdkHome>/path/to/your/java/home</jdkHome>
        </configuration>
    </toolchain>
</toolchains>
  ```

***THIS STEP IS MANDATORY EVEN IF THE JDK 7 IS YOUR ONLY INSTALLED JDK***

#### Maven Wrapper
  
**You don't need to have Maven installed**   
The project provides an embedded Maven executable that you
can invoke using the Maven wrapper. It accepts all the normal Maven commands and uses the standard configuration
                                    files and folders (`.m2/`, `settings.xml`, `toolchains.xml` etc...).

You can invoke it in the following ways:   
*Unix (MacOS, Linux, BSD...)*

    ./mvnw
    
*Windows Powershell*
    
    ./mvnw.cmd
    
*Windows CMD*
    
    mvnw
    
*For example*:

    ./mvnw clean install
    
### Contribution

1) Start a _feature branch_ from _develop_.
2) If the features is related to an issue, please, in one of the comments, 
remember to include a reference to the issue in the form `#<issue_id>`.
3) _Push_ your branch feature to GitLab
4) Open a _[merge request](https://gitlab.com/tgi-infocert-eigor/eigor/merge_requests/new)_ to merge your branch to _develop_.
5) Assign the _merge request_ for review to one member of the group that can actually perform the merge.

__warning__
Please note that a MR has to fulfill some requirements to be merged.
* The test pipeline should pass.
* All the discussions related to the MR should be closed.
* There should not be conflicts with _develop_. 
* If possible MR will be served when they can be merged, older first.

__warning__
* Since `develop` on GL is protected branch, all merges to it must be done through GitLab by opening a 
[merge request](https://gitlab.com/tgi-infocert-eigor/eigor/merge_requests/new).  
* __DO NOT FINISH FEATURES THROUGH GIT FLOW.__ If feature branches are closed by calling
`git flow feature finish` it will automatically merge into `develop` and remove the remote feature branch.   
* Always use the merge request feature in GitLab and then delete your local feature branch after merge
has been approved.