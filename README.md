Progetto EIGoR ![coverage](https://gitlab.com/tgi-infocert-eigor/eigor/badges/develop/build.svg)
==============

## Build
Java 8 is needed. To build the project:

    mvn clean install
    
If it completes correctly, you'll find the CLI executable jar here...
    
    eigor-cli/target/eigor.jar
    
Execute it with `java -jar eigor.jar` to run it.

## Release
1. start a release with gitflow. That places you in a `release/eigor-x.y.z` branch.
2. from the root project executes `mvn versions:set` and set the release name: i.e. `x.y.z`
3. run `mvn clean install` to check all is working correctly
4. commit all modified files, usually the poms.
5. close the release with gitflow.
6. in local develop executes again `mvn versions:set` to set the next `x.y.z+1-SNAPSHOT`
7. run a `mvn install` locally to have all project dependencies updated.
8. push master and develop, TAGs inlcuded!
   
## Contributing
* Make your changes in a new git branch. 
* Please be aware that we are following the 
[git flow branching model](http://nvie.com/posts/a-successful-git-branching-model/) 
with `develop` as the branch for the next version:


    git checkout -b feature/my-shiny-contribution develop
    
* Other info:
  * Branch name for production releases: `master` 
  * Branch name for "next release" development: `develop` 
  * Feature branches? `feature/` 
  * Bugfix branches? `bugfix/` 
  * Release branches? `release/` 
  * Hotfix branches? `hotfix/` 
  * Support branches? `support/` 
  * Version tag prefix? : `eigor-` 
  
* [git flow cheatsheet](https://danielkummer.github.io/git-flow-cheatsheet/)  

### WARNING
Since `develop` is now a protected branch, all merges to it must be done through GitLab by opening a 
[merge request](https://gitlab.com/tgi-infocert-eigor/eigor/merge_requests/new).  
**DO NOT FINISH FEATURES THROUGH GIT FLOW.** If feature branches are closed by calling
`git flow feature finish` it will automatically merge into `develop` and remove the remote feature branch.   
Always use the merge request feature in GitLab and then delete your local feature branch after merge
has been approved.


