# CodeArena
## Summary 
To make programmers aware of harmful coding practises and how they can improve their code, we propose CodeArena. CodeArena is an extension to the popular 3D sandbox game called Minecraft. It allows developers to experience incremental changes in the quality of their code and gain progressive insight in the causes of hard-to-maintain code. This tool translates features of a codebase that are considered harmful to monsters in Minecraft, which can then be "fought" to improve the codebase. Fighting the monsters will trace the user back to the source code. If the developer succeeds in solving the issue, the monster will die and the developer will be rewarded in-game. This way, the developer can gradually improve the quality of the code, while learning about code quality in an engaging way.

The developer can advance between different levels and gain experience points for each metric that has been improved. Each level represents a component in the codebase. The metrics to be improved consist of code duplication, unit complexity, unit size and interface size. Points are also awarded for a decrease in code volume. Points will be subtracted if the code quality is decreased.

## Installation
There are two ways to setup CodeArena:
1. **Install CodeArena as Minecraft Forge Mod:** This requires a paid Minecraft account. However, a single Minecraft account can be used for any number of CodeArena users.
2. **Run CodeArena from your IDE:** This does not require a Minecraft account, but does require to install an IDE. I will outline the installation using the Eclipse IDE here.

### Install CodeArena as Minecraft Forge Mod
Download the CodeArena jar from the [Releases section of GitHub](https://github.com/SimonBaars/CodeArena/releases) (or by clicking [here](https://github.com/SimonBaars/CodeArena/releases/download/v1.0/CodeArena-1.0.jar)). Follow [this guide](https://www.minecraftmods.com/how-to-install-minecraft-forge/) to install Minecraft Forge. Make sure you install Forge for Minecraft version **1.12**. Then, follow [this guide](https://www.minecraftmods.com/how-to-install-mods-for-minecraft-forge/) to install CodeArena as a Minecraft mod.

### Run CodeArena from your IDE
Download and install Eclipse from [here](https://www.eclipse.org/downloads/). Clone the CodeArena repo, and import it as a Gradle project into Eclipse. In the file explorer of Eclipse, right click the `CloneDetection_Client.launch` file in the root of the CodeArena project. In the menu, choose `Run as` and choose the first option. This will start CodeArena.

## Turning a Java project into a CodeArena
Now I will explain how to turn a Java project into an arena. Open a single player world using Minecraft. In game, press `c`. This will open the CodeArena dialog. In this window, the arena can be configured. 
