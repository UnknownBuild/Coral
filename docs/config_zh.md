# Coral 配置指南

[English](config.md) | 中文

Coral 的配置文件使用 Java Properties 配置约定编写，如果你不了解 Properties，下面我们也对此做了一些说明。Minecraft 服务器的配置文件也是采用 Properties 文件。

## 关于配置文件

### 配置路径

Coral 的配置文件名字必定为 coral.properties，模组会从以下路径读取配置。

```
coral.properties
config/coral.properties
configs/coral.properties
```

模组要求仅读取一份配置文件，如果以上路径同时存在多份配置文件时，模组将无法启动。如果不存在任何自定义的配置文件，模组将使用默认配置。

### 配置格式

配置文件使用 Java Properties 的配置约定编写，格式如下。

```properties
# 以井号开头的为注释, 可随便写一些用于记录或提示的文本
# 所有配置以 <key>=<value> 的形式存在, key 为配置项, value 为值, 比如
game=Minecraft
player.id=1
player.is_online=true
player.name=MegaShow
```

### 配置引用

Coral 提供了默认配置和空配置两个预设配置。

- default：默认配置，启用大部分 Coral 的特性；
- empty：空配置，禁用所有 Coral 的特性。

在使用 Coral 的时候可以通过 use 配置项指定引用相关配置，然后再根据自己的需要添加启用个别特性。

```properties
use=empty
feature.xxx=true
```

### 翻译与样式

Coral 大多数功能需要向玩家们显示文本信息，通过配置文件可以修改文本信息的语言。

```properties
language=zh_cn
```

目前 Coral 支持简体中文和英文，默认为英文。

|  语言  |  代码   |
|:----:|:-----:|
| 简体中文 | zh_cn |
|  英文  | en_us |

如果您对语言的支持不满意，可以自定义编写语言文件。

```properties
language=zh_cn
language.path=config/lang.json
```

以上配置将优先从 config/lang.json 读取文本信息，如果该文件中不存在，再根据 language 字段读取默认文本。Coral的预设语言文件在 [源码](https://github.com/UnknownBuild/Coral/tree/master/src/main/resources/assets/coral/lang) 中可查看。

Coral 针对不同语言使用了同一套样式方案，如果您对样式的支持不满意，可以自定义编写样式文件。

```properties
style.path=config/style.json
```

Coral 的预设样式文件在 [源码](https://github.com/UnknownBuild/Coral/tree/master/src/main/resources/assets/coral/style.json) 中可以查看。

## 命令支持

### here

我在这里。赋予玩家自身发光效果，并向世界聊天广播玩家的位置。

```
/here
```

该命令仅在专用服务器或单人游戏局域网模式下生效，不支持单人游戏。

| 配置项                   | 类型  | 默认值  | 说明        |
|-----------------------|-----|------|-----------|
| command.here          | 布尔值 | true | 是否启用该命令   |
| command.here.duration | 整型  | 30   | 发光效果时长(秒) |

### player

玩家命令增强。提供更丰富的查询功能。

启用该功能后，服务器将在 world/data 下创建文件 coral_player.data，该文件用于存储玩家在线信息，仅用于辅助玩家信息查询，即使被删除也不影响服务器游玩。

```
/player list       # 列举当前在线玩家名单, 等同于 /list uuids, 但支持点击复制 uuid
/player listall    # 列举服务器所有玩家名单, 部分玩家不会展示名称
```

listall 命令要求执行玩家拥有等级 3 及以上权限。

| 配置项            | 类型  | 默认值  | 说明      |
|----------------|-----|------|---------|
| command.player | 布尔值 | true | 是否启用该命令 |

### wru

你在哪里？询问玩家的位置。

```
/wru <player>
```

该命令仅在专用服务器或单人游戏局域网模式下生效，不支持单人游戏。

| 配置项         | 类型  | 默认值  | 说明      |
|-------------|-----|------|---------|
| command.wru | 布尔值 | true | 是否启用该命令 |

## 特性支持

### call_sleep

快睡觉觉啦。当玩家进入睡觉状态时，向世界广播睡觉请求，提示其他玩家睡觉。

该命令仅在专用服务器或单人游戏局域网模式下生效，不支持单人游戏。

| 配置项                | 类型  | 默认值  | 说明      |
|--------------------|-----|------|---------|
| feature.call_sleep | 布尔值 | true | 是否启用该特性 |

### death_info

快来捡东西。在玩家死亡时，向世界广播死亡地址。

该命令在所有模式中均生效。

| 配置项                | 类型  | 默认值  | 说明      |
|--------------------|-----|------|---------|
| feature.death_info | 布尔值 | true | 是否启用该特性 |
