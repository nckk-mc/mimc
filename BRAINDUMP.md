# Unordered list of things to keep in mind

## Unresolved questions
### What if BungeeCord goes down?

- We loose all player <-> Partition <-> Server instance information
- Do we want to persist this?

### What if a user disconnects?
- Prune partitions if necessary

## Don't forget
### Players can still do /server
- Disable this. Players don't get to choose their node 