<div align="right">
  语言:
  中文 | 
  <a title="English" href="/README_EN.md">English</a>
</div>

[![forthebadge](https://forthebadge.com/images/badges/uses-git.svg)](https://forthebadge.com)
# NoCheatPlus
<br>
<p align="center"><img src="ncp-logo.png" height="128"/></p>
<br>
基岩版Nukkit服务器及其衍生核心PM1E/PowerNukkitX开发的先进反作弊。架构和设计学习Java版
知名反作弊NoCheatPlus。其目的是修复和阻止我的世界基岩版中的漏洞和作弊
行为。

非常欢迎和支持你来贡献代码，以给社区贡献，star本项目以关注我们

## 特点
- 高性能、低占用、高效率
- 易使用、多语言、社区开源

## 🎉重构进度
- [ ] 飞行检测 (进行中)
- [ ] 鞘翅飞行检测
- [ ] 空中跳跃检测
- [ ] 加速检测
- [ ] 非法移动方式检测
- [ ] 无摔落伤害检测
- [ ] 高跳检测
- [ ] 无击退检测
- [ ] 穿墙检测
- [ ] 异常数据包检测
- [ ] 非法客户端检测
- [ ] 发包数量检测
- [ ] 自动搭路检测
- [ ] 杀戮光环检测
- [ ] 攻击距离检测
- [ ] 连点器检测
- [ ] 范围伤害检测
- [ ] 非法攻击方式检测
- [ ] 协管面板
- [ ] 记录高危玩家行动

## 安装
Java CI: https://ci.lanink.cn/job/NoCheatPlus
在仓库[CI](https://ci.lanink.cn/job/NoCheatPlus/)里下载最新版本 `NoCheatPlus-1.0-SNAPSHOT-jar-with-dependencies.jar` ，将其放入服务器的 `plugins/` 文件夹内。
在使用插件之前，您必须安装以下依赖
 
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

## 配置

接下来启动服务器。之后，你会看到`plugins/`下生成了一个名为`NoCheatPlus`的目录。
让我们首先打开其中的配置文件 `ncpconfig.yml` 。
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
      strict_mode: false
      setback_policy:
        fall_damage: true
        void_to_void: true
      actions: "cancel vl>50&&log vl>30 break=10&&warn vl>150 message=fly_short&&kick vl>200&&ban repeat=3 time=3,0,0"
    morepackets:
      active: true
      actions: "cancel vl>5&&kick vl>15&&ban repeat=3 time=3,0,0"

string:
  kick: "§c§lNCP §7>> §rYou are kicked by NCP because of using @hack on server@next"
  ban: "§c§lNCP §7>> §rYou are banned by NCP for §c@days,@hours,@minutes,@seconds§r because of using @hack @nextEndTime: @end"
  fly_short: "@player could be flying hack @vl"

permission:
  no_permission: "§c§lNCP §7>> §rYou do not have permission to run this command."
  policy:
    - "nocheatplus.admin.all:reload,kick,ban,unban,debug,toggle"
    - "nocheatplus.admin.helper:kick,ban,unban"
~~~

### 自定义处罚系统说明
在每个检测项目中有一个actions的设定，下面给出处罚操作的格式:
处罚类型 对象A[关系式]对象B 其他参数
多个处罚操作之间用与&&进行连接
支持的处罚类型如下:
- `cancel` 产生一个拉回操作，回滚当前tick的运动
- `log` 向NCP日志中记录玩家作弊行为，参数`break 冷却秒数`
- `warn` 向玩家发送一个警告消息，参数`message 消息内容`
- `kick` 将玩家踢出服务器
- `ban` 将玩家从服务器封禁，参数`repeat 容错次数`,`time 封禁时间`
若不设置参数，插件将使用NCP默认值
添加警告消息需要在string中加入

### NCP命令权限管理
在permission.policy中进行设置，格式为:
- `权限:命令表`
命令之间用逗号连接
若命令在NCP中没有一条记录，那么默认所有玩家都可以使用它
例如version在事例的nocheatplus.admin.all和nocheatplus.admin.helper中都没规定

## 视频教程
[1] https://b23.tv/3xIrYPQ

## 开发

公开的API在: NCP-API中

## 官方测试服
服务器地址: axe.0mc.me
服务器端口: 10878

## 注意

项目目前正处于重构状态，在正式服上慎用

## 联系我们
Discord频道: https://discord.gg/bCQ8pEgk4t
