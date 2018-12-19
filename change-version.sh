
./mvnw versions:set -DnewVersion=$1
cd ./eigor-parent
../mvnw versions:set -DnewVersion=$1
