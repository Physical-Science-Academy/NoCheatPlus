<div align="right">
  语言:
  中文 | 
  <a title="English" href="/README_EN.md">English</a>
</div>

[![forthebadge](https://forthebadge.com/images/badges/made-with-java.svg)](https://forthebadge.com)
# NoCheatPlus
一款基于基岩版内核Nukkit以及其衍生核心开发的先进反作弊,作者为Catrainbow233。

其目标为满足服务器的安全需求。

欢迎贡献代码，star本项目以关注我们。
## 🎉特性
- [x] 飞行检测
- [x] 鞘翅飞行检测
- [x] 空中跳跃检测
- [x] 加速检测
- [x] 非法移动方式检测
- [x] 无摔落伤害检测
- [x] 高跳检测
- [x] 无击退检测
- [ ] 穿墙检测
- [x] 异常数据包检测
- [x] 非法客户端检测
- [x] 发包数量检测
- [x] 自动搭路检测
- [x] 杀戮光环检测
- [x] 攻击距离检测
- [x] 连点器检测
- [x] 范围伤害检测
- [x] 非法攻击方式检测
- [ ] 协管面板
- [ ] 记录高危玩家行动

## 安装

在仓库[Releases](https://github.com/Physical-Science-Academy/NoCheatPlus/releases)里下载最新版本 `NoCheatPlus.jar` ，将其放入服务器的 `plugins/` 文件夹内。
在使用插件之前，您必须安装以下依赖
 
- 暂无

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
      setback_policy:
        fall_damage: true
        void_to_void: true
      actions: "cancel vl>50&&log vl>30 break=10&&warn vl>150 message=fly_short&&kick vl>200&&ban repeat=3 time=3,0,0"

string:
  #被反作弊踢出后给出的信息
  kick: "§c§lNCP §7>> §rYou are kicked by NCP because of using @hack on server@next"
  #被反作弊封禁后进入游戏的提示
  ban: "§c§lNCP §7>> §rYou are banned by NCP for §c@days,@hours,@minutes,@seconds§r because of using @hack @nextEndTime: @end"
  fly_short: "@player could be flying hack @vl"

permission:
  no_permission: "§c§lNCP §7>> §rYou do not have permission to run this command."
  policy:
    - "nocheatplus.admin.all:reload,kick,ban,unban,debug"
    - "nocheatplus.admin.helper:kick,ban,unban"
~~~

## 视频教程
暂无

## 开发

待插件稳定后补充

## 注意

项目目前正处于重构状态，不保证可用性和兼容性

