# PotatoIPDisplay

一款提供查询与显示玩家 IP 归属地信息的 Bukkit 插件。

使用文档详见 [此处](https://upt.curiousers.org/docs/PotatoIpDisplay/intro) 。

## 特性

### 高效率
- 支持本地离线查询，极速响应
- 异步的查询
- 基于 IP 的结果缓存

### 多种查询模式
- `ip2region` - [lionsoul2014/ip2region](https://github.com/lionsoul2014/ip2region) 本地查询 提供IPv4查询
- `pconline` - [太平洋网络IP地址查询Web接口](http://whois.pconline.com.cn/) 在线查询 提供IPv4查询
- `ip-api` - [IP-API.com IP Geolocation API](https://ip-api.com/) 在线查询 提供IPv4查询

### 以及
- 提供可自定义的配置文件
- **支持 Placeholder API**，可配合其他插件使用查询结果
  - *玩家称号，消息格式化，等等*
- 插件最初为 [土豆网络](https://upt.curiousers.org) 定制，开源后采纳了大家提供的许多建议，相比初版已经优化和解决了不少问题。感谢你的帮助！

## 

## 命令与权限

| 命令                                    | 描述                        | 权限                        | 默认 |
|:----------------------------------------|:----------------------------|:----------------------------|:-----|
| /pipd                                   | 等效于 `/potatoipdisplay`   | `potatoipdisplay.command`   | OP   |
| /potatoipdisplay                        | 插件信息                    | `potatoipdisplay.command`   | OP   |
| /potatoipdisplay about                  | 显示关于信息                | `potatoipdisplay.command`   | OP   |
| /potatoipdisplay clear [player/cache]   | 清除玩家/查询缓存           | `potatoipdisplay.command`   | OP   |
| /potatoipdisplay lookup [玩家/IPv4]     | 查询玩家或IP                | `potatoipdisplay.lookup`    | OP   |
| /potatoipdisplay reload                 | 重载插件                    | `potatoipdisplay.reload`    | OP   |

## Placeholder API

| 变量                         | 描述           |
|:-----------------------------|:---------------|
| `%potatoipdisplay_ip%`       | 玩家的 IP      |
| `%potatoipdisplay_country%`  | IP 所属国家    |
| `%potatoipdisplay_province%` | IP 所属省份    |
| `%potatoipdisplay_city%`     | IP 所属城市    |
| `%potatoipdisplay_region%`   | IP 所属区域    |
| `%potatoipdisplay_isp%`      | 运营商信息     |
| `%potatoipdisplay_fallback%` | fallback 变量  |

#### 什么是 fallback 变量？

顾名思义，fallback 变量适用于一些无法查询到对应信息的情况。插件将从 `省份` -> `国家` -> `城市` 依次查询并回退到第一个有效结果，从而在最大程度上避免返回"`未知`"值。

![demo](assets/papidemo.png)

## bStats
![bStats](https://bstats.org/signatures/bukkit/PotatoIpDisplay.svg)</a>

## LICENSE
```
Copyright (C) 2024 by NightFish <hk198580666@outlook.com>
Copyright (C) 2024 by yukonisen <yukonisen@curiousers.org>

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program. If not, see <http://www.gnu.org/licenses/>.
```
