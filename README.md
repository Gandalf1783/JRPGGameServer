# JRPGGameServer

This is a server for the https://github.com/Gandalf1783/JRPG Game.

You can download and compile this.

Any help in terms of coding is appreciated. Create issues if you find any.

Attention:
This uses a Database where UUID's are saved and the SQL-Table Structure is not yet on github in this repo.

Libraries you have to use:
KryoNet-2.21-ALL

JANSI-1.17.1

JLINE-3.16.0

JLINE-TERMINAL-JNA-3.20.0

JNA-4.5.1

MYSQL-CONNECTOR-5.1.49

QuadTreeLibrary (Mine, you can find the releases on my repo)

---

### Known Bugs:
- Not a bug, but seed has to be determined when no world is loaded randomly.

### Things to Work on:
- Security-Features during Login-Process
- API for confirming Users Identity

### Fixed:
- Chunkgeneration now correctly works.
- Chunkgeneration always creates the whole map, rather than just a part of it. Will take exponentially more time with a larger world.
- Command world createimage does not always reflect the current world with the current seed.
- Sending all Players to a new Player is not done correctly! (This Bug could reappear!)
    
