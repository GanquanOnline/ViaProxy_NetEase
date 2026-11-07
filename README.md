# ViaProxy_NetEase

[![Build](https://github.com/GanquanOnline/ViaProxy_NetEase/actions/workflows/build.yml/badge.svg)](https://github.com/GanquanOnline/ViaProxy_NetEase/actions/workflows/build.yml)

独立代理：让 Java 版客户端连上基岩服，包括网易基岩服。Java 客户端连本机代理，代理用内置的 ViaBedrock_NetEase 把 Java 协议翻译成基岩协议，再转发到目标服；启用网易仿真时走 RakNet 8。

> **English.** A standalone ViaProxy fork for joining Bedrock servers (including NetEase / China Edition) from a Java Edition client. It bundles ViaBedrock_NetEase and switches to RakNet 8 when NetEase emulation is enabled. Licensed GPL-3.0-or-later.

**状态：整条网易链路仍在开发中**，不建议用于生产环境。

## 上游链条

`ViaVersion/ViaProxy` → **本仓库**

内部打包：`ViaVersion`（协议基座，含本地补丁）＋ `ViaBedrock_NetEase`（Java ↔ 基岩翻译）。

## 支持的版本

- **Java 侧**：与上游 ViaProxy 相同的客户端范围，见[上游 README](https://github.com/ViaVersion/ViaProxy#supported-client-versions)
- **基岩侧**：由打包进来的 ViaBedrock_NetEase 决定
  - 国际基岩最高 `2225`（1.26.60）
  - 网易 `898`（`1.21.130_NetEase`）、`860`（`1.21.124_NetEase`），RakNet 8

基岩目标版本在 `viabedrock.yml` 里配，不在 ViaProxy 自己的配置里。

## 与上游的差异

- Java 21 工具链
- 依赖走 `mavenLocal()`，用来吃本地发布的 ViaVersion 与 ViaBedrock_NetEase
- `includeInJar` 打包 ViaBedrock_NetEase，产物是自带翻译层的单文件 jar
- 网易仿真开启时 RakNet 版本取 ViaBedrock 配置里的值（默认 8），不写死国际 RakNet
- 双传输栈：RakNet 走 kastle，NetherNet 走 Cloudburst 2.0.0.CR6
- 基岩目标判定统一走 `BedrockProtocolVersion.isBedrock`，不再用 `equals(bedrockLatest)`
- 启动失败直接结束进程，不留半启动的界面

## 构建

需要 JDK 21，按依赖顺序编：

```bash
# 1. 协议基座
cd ViaVersion && ./gradlew publishToMavenLocal

# 2. 翻译层
cd ViaBedrock && ./gradlew publishToMavenLocal

# 3. 代理本体
cd ViaProxy && ./gradlew jar
```

产物在 `build/libs/ViaProxy-*.jar`，已包含 ViaBedrock 与 ViaVersion，不需要额外装插件。

## 运行

```bash
java -jar ViaProxy-*.jar                        # 打开 GUI
java -jar ViaProxy-*.jar config viaproxy.yml    # 配置文件不存在时生成模板并退出；存在时按该配置启动
```

默认监听 `0.0.0.0:25568`，Java 客户端连 `localhost:25568` 即可。网易服还需要在 `viabedrock.yml` 里打开 `netease.enabled`。

## 许可与署名

本项目以 **GPL-3.0-or-later** 分发。

- `LICENSE` 是 GPLv3 原文，逐字保留：许可证正文本身禁止修改，也不能被替换成别的许可证。
- 上游版权：ViaProxy 由 RK_01/RaphiMC、ViaVersion 组织及贡献者持有（GPL-3.0）；ViaBedrock 由 RK_01/RaphiMC 及贡献者持有。
- 本仓库新增与修改部分的版权归 GanquanOnline 贡献者所有，同样按 GPL-3.0-or-later 授权。
- 按 GPL-3.0 §5(a) 声明：本仓库自 2026 年起修改了上游代码，修改内容见上文“与上游的差异”。分发二进制时必须同时提供对应源码。

分发本项目的衍生作品请保持 GPL-3.0-or-later，不要改成 MIT、Apache-2.0 等许可证。
