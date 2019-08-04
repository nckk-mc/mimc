# Unordered list of things to keep in mind

## Unresolved questions
### What if BungeeCord goes down?

- We lose all player <-> Partition <-> Server instance information
- Do we want to persist this?

### Every world seems to load the spawn chunk, no matter if keep-spawn-loaded is true or not
- MultiverseCore plugin should be disabled as this loads spawn irrespective of the flag
- When the first player connects World.getSpawn() is always retrieved, even if we don't need to select a new spawnspot ( See EntityPlayer.a(WorldServer) )

## Don't forget
### Players can still do /server
- Disable this. Players don't get to choose their node 

