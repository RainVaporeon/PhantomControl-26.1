# PhantomControl

### Building

Java 25 or higher is required for compilation.

In order to compile the code to a Paper 26.1.2 plugin, run:

`mvn clean package`

### Usage

Run `/phantom global` to toggle global spawning. (Anyone sleeping resets the insomnia)

Run `/phantom time <days>` to set insomnia days before phantoms are allowed to spawn.

Run `/phantom query <params>` to query world info

Valid parameters:

`!<world>` - (Tab filled) Get world info

`@all` - Get every world and player info

otherwise - Get player info