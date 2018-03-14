Progetto EIGoR ![coverage](https://gitlab.com/tgi-infocert-eigor/eigor/badges/develop/build.svg)
==============

## Eigor Command Line

### Example: Converting a CEN Invoice To FattPa

1. Prepare a CEN file and store it in a `cen.csv`. For instance like that.

```
BG/BT,Value
BT-1,TOSL110
BT-2,10-Apr-13
BT-3,Consignment invoice
BT-5,Danish Krone
BT-9,10-May-13
BG-2,
BT-24,urn:cen.eu:en16931:2017
BG-4,
BT-27,SellerCompany
BT-31,123456789MVA [DK:VAT]
BG-5,
BT-35,Indirizzo obbligatorio
BT-37,comune obbligatorio
BT-38,20100
BT-40,Denmark
BG-7,
BT-44,Buyercompany ltd
BT-48,
BT-49,UFF123
BG-8,
BT-50,Indirizzo obbligatorio
BT-52,comune obbligatorio
BT-53,20100
BT-55,Denmark
BG-22,
BT-106,4000
BT-109,4000
BT-110,675
BT-112,4675
BT-115,4675
BG-23,
BT-116,1500
BT-117,375
BT-118,Standard rate
BT-119,25%
BG-23,
BT-116,2500
BT-117,300
BT-118,Standard rate
BT-119,12%
BG-25,
BT-126,1
BT-129,1000
BT-130,Each
BT-131,1000
BG-29,
BT-146,1
BG-30,
BT-151,Standard rate
BT-152,25%
BG-31,
BT-153,Printing paper
BG-25,
BT-126,2
BT-129,100
BT-130,Each
BT-131,500
BG-29,
BT-146,5
BG-30,
BT-151,Standard rate
BT-152,25%
BG-31,
BT-153,Parker Pen
BG-25,
BT-126,3
BT-129,500
BT-130,Each
BT-131,2500
BG-29,
BT-146,5
BG-30,
BT-151,Standard rate
BT-152,12%
BG-31,
BT-153,American Cookies
```

2. Invokes the converter

    java -jar eigor.jar --input cen.csv --source cen --output ./result --target fattpa

## Eigor API

* Usage information
  * API usage examples in `eigor-api/src/test/java/com/infocert/eigor/api`
  * `converterdata` and example `eigor.properties` are available `eigor-api/target/eigor-api-extras-{version}.zip` after a release profile install.


## Latest Snapshot
* Download the lastest snapshot version of the [Eigor CLI](https://gitlab.com/tgi-infocert-eigor/eigor/-/jobs/artifacts/develop/download?job=eigor-cli-snapshot) command line interface to convert invoices.


## Latest Release
* Download the lastest stable version of the [Eigor CLI](https://gitlab.com/tgi-infocert-eigor/eigor/-/jobs/artifacts/master/download?job=eigor-cli-release) command line interface to convert invoices.
  
## Documentation
[Eigor documentation](https://tgi-infocert-eigor.gitlab.io/eigor/)

## Build

Please check the [build instructions](BUILD.md).

### Run 

Please check the [run instructions](RUN.md).

## Release
1. start a release with gitflow. That places you in a `release/eigor-x.y.z` branch.
2. from the root project executes `./mvnw versions:set` and set the release name: i.e. `x.y.z`
3. run `./mvnw clean install` to check all is working correctly
4. commit all modified files, usually the poms.
5. close the release with gitflow.
6. in local develop executes again `./mvnw versions:set` to set the next `x.y.z+1-SNAPSHOT`
7. run a `./mvnw install` locally to have all project dependencies updated.
8. push master TAG inlcuded!
9. push and develop.
   
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


