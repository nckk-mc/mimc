# Unordered list of things to keep in mind

## Open issues
### What if BungeeCord goes down?
- We lose all player <-> Partition <-> Server instance information
- Do we want to persist this?

### Players can still do /server
- Disable this. Players don't get to choose their node 

### Every world seems to load the spawn chunk, no matter if keep-spawn-loaded is true or not
- MultiverseCore plugin should be disabled as this loads spawn irrespective of the flag
- When the first player connects World.getSpawn() is always retrieved, even if we don't need to select a new spawnspot ( See EntityPlayer.a(WorldServer) )

### Teleporting should be coordinated by the proxy
- Teleporting a player to a target location should never be decided by a node rather by the proxy
- When teleporting, a message should be sent to the proxy requesting a teleporting
- The proxy figures out which node the player needs to be sent to
- It responds with either
	1. You can teleport because the coordinate is local to your node
	2. You can teleport AFTER setting the target region files read-only
- Then the normal migration mechanism takes over

### The proxy could keep track of loaded region files
- If we have the nodes communicate their loaded/unloaded region files back to the proxy it could make sure we never load the same region files on two nodes.

### There are a number of uses of `worldserver.getSpawn()`
- Identify them, and neutralize or disable
- We cannot have two servers loading the same chunks


### Support for /locate requires us to increase MERGE_DISTANCE
- see https://www.spigotmc.org/threads/how-to-find-closest-generated-structure.338532/
- /locate has a 100 chunk limit (1.6k blocks) which, when allowed, requires us to increase MERGE_DISTANCE to 4k
- Perhaps we could make support for this opt-in, increasing the minimal MERGE_DISTANCE

