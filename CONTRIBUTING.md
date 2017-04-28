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