# Political Simulation

Java 8 or newer JDK. No external libraries are required.

Compile and run from this folder on macOS, Linux, or Windows:

```sh
mkdir build
javac -encoding UTF-8 -d build src/*.java
java -cp build App
```

Use **Next Turn** for one step, **Auto Run / Pause** for continuous play, and
**Reset Seed** to reproduce the initial map and default policies. Each faction's
policy can be changed while the simulation runs: Random, Resources, or Compact.
Flags and borders identify owners; a yellow dot marks a developed forest.
Developed forests supply extra expansion actions. Mountains cannot be claimed.
Alliances block mutual conquest until the alliance is broken.

The default seed is 20261031. Identical seeds and policy changes at the same turns
produce the same simulation. Different policies consume random draws differently,
so a shared seed alone is not a controlled statistical comparison of policies.

The default window uses a 50 by 50 map and seven factions. To configure another
run in code, use `new Grid(width, height, gridSize, factionCount, seed)`.
Faction count can be from one to the number of tiles. The three-argument and
four-argument constructors still create three factions, and the fourth argument
in the four-argument constructor remains the random seed.
The default policies repeat Random, Resources and Compact across the factions.

Run the standalone invariant checks (no JUnit dependency):

```sh
javac -encoding UTF-8 -cp build -d build tests/SimulationChecks.java
java -Djava.awt.headless=true -cp build SimulationChecks
```

The project retains the team's terrain, empire and turn system, and integrates
their configurable faction count, larger default map and forest/mountain shapes.
The team's automatic-turn feature runs through a Swing timer on the event dispatch
thread. The terrain drawing helper and art assets come from Haoqian716's existing fork.
Strategy selection, immutable turn snapshots, observers and statistics extend
that baseline. Runtime terrain drawing uses TileArt; PNG assets remain available
in art/ and are not loaded by the current renderer. The shared drawing method in
Land adds ownership flags, borders and the developed-forest marker after each
terrain hook. Forest and mountain coordinates are scaled freshly on every paint.
