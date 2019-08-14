#!/usr/bin/env bash
set -e

if [ -d "Paper" ]; then
	echo "MIMC | Paper directory exists. For a clean setup remove Paper and Waterfall folder."
	exit -1
fi
if [ -d "Waterfall" ]; then
	echo "MIMC | Waterfall directory exists. For a clean setup remove Paper and Waterfall folder."
	exit -1
fi

echo "MIMC | Cloning paper..."
echo ""

git clone https://github.com/PaperMC/Paper.git
cd Paper
git checkout ver/1.14

echo "MIMC | Building paper..."
echo ""
./paper jar

echo "MIMC | Building paper plugins..."
echo ""
cd ../paper-plugins/info
mvn

cd ../../Paper

echo "MIMC | Copying over Paper configuration defaults..."
echo ""
mkdir -p work/test-server/plugins
cp ../paper-plugins/info/target/info-1.0-SNAPSHOT.jar work/test-server/plugins/
cp ../paper-plugins/migrate/mimc.migrate.jar work/test-server/plugins/
cp ../config/Paper/* work/test-server/

echo "MIMC | Applying paper-patches"
cd Paper-Server
for patch in ../../paper-patches/*.patch; do git apply --ignore-space-change --ignore-whitespace $patch; done
cd ..

echo "MIMC | Applying paper-api-patches"
cd Paper-API
for patch in ../../paper-api-patches/*.patch; do git apply --ignore-space-change --ignore-whitespace $patch; done
cd ..

echo "MIMC | Rebuilding Paper..."
mvn
cd ..


echo "MIMC | Cloning Waterfall..."
echo ""

git clone https://github.com/PaperMC/Waterfall.git

echo "MIMC | Building Waterfall..."
echo ""
cd Waterfall
./waterfall build

echo "MIMC | Applying waterfall-patches"
cd Waterfall-Proxy
for patch in ../../waterfall-patches/*.patch; do git apply --ignore-space-change --ignore-whitespace $patch; done
cd ..
	
echo "MIMC | Rebuilding Waterfall..."
echo ""
mvn
	
mkdir -p work
cp ../config/Waterfall/* work/

cd ..


echo "MIMC | Running Paper test server copy over plugins. Ctrl+C after it has started."

./start_node_spawn.sh
