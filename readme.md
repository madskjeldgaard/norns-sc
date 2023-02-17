# README

The SuperCollider architecture for [Monome Norns' ecosystem](https://github.com/monome/norns), ripped out of there to here to have an easy to use environment for testing audio engines before adding lua-scripts and working on the norns device itself.

The motivation for this was just to have a more productive workflow around writing and designing programs for the norns. The thought is to do as much prototyping as possible on your computer before transferring it to the norns device and/or starting on the lua side of thing.

## Installation

```supercollider
Quarks.install("https://github.com/madskjeldgaard/norns-sc");
```

## Usage
```supercollider
Crone.run(); // Start Crone manually
Crone.setEngine("Engine_TestSine"); // Set new engine

// This emulates how lua sets a parameter in the engine:
Crone.engine.setCommand('hz', 200.5);
```
