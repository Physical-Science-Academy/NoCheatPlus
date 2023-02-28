<div align="right">
  语言:
  中文 | 
  <a title="English" href="/README_EN.md">English</a>
</div>

[![Java CI](https://github.com/Physical-Science-Academy/NoCheatPlus/actions/workflows/maven-publish.yml/badge.svg?branch=main)](https://github.com/Physical-Science-Academy/NoCheatPlus/actions/workflows/maven-publish.yml)
[![Discord](https://img.shields.io/discord/795119986716704768?style=plastic)](https://discord.gg/bCQ8pEgk4t)
[![forthebadge](https://forthebadge.com/images/badges/uses-git.svg)](https://forthebadge.com)
# NoCheatPlus
<br>
<p align="center"><img src="ncp-logo.png" height="128"/></p>
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

## 🎉重构进度
- [95％] 生存飞行检测
- [√] 创造飞行检测
- [20%] 加速检测
- [√] 秒吃检测
- [50%] 无减速检测
- [√] 背包行走
- [√] 自动拿箱
- [ ] 无摔落伤害检测
- [ ] 无击退检测
- [ ] 穿墙检测
- [√] 防崩服
- [√] 非法客户端检测
- [√] 发包数量检测
- [ ] 自动搭路检测
- [ ] 杀戮光环检测
- [ ] 攻击距离检测
- [ ] 连点器检测
- [ ] 范围伤害检测
- [ ] 协管面板

## 安装
- Java CI: https://ci.lanink.cn/job/NoCheatPlus
在仓库[CI](https://ci.lanink.cn/job/NoCheatPlus/)里下载最新版本 `NoCheatPlus-1.0-SNAPSHOT-jar-with-dependencies.jar`
- 将其放入服务器的 `plugins/` 文件夹内。
- 在使用插件之前，您必须安装以下依赖
 
- `KotlinLib`

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
- 处罚类型 对象A[关系式]对象B 其他参数
- 多个处罚操作之间用与&&进行连接
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
- 例如version在事例的nocheatplus.admin.all和nocheatplus.admin.helper中都没规定

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

## 视频教程
[1] https://b23.tv/3xIrYPQ

## 开发者接口

- NCP提供丰富的API，可以实现大量自定义功能。API可以在NoCheatPlusAPI中查看
- 实例化方式 `NoCheatPlusAPI api = NoCheatPlus.instance;`
### 
| 方法名 | 介绍 |
|:------------|:----------------|
| getNCPProvider() | 获取NCP主类 |
| getComManager() | 获得NCP模块管理器 |
| getAllComponents() | 获得所有的NCP模块 |
| getAllPlayerData() | 获得所有的NCP玩家数据 |
| addComponents(components: NCPComponent) | 注册NCP模块 |
| hasPlayer(player: Player) | 判断玩家是否在NCP中生成了数据 |
| getPlayerProvider(player: Player) | 获得玩家数据 |
| getNCPLogger() | 获得NCP日志记录器 |
| getNCPConfig() | 获得NCP主配置文件 |
| getNCPBanRecord() | 获得NCP封禁记录文件 |
| isPlayerBan(player: Player) | 判断玩家是否被NCP封禁 |
| kickPlayer(player: Player, type: CheckType) | 让NCP踢出一个玩家 |
| banPlayer(player: Player, days: Int) | 让NCP封禁一个玩家 |
| hasPermission(player: Player, command: String) | 玩家是否拥有使用NCP某命令的权限 |
| hasPermissionBypass(player: Player, type: CheckType) | 玩家是否有权限绕过某检查 |
| createBypassPermission(permission: String, type: CheckType) | 创建一个绕过某检测的权限 |
| removeBypassPermission(permission: String, type: CheckType) | 删除一个绕过某检测的权限 |
| clearAllViolations(player: Player) | 清除玩家所有Violation Level |

## 官方测试服
- 服务器地址: axe.0mc.me
- 服务器端口: 10878

## 注意

- 插件仍然在开发状态，有问题请发送issue！

## 联系我们
- Discord频道: https://discord.gg/bCQ8pEgk4t
