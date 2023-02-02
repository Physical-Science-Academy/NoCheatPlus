<div align="right">
  Languages:
  English | 
  <a title="中文" href="/README.md">中文</a>
</div>

[![forthebadge](https://forthebadge.com/images/badges/uses-git.svg)](https://forthebadge.com)
# NoCheatPlus
<br>
<p align="center"><img src="ncp-logo.png" height="128"/></p>
<br>
An advanced AntiCheat worked on Nukkit/PetteriM1EditionNukkit/PowerNukkit. Learning from
a well-known anticheat NoCheatPlus from Bukkit(Spigot). We are in order to
fix the bugs in Nukkit and prevent players from cheating.

You are supported to contribute, and give us a star to support our development.

## Feature
- Gigh-Performance、Low Occupancy、High Efficiency
- Light、International、Open Source

## 🎉Progress
- [ ] SurvivalFly (On Going)
- [ ] Elytra
- [ ] AirJymp
- [ ] Speed
- [ ] Invalid Movement
- [ ] NoFall
- [ ] HighJump
- [ ] Velocity
- [ ] Phase
- [ ] BadPacket
- [ ] Client
- [ ] Packet
- [ ] Scaffold
- [ ] KillAura
- [ ] Reach
- [ ] AutoClicker
- [ ] HitBox
- [ ] WrongAttack
- [ ] HelperTool

## Installation
Java CI: https://ci.lanink.cn/job/NoCheatPlus
Download the latest `NoCheatPlus-1.0-SNAPSHOT-jar-with-dependencies.jar` on [CI](https://ci.lanink.cn/job/NoCheatPlus/)，and put it in your folder `plugins/`.
And It needs Library plugin
 
- `KotlinLib`

## Command
- `/ncp` get the version info
- `/ncp version` get the version info
- `/ncp reload` reload the config currently
- `/ncp debug` toggle the debug mode
- `/ncp ban` ban a player
- `/ncp unban` unban a player
- `/ncp kick` kick a player

## Config

Then let's run the server. You can ser a folder was created in`plugins/` named `NoCheatPlus`.
Here we pay attention to the main config `ncpconfig.yml`.
~~~yaml
#NoCheatPlus AntiCheat Config
config-version:
  notify: false
  version: 1000
logging:
  active: true
  auto-delete-days: 1
  debug: true
  prefix: "§c§lNCP §7>> §r"
  extended:
    command: true
    violation: true
actions:
  waring_delay: 10
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
checks:
  moving:
    survivalfly:
      active: true
      setback_policy:
        fall_damage: true
        void_to_void: true
      actions: "cancel vl>50&&log vl>30 break=10&&warn vl>150 message=fly_short&&kick vl>200&&ban repeat=3 time=3,0,0"

string:
  #the info to show whom was kicked
  kick: "§c§lNCP §7>> §rYou are kicked by NCP because of using @hack on server@next"
  #the info to show whom was banned
  ban: "§c§lNCP §7>> §rYou are banned by NCP for §c@days,@hours,@minutes,@seconds§r because of using @hack @nextEndTime: @end"
  fly_short: "@player could be flying hack @vl"

permission:
  no_permission: "§c§lNCP §7>> §rYou do not have permission to run this command."
  policy:
    - "nocheatplus.admin.all:reload,kick,ban,unban,debug"
    - "nocheatplus.admin.helper:kick,ban,unban"
~~~

### Custom Action System
There is an actions setting in each detection item, and the format of the penalty operation is given below: 
Penalty Type Object A[ Relation] Object B Other parameters 
The penalty types supported by connecting multiple penalty operations with &&
are as follows:
- `cancel` Lag back a player
- `log` Record the invalid action in NCP Logger，parameter `break The Cooling Time`
- `warn` Send a warning message to player，parameter `message The message you sent`
- `kick` Kick a player from server
- `ban` Ban a player from server，parameter `repeat Fault Tolerance Times`,`time Duration Of Ban`
If no parameter is set, the plug-in will use NCP default value
to add warning message, which needs to be added in string.

### NCP Commands Permission
Set it in permission.policy, and the format is:
- `Permission: Command Table' 
commands are connected by commas. 
If there is no record of a command in NCP, all players can use it by default.
For example, the version is not specified in nocheatplus.admin.all and nocheatplus.admin.helper of the case.

## Video Showing
[1] https://b23.tv/3xIrYPQ

## Development

Our open API: NCP-API

## Notice

Plugin are still in development. Don't use on formal server.