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
	
	# 4. Apply patches
	cd ../../Paper-Server
	git apply paper-patches/01-first-steps.patch

	# Back to the Paper root
    cd ../
	
	# 5. rebuild
	mvn
	
	# 6. Run two instances
	cd work/test-server
	java -jar paper-1.14.4.jar -p 25565
	java -jar paper-1.14.4.jar -p 25566
	
	# 7. Clone waterfall and apply patches
    cd ../../..
	git clone https://github.com/PaperMC/Waterfall.git
	cd waterfall/Waterfall-Proxy
	git apply waterfall-patches/01-first-steps.patch
	cd ../
		
	# 8. Build
	./waterfall build
		
	# 9. Set up testserver
	mkdir work/
	cd work/
	java -jar ../Waterfall-Proxy/bootstrap/target/Waterfall.jar
	
	# 10. Configure waterfall with two instances (spawn and child)
	sed -i 's/lobby/spawn/g' config.yml
	sed -i 's/Just another Waterfall - Forced Host/Spawn node/g' config.yml
	sed -i 's/restricted: false/restricted: false\n  child:\n    motd: \x27Child node\x27\n    address: localhost:25566\n    restricted: false/g' config.yml
	sed -i 's/max_players: 1/max_players: 10000/g' config.yml

	