# Coral配置指南

Coral的配置文件使用YAML标记语言编写，如果您不了解YAML，请先尝试通过百度或必应了解YAML。

## 关于配置文件

Coral的配置文件被放置在服务端`configs`文件夹中，可以被命名为`coral.yaml`或`coral.yml`。

Coral的默认配置可在[源码](https://github.com/UnknownBuild/Coral/blob/master/src/main/resources/assets/coral/default-config.yaml)中查看，该文件为最新版本的Coral默认配置。(如果需要查看特定版本的默认配置，可跳转到该Commit查看，或者在该版本`jar`文件中查看)

## 翻译与样式

Coral大多数功能需要向玩家们显示文本信息，通过配置文件可以修改文本信息的语言。

```yaml
translation:
  region: zh_CN
```

目前Coral支持简体中文和英文。

|   语言   | 代码  |
| :------: | :---: |
| 简体中文 | zh_CN |
|   英文   | en_US |

如果您对语言的支持不满意，可以自定义编写语言文件。

```yaml
translation:
  region: zh_CN
  customLangFile: configs/lang.json
```

以上配置将优先从`configs/lang.json`读取文本信息，如果该文件中不存在，再根据`region`字段读取简体中文默认文本。Coral的预设语言文件在[源码](https://github.com/UnknownBuild/Coral/tree/master/src/main/resources/assets/coral/lang)中可查看。

Coral针对不同语言使用了同一套样式方案，如果您对样式的支持不满意，可以自定义编写样式文件。

```yaml
translation:
  customStyleFile: configs/style.json
```

Coral的预设样式文件在[源码](https://github.com/UnknownBuild/Coral/tree/master/src/main/resources/assets/coral/style.json)中可以查看。

## 命令

### here

* 功能：赋予玩家自身发光效果，并向世界广播玩家地址。
* 用法：`/here`。
* 配置：

```yaml
command:
  here:
    enabled: true  # 是否启用该命令
    duration: 30s  # 发光时长，目前该参数未起作用
```

### wru

* 功能：询问玩家位置。
* 用法：`/wru <PlayerName>`
* 配置：

```yaml
command:
  wru: true  # 是否启用该命令
```

## 扩展

### msgCallSleep

* 功能：当玩家进入睡觉状态时，向世界广播睡觉请求。
* 配置：

```yaml
function:
  msgCallSleep: true  # 是否启用该功能
```

### msgDeathInfo

* 功能：在玩家死亡时，向世界广播死亡地址。
* 配置：

```yaml
function:
  msgDeathInfo: true  # 是否启用该功能
```