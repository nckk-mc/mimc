    # 1. Build PaperMC
    git clone https://github.com/PaperMC/Paper.git
    cd Paper
    ./paper build
    
	# 2. Run a testserver so the appropriate folders are created
	./paper t
	
	# 3. Customize configuration for use in multi-instance mode
	cd work/test-server
	
	# In server.properties:
	# Set a fixed level seed (so all instances use the same seed)
	echo level-seed=1 >> server.properties
	
	# Set online-mode to false so it can run behind BungeeCord
	cat server.properties | grep -v online-mode > server.properties
	echo online-mode=false >> server.properties
	
	# In paper.yml:
	# Set keep-spawn-loaded to false 
	sed -i 's/keep-spawn-loaded: true/keep-spawn-loaded: false/g' paper.yml
	
	# 4. Disable the concurrency check in Paper
	# In WorldNBTStorage.java disable the session lock that prevents multiple instances
	# from accessing the same world concurrently
	cd ../../Paper-Server/src/main/java/net/minecraft/server
	
	# Find checkSession() in WorldNBTStorage.java and just early out
	sed -i 's/throws ExceptionWorldConflict {/throws ExceptionWorldConflict {\n        return;/g' WorldNBTStorage.java

	# Back to the Paper root
    cd ../../../../../..
	
	# 5. rebuild
	mvn
	
	# 6. Run two instances
	cd work/test-server
	java -jar paper-1.14.4.jar -p 25565
	java -jar paper-1.14.4.jar -p 25566
	
	# 7. Build Waterfall
    cd ../../..
	git clone https://github.com/PaperMC/Waterfall.git
	cd waterfall
	./waterfall build
		
	# 8. Set up testserver
	mkdir work/
	cd work/
	java -jar ../Waterfall-Proxy/bootstrap/target/Waterfall.jar
	
	#9. Configure waterfall with two instances (spawn and child)
	sed -i 's/lobby/spawn/g' config.yml
	sed -i 's/Just another Waterfall - Forced Host/Spawn node/g' config.yml
	sed -i 's/restricted: false/restricted: false\n  child:\n    motd: \x27Child node\x27\n    address: localhost:25566\n    restricted: false/g' config.yml
	sed -i 's/max_players: 1/max_players: 10000/g' config.yml

	