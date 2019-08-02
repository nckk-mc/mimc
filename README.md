    # 1. Build PaperMC
    git clone https://github.com/PaperMC/Paper.git
    cd Paper
	git checkout ver/1.14
    ./paper jar
    
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
	
	# In spigot.yml:
	# Running behind bungeecord so we can do ip forwarding
	sed -i 's/bungeecord: false/bungeecord: true/g' spigot.yml
	
	# 4. Apply patches
	cd ../../Paper-Server
	for patch in paper-patches/*.patch; do
		git apply $patch
	done

	# Back to the Paper root
    cd ../
	
	# 5. rebuild
	mvn
	
	# 6. Run two instances
	cd work/test-server
	java -jar ../../Paper-Server/target/paper-1.14.4.jar -p 25565
	java -jar ../../Paper-Server/target/paper-1.14.4.jar -p 25566
	
	# 7. Clone waterfall and apply patches
    cd ../../..
	git clone https://github.com/PaperMC/Waterfall.git
	./waterfall build
	cd waterfall/Waterfall-Proxy
	for patch in ../../waterfall-patches/*.patch; do
		git apply $patch
	done
	cd ../
		
	# 8. Build
	mvn
		
	# 9. Set up testserver
	mkdir work/
	cd work/
	java -jar ../Waterfall-Proxy/bootstrap/target/Waterfall.jar
	
	# 10. Configure waterfall with two instances (spawn and child)
	sed -i 's/lobby/spawn/g' config.yml
	sed -i 's/Just another Waterfall - Forced Host/Spawn node/g' config.yml
	sed -i 's/restricted: false/restricted: false\n  child:\n    motd: \x27Child node\x27\n    address: localhost:25566\n    restricted: false/g' config.yml
	sed -i 's/max_players: 1/max_players: 10000/g' config.yml
	sed -i 's/online_mode: true/world_folder: ..\/..\/Paper\/work\/test-server\/\nonline_mode: true/g' config.yml
	sed -i 's/ip_forward: false/ip_forward: true/g' config.yml
	
	# 11. Current test setup
	# - Start up waterfall
	# - Start up spawn node @ port 25565
	# - Connect first player and teleport to 0,0
	# - Connect second player and teleport to 2000,0
	# - Shut down both servers
	# - Restart waterfall
	# - Start spawn node @ port 25565 and s1 node @ port 25566
	# - Reconnect your first player (He should be redirected to the spawn node)
	# - Reconnect your second player (He should be redirect to the s1 node)
	# - And now for the magic: teleport the second player to 10,0
	# - You should see bungeecord redirect him to the spawn node