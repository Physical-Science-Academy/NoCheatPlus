<div align="right">
  Languages:
  English | 
  <a title="中文" href="/README_CH.md">中文</a>
</div>

[![Java CI](https://github.com/Physical-Science-Academy/NoCheatPlus/actions/workflows/maven-publish.yml/badge.svg?branch=main)](https://github.com/Physical-Science-Academy/NoCheatPlus/actions/workflows/maven-publish.yml)
[![English](https://img.shields.io/badge/English-100%25-green?style=flat-square)](https://github.com/Physical-Science-Academy/NoCheatPlus/blob/main/README.md)
[![简体中文](https://img.shields.io/badge/简体中文-100%25-green?style=flat-square)](https://github.com/Physical-Science-Academy/NoCheatPlus/blob/main/README_CH.md)
[![Discord](https://img.shields.io/discord/795119986716704768?style=plastic)](https://discord.gg/bCQ8pEgk4t)
[![forthebadge](https://forthebadge.com/images/badges/uses-git.svg)](https://forthebadge.com)
[![License: GPL v3](https://img.shields.io/badge/License-GPL%20v3-blue.svg?style=flat-square)](https://github.com/Physical-Science-Academy/NoCheatPlus/blob/main/LICENSE)

# NoCheatPlus
<br>
<p align="center"><img src="ncp-logo.png" height="128"/></p>
<br>

- An advanced AntiCheat worked on Nukkit/PetteriM1EditionNukkit/PowerNukkitX.
- Learning from a well-known anti-cheat NoCheatPlus from Bukkit(Spigot).
- We are in order to fix the bugs in Nukkit and prevent players from cheating.

- You are supported to contribute, and give us a star to support our development.

## Feature
- High-Performance、Low Occupancy、High Efficiency
- Light、International、Open Source
- Protect your server, and prevent it from crashing
- prevent players from cheating on your server

## Links
---------

###### Download
* [Jenkins (current)](https://ci.lanink.cn/job/NoCheatPlus/)
* [NukkitX/CloudBurst (stable)](https://cloudburstmc.org/resources/nocheatplus.820/)
* [MCBBS (stable)](https://www.mcbbs.net/forum.php?mod=viewthread&tid=1430379)
* [MINEBBS (stable)](https://www.minebbs.com/resources/nocheatplus.5551/)

###### Supports
* [Issues/Tickets](https://github.com/Physical-Science-Academy/NoCheatPlus/issues)

###### Developers
* [License (GPLv3)](https://github.com/Physical-Science-Academy/NoCheatPlus/blob/main/LICENSE)
* [Contribute](https://github.com/Physical-Science-Academy/NoCheatPlus/blob/main/CONTRIBUTING.md)

###### Nukkit Support
* [Nukkit](https://github.com/Nukkit/Nukkit)
* [NukkitX](https://github.com/CloudburstMC/Nukkit)
* [PM1E](https://github.com/PetteriM1/NukkitPetteriM1Edition)
* [PNX](https://github.com/PowerNukkitX/PowerNukkitX)
* [EaseCation-Nukkit](https://github.com/EaseCation/Nukkit)

###### Related Plugins
* [NCPPlugin](https://cloudburstmc.org/resources/ncpplugin.896/)
* [ECCompatNCP](https://cloudburstmc.org/resources/eccompatncp.902/)
* [NCPPanel](https://cloudburstmc.org/resources/ncppanel.906/)
* [CompatNCP](https://cloudburstmc.org/resources/compatncp.907/)

## 🎉Progress
- [97％] SurvivalFly
- [√] CreativeFly
- [20%] Speed
- [√] FastEat
- [50%] NoSlow 
- [√] InventoryMove
- [√] InventoryFastClick
- [ ] NoFall
- [ ] Velocity
- [ ] Phase
- [√] Crasher
- [√] Client
- [√] MorePacket
- [√] FastBreak
- [ ] Scaffold
- [ ] KillAura
- [ ] Reach
- [√] AutoClicker
- [ ] HitBox
- [ ] Helper Tool GUI

## Installation
- Java CI: https://ci.lanink.cn/job/NoCheatPlus
- Download the latest `NoCheatPlus-1.0-SNAPSHOT-jar-with-dependencies.jar` on [CI](https://ci.lanink.cn/job/NoCheatPlus/)
- and put it in your folder `plugins/`.
- And It needs Library plugin
 
- `KotlinLib`

## Command
- `/ncp` get the version info
- `/ncp version` get the version info
- `/ncp reload` reload the config currently
- `/ncp debug` toggle the debug mode
- `/ncp ban` ban a player
- `/ncp unban` unban a player
- `/ncp kick` kick a player
- `/ncp toggle` Switch detection
- `/ncp permission` manage permissions.

## Config

- Then let's run the server. 
- You can ser a folder was created in`plugins/` named `NoCheatPlus`.
- Here we pay attention to the main config `ncpconfig.yml`.
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

### Custom Action System
- There is an actions setting in each detection item, and the format of the penalty operation is given below: 
- Penalty Type Object A[ Relation] Object B Other parameters 
- The penalty types supported by connecting multiple penalty operations with &&
- are as follows:
- `cancel` Lag back a player
- `log` Record the invalid action in NCP Logger，parameter `break The Cooling Time`
- `warn` Send a warning message to player，parameter `message The message you sent`
- `kick` Kick a player from server
- `ban` Ban a player from server，parameter `repeat Fault Tolerance Times`,`time Duration Of Ban`
- If no parameter is set, the plug-in will use NCP default value
- to add warning message, which needs to be added in string.

### NCP Commands Permission
- Set it in permission.policy, and the format is:
- `Permission: Command Table' 
- commands are connected by commas(,). 
- If there is no record of a command in NCP, all players can use it by default.
- For example, the version is not specified in nocheatplus.admin.all and nocheatplus.admin.helper of the case.

### NCP Custom Bypass Permission
Add them in permission.bypass，and the format is:
- `CheckType:Permissions(List)`
The player who has these permissions will bypass the check.

Availabe CheckType:
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

## Video Showing
- https://b23.tv/3xIrYPQ

## Development

- NCP provides much API，to achieve many functions。see the api in NoCheatPlusAPI

A work in progress API for [NoCheatPlus](https://github.com/Physical-Science-Academy/NoCheatPlus)

You can find the latest version here: https://jitpack.io/#Physical-Science-Academy/NoCheatPlus

Gradle:
```gradle
allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}

dependencies {
	        implementation 'com.github.Physical-Science-Academy:NoCheatPlus:-SNAPSHOT'
	}
```

Maven:
```xml
<repository>
   <id>jitpack.io</id>
   <url>https://jitpack.io</url>
</repository>
  
<dependency>
	    <groupId>com.github.Physical-Science-Academy</groupId>
	    <artifactId>NoCheatPlus</artifactId>
	    <version>-SNAPSHOT</version>
	</dependency>
```

Obtaining an instance of the API:
```java
NoCheatPlusAPI api = NoCheatPlus.instance;
```

### 
| Method | Description |
|:------------|:----------------|
| getNCPProvider() | get main class of NCP |
| getComManager() | get component manager of NCP |
| getAllComponents() | get all modules of NCP |
| getAllPlayerData() | get all player data in NCP |
| addComponents(components: NCPComponent) | register a NCP Module |
| hasPlayer(player: Player) | judge a player's data is existed |
| getPlayerProvider(player: Player) | get a player's data in NCP |
| getNCPLogger() | get NCP Logger |
| getNCPConfig() | get config file of NCP |
| getNCPBanRecord() | get ban config of NCP |
| isPlayerBan(player: Player) | judge a player if he is banned by NCP |
| kickPlayer(player: Player, type: CheckType) | kick a player by NCP |
| banPlayer(player: Player, days: Int) | ban a player by NCP |
| hasPermission(player: Player, command: String) | judge a player if he is allowed to use this NCP's command |
| hasPermissionBypass(player: Player, type: CheckType) | judge a playet if he is allowed to bypass this check |
| createBypassPermission(permission: String, type: CheckType) | created a permission to bypass the check |
| removeBypassPermission(permission: String, type: CheckType) | delete a permission which can bypass some checks |
| clearAllViolations(player: Player) | clear all Violation Levels of a player |

## Test Server
- Address: axe.0mc.me
- Port: 10878

## Notice

- Plugin are still in development. Report through Issue if you need!

## Contact us
- Discord: https://discord.gg/bCQ8pEgk4t
