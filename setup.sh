#!/bin/bash
if [ -d "Paper" ]; then
	echo "Paper directory exists. For a clean setup remove Paper and Waterfall folder."
	exit -1
fi
if [ -d "Waterfall" ]; then
	echo "Waterfall directory exists. For a clean setup remove Paper and Waterfall folder."
	exit -1
fi

echo "MIMC | Cloning paper..."
echo ""

git clone https://github.com/PaperMC/Paper.git
cd Paper
git checkout ver/1.14

echo "MIMC | Building..."
echo ""
./paper jar

echo "MIMC | Running test server. Type stop after it has succesfully started..."
echo ""
mkdir -p work/test-server/plugins
cp ../paper-plugins/mimc.migrate.jar work/test-server/plugins/
echo "eula=true" > work/test-server/eula.txt
./paper t 

echo "MIMC | Configuring Paper"
echo ""

cd work/test-server
echo level-seed=1 >> server.properties
cat server.properties | grep -v online-mode > server.properties
echo online-mode=false >> server.properties
sed -i 's/keep-spawn-loaded: true/keep-spawn-loaded: false/g' paper.yml
sed -i 's/bungeecord: false/bungeecord: true/g' spigot.yml

rm plugins/Essentials*.jar
rm plugins/Multiverse*.jar # this loads spawn even though keep-spawn-loaded is set to false

cd ../..

echo "Applying paper-patches"
cd Paper-Server
for patch in ../../paper-patches/*.patch; do git apply $patch; done
cd ..

echo "Applying paper-api-patches"
cd Paper-Api
for patch in ../../paper-api-patches/*.patch; do git apply $patch; done
cd ..

echo "Rebuilding..."
mvn
cd ..


echo "MIMC | Cloning waterfall..."
echo ""

git clone https://github.com/PaperMC/Waterfall.git

echo "MIMC | Building..."
echo ""
cd waterfall
./waterfall build

echo "Applying waterfall-patches"
cd Waterfall-Proxy
for patch in ../../waterfall-patches/*.patch; do git apply $patch; done
cd ..
	
echo "MIMC | Building..."
echo ""
mvn
	
echo "MIMC | Running waterfall test server to generate config.yml. Ctrl+C after it has started."
mkdir work/
cd work/
java -jar ../Waterfall-Proxy/bootstrap/target/Waterfall.jar

echo "MIMC | Configuring Waterfall"
echo ""

sed -i 's/lobby/spawn/g' config.yml
sed -i 's/Just another Waterfall - Forced Host/Spawn node/g' config.yml
sed -i 's/restricted: false/restricted: false\n  child:\n    motd: \x27Child node\x27\n    address: localhost:25566\n    restricted: false/g' config.yml
sed -i 's/max_players: 1/max_players: 10000/g' config.yml
sed -i 's/online_mode: true/world_folder: ..\/..\/Paper\/work\/test-server\/\nonline_mode: true/g' config.yml
sed -i 's/ip_forward: false/ip_forward: true/g' config.yml

cd..