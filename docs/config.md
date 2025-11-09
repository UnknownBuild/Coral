# Coral Configuration Guides

English | [中文](config_zh.md)

> English version is translated by tool.

Coral configuration file is written by Java Properties. If you are not familiar with Properties, we also provides some explanations below. The configuration file for Minecraft server also uses the Properties file.

## About configuration files

### Configuration path

The configuration file name for Coral must be coral.properties, and mod will read the configuration from the following path.

```
coral.properties
config/coral.properties
configs/coral.properties
```

The module requires only one configuration file. If there ere multiple configuration files in the above path, mod will not be able to start. If there are no custom configuration file, mod will use the default configuration.

### Configuration format

The configuration file is written by Java Properties, in the following format.

```properties
# Comments starting with sign '#', it can be used to write some text for recording or prompting purposes.
# All configurations exist in the form of <key>=<value>, where key is the configuration item and value is the value, for example
game=Minecraft
player.id=1
player.is_online=true
player.name=MegaShow
```

### Configuration References

Coral provides two preset configurations: default configuration and empty configuration.

- default: default configuration, enable most of Coral features.
- empty: empty configurations, disable all Coral features.

When using Coral, you can specify reference configurations through use keyword, and then add individual features according to your own needs.

```properties
use=empty
feature.xxx=true
```

### Translation and style

Most of Coral features require to display text information to players, and you can modify the language of text through configuration file.

```properties
language=en_us
```

Currently, Coral supports Simplified Chinese and English, default with English. 

|  Lanugage  |  Code   |
|:----:|:-----:|
| Simplified Chinese | zh_cn |
|  English  | en_us |

If you are not satisfied with the language support, you can customize the language file.

```properties
language=zh_cn
language.path=config/lang.json
```

The above configuration will prioritize reading text information from config/lang.json. If it does not exist in the file, the default text will be read. Coral's default language file can be found in [sources](https://github.com/UnknownBuild/Coral/tree/master/src/main/resources/assets/coral/lang).

Coral uses the same style schema for different languages. If you are not satisfied with the default style, you can customize the style file.

```properties
style.path=config/style.json
```

Coral's default style file can be found in [sources](https://github.com/UnknownBuild/Coral/tree/master/src/main/resources/assets/coral/style.json).

## Command supports

### here

I am here. Give players their own highlight effect and broadcast their location to the world.

```
/here
```

This command only works on dedicated server or LAN mode, not support for single player mode.

| Configuration Item | Type | Default Value | Description |
|-----------------------|-----|------|-----------|
| command.here          | boolean | true | enable this command |
| command.here.duration | integer  | 30 | higlight effect duration (seconds) |

### player

Enhances player command, provides richer querying features.

After enables this feature, the server will create file coral_player.data in folder world/data. This file is used to store player online information and is only used to query command. Even if the file is deleted, it will not affect gaming.

```
/player list       # List current online players, same as /list uuids, but supports clicks to copy uuid
/player listall    # List all player on this server, some player's names will not be display
```

listall command requires the executing player to have level 3 or higher permissions.

| Configuration Item | Type | Default Value | Description |
|----------------|-----|------|---------|
| command.player | boolean | true | enable this command |

### wru

Where are you? Ask about player's location.

```
/wru <player>
```

This command only works on dedicated server or LAN mode, not support for single player mode.

| Configuration Item | Type | Default Value | Description |
|-------------|-----|------|---------|
| command.wru | boolean | true | enable this command |

## Feature supports

### call_sleep

Sleep now. When a player enters sleep status, broadcast a sleep request to the world to prompt other players to sleep.

This command only works on dedicated server or LAN mode, not support for single player mode.

| Configuration Item | Type | Default Value | Description |
|--------------------|-----|------|---------|
| feature.call_sleep | boolean | true | enable this feature |

### death_info

Come and pick up your things. When a player dies, broadcast the death location to the world.

This command works on all modes.

| Configuration Item | Type | Default Value | Description |
|--------------------|-----|------|---------|
| feature.death_info | boolean | true | enable this feature |
