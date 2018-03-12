## Schematron XSLT files update

Schematron XSLT regeneration can be done in several ways.

### For users

Schematron files can be edited, and their corresponding xslt be updated when eigor-cli or eigor-api is run.

#### Schematron XSLT regeneration for eigor-cli

* can be enabled in eigor.properties file using the parameter _auto-update-xslt_ that is available per converter
* example 'eigor.converter.ubl-cen.schematron.auto-update-xslt=false'
* setting _auto-update-xslt_=_true_ will enable timpestamp check for each sch-xslt file pair, and trigger necessary regeneration at runtime

#### Schematron XSLT regeneration for eigor-api

* same as eigor-cli. Adjust classpath eigor.properties parameter _auto-update-xslt_

### For developers

#### maven profiles

* **coverage** - no action
* **dev** - will only trigger regeneration if corresponding xslt file is missing (deleted) -- timpestamps are still unreliable from zip files (looking at windblows here), but we can try to enable updateOnSchematronChanges experimentally if needed
* **release** - used for eigor-cli-snapshot (branch develop) and tests - will not trigger regeneration, existing xslts will be packed even if they are behind sch files
* **releasexslt** - used for eigor-cli-release (branch master) - force xslt regeneration to ensure they are up to date so that the user will not have to wait on first run

#### ph-sch2xslt-maven-plugin
The following parameters can be used in pom.xml for this plugin to affect XSLT updating

* **overwriteWithoutQuestion** - regeneration will trigger regardless and any existing xslt will be overwritten; default false
* **updateOnSchematronChanges** - regeneration of xslt will trigger if corresponding sch file has a newer timpestamp; default false

**Note**: without one of these 2 params true, regeneration will trigger only for xslt files that are missing

#### Updating schematron files

If .sch files have changed or have been replace with a newer version, the corresponding xslt files also need updating.

To update sch files and their corresponding xslt files:

* update schematron file(s)
* delete corresponding xslt file(s)
* run mvn clean install -Pdev (just for the converter module affected)
* commit updated xslt file(s)

