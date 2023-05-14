<div align="center">

[![Java CI](https://github.com/Physical-Science-Academy/NoCheatPlus/actions/workflows/maven.yml/badge.svg?branch=main)](https://github.com/Physical-Science-Academy/NoCheatPlus/actions/workflows/maven.yml)
[![Maven](https://jitpack.io/v/Physical-Science-Academy/NoCheatPlus.svg)](https://jitpack.io/#Physical-Science-Academy/NoCheatPlus)
[![English](https://img.shields.io/badge/English-100%25-green?style=flat-square)](https://github.com/Physical-Science-Academy/NoCheatPlus/blob/main/README.md)
[![简体中文](https://img.shields.io/badge/简体中文-100%25-green?style=flat-square)](https://github.com/Physical-Science-Academy/NoCheatPlus/blob/main/README_CH.md)
[![Discord](https://img.shields.io/discord/795119986716704768?style=plastic)](https://discord.gg/bCQ8pEgk4t)
[![forthebadge](https://forthebadge.com/images/badges/uses-git.svg)](https://forthebadge.com)
[![License: GPL v3](https://img.shields.io/badge/License-GPL%20v3-blue.svg?style=flat-square)](https://github.com/Physical-Science-Academy/NoCheatPlus/blob/main/LICENSE)

[English](README.md) | [简体中文](README_CH.md)

</div>

<div align="center">

# NoCheatPlus

</div>

<br>
<p align="center"><img src="ncp-logo.png" height="256"/></p>
<br>

- 基岩版Nukkit服务器及其衍生核心PM1E/PowerNukkitX开发的先进反作弊。
- 架构和设计学习Java版知名反作弊NoCheatPlus。
- 其目的是修复和阻止我的世界基岩版中的漏洞和作弊行为。

- 非常欢迎和支持你来贡献代码，以给社区贡献，star本项目以关注我们

## 特点

- 高性能、低占用、高效率
- 易使用、多语言、社区开源
- 保护你的服务器,防止崩服
- 阻止玩家在你的服务器上面作弊

## 相关链接
---------

###### 官方文档/维基

* [NoCheatPlus Docs](https://docs.catrainbow.me)

###### 插件下载

* [Jenkins (实时构建)](https://ci.lanink.cn/job/NoCheatPlus/)
* [NukkitX/CloudBurst (仅稳定版)](https://cloudburstmc.org/resources/nocheatplus.820/)
* [MCBBS (仅稳定版)](https://www.mcbbs.net/forum.php?mod=viewthread&tid=1430379)
* [MINEBBS (仅稳定版)](https://www.minebbs.com/resources/nocheatplus.5551/)
* [PowerNukkitX Hub (实时构建)](https://powernukkitx.com/hub/plugin/detail/Physical-Science-Academy/NoCheatPlus)

###### 支持和帮助

* [Issues/Tickets](https://github.com/Physical-Science-Academy/NoCheatPlus/issues)

###### 对开发者

* [License (GPLv3)](https://github.com/Physical-Science-Academy/NoCheatPlus/blob/main/LICENSE)
* [Contribute](https://github.com/Physical-Science-Academy/NoCheatPlus/blob/main/CONTRIBUTING.md)

###### 适配核心

* [Nukkit](https://github.com/Nukkit/Nukkit)
* [NukkitX](https://github.com/CloudburstMC/Nukkit)
* [PM1E](https://github.com/PetteriM1/NukkitPetteriM1Edition)
* [PNX](https://github.com/PowerNukkitX/PowerNukkitX)
* [EaseCation-Nukkit](https://github.com/EaseCation/Nukkit)

###### 相关插件和扩展组件

* [NCPPlugin](https://cloudburstmc.org/resources/ncpplugin.896/)
  ------------------------------
  这是一个调试插件，它可以让你的NCP只通过Title提醒被踢的玩家，而不会实际提醒
  踢它，它可以用来调试插件的配置。

 -------------------------------

* [ECCompatNCP](https://cloudburstmc.org/resources/eccompatncp.902/)
  ------------------------------
  这是一个 CPS 检测扩展组件。它使用知名小游戏服务器的检测算法
  EaseCation，间隔获取cps。如果每次都高于阈值，则反作弊将踢它。
  使用该插件会自动关闭NCP自身的CPS检测系统。

 -------------------------------

* [NCPPanel](https://cloudburstmc.org/resources/ncppanel.906/)
  ------------------------------
  这是NCP的扩展组件，为NCP提供可视化操作面板和玩家举报系统。
  它的所有语言主题甚至命令都可以在配置文件中自定义。

 -------------------------------

* [CompatNCP](https://cloudburstmc.org/resources/compatncp.907/)
  ------------------------------
  这是NCP的一个扩展，可以让你手动兼容其他插件，减少误判
  由于不支持导致反作弊，比如双跳。

  ------------------------------

* [NCPLiteBan](https://cloudburstmc.org/resources/ncpliteban.912/)
  ------------------------------
  这是NCP的扩展插件，同步NCP ban数据到群服数据数据库
  同步，并依赖于插件 DbLib。如果您安装了 NCPPanel，它甚至可以同步面板数据。

  ------------------------------

* [NCPStaticBar](https://cloudburstmc.org/resources/ncpstaticbar.913/)
  ------------------------------
  这是 NCP 的扩展，它将把你的 NCP 变成一个被动的静态反作弊。反作弊不起作用
  通常情况下，只有在其他玩家举报后，它才会检测到玩家。它没有报告系统
  本身，并且需要安装 NCPPanel。

  ------------------------------

* [NCPWebserver](https://github.com/Physical-Science-Academy/NCPWebserver)
  ------------------------------
  这是NCP的扩展插件，可以为你的NCP提供一个网页面板来管理服务器。这意味着你不用手动修改配置文件，直接通过网页也能便捷的检测服务器内玩家的行为。

  ------------------------------

## 🎉重构进度

- [ ] SurvivalFly(97%)
- [x] CreativeFly
- [ ] Speed(20%)
- [x] FastEat
- [ ] NoSlow(50%)
- [x] InventoryMove
- [x] InventoryFastClick
- [x] NoFall
- [ ] Velocity
- [ ] Phase
- [x] Crasher
- [x] Client
- [x] MorePacket
- [x] FastBreak
- [ ] Scaffold
- [ ] KillAura
- [x] Reach
- [x] X-Ray
- [x] AutoClicker
- [ ] HitBox
- [x] Helper Tool GUI

## 安装

- Java CI: https://ci.lanink.cn/job/NoCheatPlus
  在仓库[CI](https://ci.lanink.cn/job/NoCheatPlus/)里下载最新版本 `NoCheatPlus-1.0-SNAPSHOT-jar-with-dependencies.jar`
- 将其放入服务器的 `plugins/` 文件夹内。
- 在使用插件之前，您必须安装以下依赖

- `KotlinLib`

## 构建插件

#### 需要: Kotlin | Java (8|17)

- `git clone https://github.com/Physical-Science-Academy/NoCheatPlus.git`
- `cd NoCheatPlis`
- `git submodule update --init`
- `./mvnw clean package`

* 构建好的插件将出现在 target/ 目录。

## 命令

- `/ncp` 获取NCP信息
- `/ncp version` 获取NCP信息
- `/ncp reload` 热重载配置文件
- `/ncp debug` 临时开关调试模式
- `/ncp ban` 封禁一名玩家
- `/ncp unban` 解封一名玩家
- `/ncp kick` 踢出一名玩家
- `/ncp toggle` 开关检测项目
- `/ncp permission` 管理绕过检查的权限

## 配置

接下来启动服务器。之后，你会看到`plugins/`下生成了一个名为`NoCheatPlus`的目录。
让我们首先打开其中的配置文件 `ncpconfig.yml` 。

~~~yaml
# NoCheatPlus AntiCheat Config
config-version:
  notify: false
  version: 1000

# Currently "en" and "zh" are supported languages
# You are able to create your own language in the "lang" config directory
lang: "en"

logging:
  active: true
  auto-delete-days: 1
  debug: false
  prefix: "§c§lNCP §7>> §r"
  extended:
    command: true
    violation: true
actions:
  waring_delay: 10
  kick_broadcast: "§c§lNCP §7>>@player has been kicked for @hack"
protection:
  net:
    packet: true
    chunk:
      active: true
      dynamicScan: false
      scanHeight: 6.0
      scanWorld:
        - world
      filter:
        - 0
        - 8
        - 9
        - 10
        - 11
        - 20
        - 26
        - 27
        - 30
        - 31
        - 32
        - 37
        - 38
        - 39
        - 40
        - 44
        - 50
        - 63
        - 64
        - 65
        - 66
        - 68
        - 71
        - 81
        - 83
        - 85
        - 96
        - 101
        - 102
        - 104
        - 105
        - 106
        - 107
        - 126
        - 141
        - 142
      ores:
        - 14
        - 15
        - 16
        - 21
        - 56
        - 73
        - 74
        - 129
  command:
    hide:
      active: true
      message: "§c§lNCP §7>> §rYou do not have permission to run this command."
      commands:
        - "?"
        - "plugins"
        - "version"
        - "about"
        - "ver"
checks:
  blockbreak:
    fastbreak:
      active: true
      max: 35
      min: 0
      actions: "cancel vl>5"
  fight:
    speed:
      active: true
      maxspeed: 25
      dealvariance: 0.1
      cancelDamage: true
      actions: "cancel vl>5&&kick vl>20"
  inventory:
    instanteat:
      active: true
      actions: "cancel vl>5&&kick vl>20"
    move:
      active: true
      actions: "cancel vl>0&&kick vl>3"
    open:
      active: true
      actions: "cancel vl>0"
    fastclick:
      active: true
      delay: 50
      actions: "cancel vl>5&&kick vl>20"
    item:
      active: true
      actions: "kick vl>10"
  moving:
    survivalfly:
      active: true
      strict_mode: false
      setback_policy:
        fall_damage: true
        void_to_void: true
        latency_protection: 120
      actions: "cancel vl>20&&log vl>30 break=60&&warn vl>90 message=fly_short&&kick vl>100&&ban repeat=3 time=3,0,0"
    speed:
      active: true
    morepackets:
      active: true
      actions: "cancel vl>5&&kick vl>15&&ban repeat=3 time=3,0,0"
    creativefly:
      active: true
      actions: "cancel vl>20"
    nofall:
      active: true
      dealdamage: true
      skipallowflight: true
      resetonviolation: false
      resetonteleport: true
      resetonvehicle: true
      actions: "cancel vl>5&&log vl>10&&kick vl>20"
    vehicle:
      active: true
      actions: "cancel vl>10&&kick vl>50&&ban repeat=3 time=3,0,0"

string:
  kick: "§c§lNCP §7>> §rYou are kicked by NCP because of using @hack on server@next"
  ban: "§c§lNCP §7>> §rYou are banned by NCP for §c@days,@hours,@minutes,@seconds§r because of using @hack @nextEndTime: @end"
  fly_short: "@player could be flying hack @vl"

permission:
  no_permission: "§c§lNCP §7>> §rYou do not have permission to run this command."
  policy:
    - "nocheatplus.admin.all:reload,kick,ban,unban,debug,toggle,permission"
    - "nocheatplus.admin.helper:kick,ban,unban"
  bypass:
    # Custom bypass permission
    MOVING_CREATIVE_FLY:
      - "ncp.creativefly.bypass"
~~~

### 自定义处罚系统说明

在每个检测项目中有一个actions的设定，下面给出处罚操作的格式:

- 处罚类型 `对象A[关系式]对象B 其他参数`
- 多个处罚操作之间用与`&&`进行连接
- 支持的处罚类型如下:
- `cancel` 产生一个拉回操作，回滚当前tick的运动
- `log` 向NCP日志中记录玩家作弊行为，参数`break 冷却秒数`
- `warn` 向玩家发送一个警告消息，参数`message 消息内容`
- `kick` 将玩家踢出服务器
- `ban` 将玩家从服务器封禁，参数`repeat 容错次数`,`time 封禁时间`
- 若不设置参数，插件将使用NCP默认值
  添加警告消息需要在string中加入

### NCP命令权限管理

在permission.policy中进行设置，格式为:

- `权限:命令表`
  命令之间用逗号连接
- 若命令在NCP中没有一条记录，那么默认所有玩家都可以使用它
- 例如version在事例的`nocheatplus.admin.all`和`nocheatplus.admin.helper`中都没规定

### NCP自定义绕过权限

在permission下bypass中添加，格式为:

- `作弊类型:权限(List)`
  拥有权限的玩家不会受到该项检测

可用作弊类型:

- `MOVING_SURVIVAL_FLY`
- `MOVING_CREATIVE_FLY`
- `MOVING_VEHICLE`
- `MOVING_SPEED`
- `MOVING_MORE_PACKETS`
- `MOVING_NO_FALL`
- `INVENTORY_INSTANT_EAT`
- `INVENTORY_OPEN`
- `INVENTORY_FAST_CLICK`
- `INVENTORY_MOVE`
- `FIGHT_SPEED`
- `FIGHT_REACH`

## 视频教程

- [BiliBili (已删除)](https://b23.tv/3xIrYPQ)

## 开发者文档

- NCP 提供了丰富的开发接口，你可以依赖它快速开发扩展插件

  开发者接口参考 [NoCheatPlus](https://github.com/Physical-Science-Academy/NoCheatPlus)

### GroupId

- `com.github.Physical-Science-Academy.NoCheatPlus`

### Repository 版本

|  ArtifactId  |          Version           |
|:------------:|:--------------------------:|
| NoCheatPlus  |   maven-repo-20220552-2a   |
| NoCheatPlus  |   maven-repo-20220552-3a   |
| NoCheatPlus  |   maven-repo-20220552-4a   |
| NoCheatPlus  | maven-repo-20220552-common |
|  CompatNCP   | maven-repo-20220552-common |
|  ECCPCompat  | maven-repo-20220552-common |
|  NCPLiteBan  | maven-repo-20220552-common |
|  NCPPlugin   | maven-repo-20220552-common |
| NCPStaticBar | maven-repo-20220552-common |

### Gradle:

```gradle
	allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}

	dependencies {
	        implementation 'com.github.Physical-Science-Academy.NoCheatPlus:CompatNCP:maven-repo-20220552-common'
	}
```

### Maven:

##### Repository:

```xml

<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>
```

##### Dependencies:

```xml

<dependencies>
    <dependency>
        <groupId>com.github.Physical-Science-Academy.NoCheatPlus</groupId>
        <artifactId>NoCheatPlus</artifactId>
        <version>maven-repo-20220552-common</version>
    </dependency>
</dependencies>
```

一个简单的例子去实例化API:

```java

NoCheatPlusAPI api = NoCheatPlus.instance;

```

###  

| 方法名                                                         | 介绍                    |
|:------------------------------------------------------------|:----------------------|
| getNCPProvider()                                            | 获取NCP主类               |
| getComManager()                                             | 获得NCP模块管理器            |
| getAllComponents()                                          | 获得所有的NCP模块            |
| getAllPlayerData()                                          | 获得所有的NCP玩家数据          |
| addComponents(components: NCPComponent)                     | 注册NCP模块               |
| hasPlayer(player: Player)                                   | 判断玩家是否在NCP中生成了数据      |
| getPlayerProvider(player: Player)                           | 获得玩家数据                |
| getNCPLogger()                                              | 获得NCP日志记录器            |
| getNCPConfig()                                              | 获得NCP主配置文件            |
| getNCPBanRecord()                                           | 获得NCP封禁记录文件           |
| isPlayerBan(player: Player)                                 | 判断玩家是否被NCP封禁          |
| kickPlayer(player: Player, type: CheckType)                 | 让NCP踢出一个玩家            |
| banPlayer(player: Player, days: Int)                        | 让NCP封禁一个玩家            |
| hasPermission(player: Player, command: String)              | 玩家是否拥有使用NCP某命令的权限     |
| hasPermissionBypass(player: Player, type: CheckType)        | 玩家是否有权限绕过某检查          |
| createBypassPermission(permission: String, type: CheckType) | 创建一个绕过某检测的权限          |
| removeBypassPermission(permission: String, type: CheckType) | 删除一个绕过某检测的权限          |
| clearAllViolations(player: Player)                          | 清除玩家所有Violation Level |

## 官方测试服(不定时开启)

- 服务器地址: axe.0mc.me
- 服务器端口: 10878

## 注意

- 插件仍然在开发状态，有问题请发送issue！

## 联系我们

- [Discord](https://discord.gg/bCQ8pEgk4t)
- [TencentQQ](http://qm.qq.com/cgi-bin/qm/qr?_wv=1027&k=ysAUd55Yl8WDcfk910CVmc6ROGf0RqFU&authKey=CHhN10VbvJV6zO81LLz44I3gVa8UvU%2BCfiGBmTMTgI4do29IJ55AlIptNu8ctzO7&noverify=0&group_code=603565881)
