$version = $args[0]
.\mvnw versions:set -DnewVersion=$version
cd .\eigor-parent
..\mvnw versions:set -DnewVersion=$version
cd ..
