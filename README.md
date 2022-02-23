<h1 align="Center">BTE-Utilities</h1>
<p align="Center">A set of utilities and tweaks for the BTE Modpack</p>
<p align="center">
    <img src="https://go.buildtheearth.net/official-shield">
    <a href="https://github.com/BuildTheEarth/BTE-Utilities/actions/workflows/gradle.yml/"><img src="https://github.com/BuildTheEarth/BTE-Utilities/actions/workflows/gradle.yml/badge.svg"></a>
    <a href="https://discord.com/invite/BGpmp3sfH5"><img src="https://img.shields.io/discord/706317564904472627?label=discord"></a>
</p>

## Features

* Custom Main Menu
* Discord Prescense Integration

### Roadmap

* Discord Game API - Join minigames from discord status

## Licensing

BTE-Utilities is distributed under the MIT License.
Read [LICENSE](https://github.com/BuildTheEarth/BTE-Utilities/blob/master/LICENSE) for more information.

## Building

* Clone this repo, or download as a zip
* Open `BTE-Utilities` in your preferred IDE
    - IntelliJ with the Minecraft Development plugin will provide the best experience for Forge.
* Build using: `./gradlew build`
* The final jar will be found under `/build/libs`

## Libraries

* [CustomMainMenu](https://www.curseforge.com/minecraft/mc-mods/custom-main-menu) - Portions of rendering code is used.
* [DiscordIPC](https://github.com/jagrosh/DiscordIPC)
* [JUnixSocket](https://github.com/kohlschutter/junixsocket)
