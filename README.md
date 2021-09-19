# WorldUtils

This Minecraft Spigot plugin contains various tools, namely for managing both public and personal in-game positions,
sending your own position, setting up a timer, resetting the server and managing some plugin settings.

## Table of Contents

toc

## Installation

To install the plugin, simply place the .jar file in the folder "plugins" of your server. The server must run at least
Spigot, Paper is also fine (CraftBukkit does not work). On the next server (re)start, the plugin should be initialized
and can be run correctly.

## Usage

To use the plugin, enter an appropriate command. All commands have tab completion (except when accessing personal
positions of other players), so you do not need to know the exact syntax when typing.

The commands and their respective syntax are:

- ```/position <name> | (<option> [<name>])```
- ```/personalposition <name> | (<option> [<name>])```
- ```/sendposition <playername>```
- ```/timer (join | leave | show | hide | start | stop | reverse | reset | (set | add) [[[<d>] <h>] <min>] <s>)```
- ```/reset [confirm]```
- ```/settings <commandname> <setting> (true | false)```

Some of these commands also have aliases that can be used instead of the full command names and allow for faster command
input. These aliases are:

- ```position```: ```pos```
- ```personalposition```: ```personalpos```, ```perspos```, ```ppos```
- ```sendposition```: ```sendpos```, ```spos```
- ```settings```: ```stg```

## Contributing

issue

## Credits

creds

## License

mit
