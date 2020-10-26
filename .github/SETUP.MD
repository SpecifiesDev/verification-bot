### Overview

Hi, before I get started with setting up this project I would like to explain the general scope of the project, and why I made it open source.
Generally speaking, you should only fork this project if you want to learn from it, or add features that I have no implemented. With that being said, I will have a fully functional service setup for this project once I start releasing stable builds. Before you fork this project please make sure you understand that this is only to be forked for personal, non-commercial use.

### Installation

The easiest way to clone, and maintain, this project is by using git and cloning it. Open up a command line and run.
```bash
git clone https://github.com/SpecifiesDev/verification-bot
```

This will clone the source code into a folder of your choice.

Now, you're going to want to build the plugin in an IDE of your choice. You need to do this because you're going to have to edit a few values within the code itself in order to get your plugin integrating properly with the bot.

## Setup
I created the bot in a way where much of it's internals can be quickly configured, without having to do much work. This section will go over necessary setup values and what they are. If you want a more comprehensive detail of what each value is, please refer to the wiki.

### Important Values
If you get out your trusty text editor and head over to `./bot/manifest.json`, you will see a lot of things going on. Let's remove all of the clutter and only focus on the important values of this configuration:

```json
    "sql": {
        "host": "",
        "user": "",
        "password": "",
        "database": ""
    },
    "bot": {
        "development_mode": false,
        "token": ""
    }
```

#### SQL

The SQL section of the manifest is rather simple. This bot is built to store all of it's necessary data within a MySQL server. If you're not aware of how to setup a SQL server, a simple google will lead you in the direction to get started. Personally, unless you're just using this for educational purposes, I would suggest investing a small amount in a dedicated server. I personally use GCP, and setup and whitelisted my IP in about two minutes.

Now, that you have your SQL server setup, proceed to grab all of the login information, and place it in the corresponding values. Here's an example:
```json
    "sql": {
        "host": "127.0.0.1",
        "user": "specifiesdev",
        "password": "totallysecurepassword",
        "database": "verification_db"
    }
```
#### Bot Tree

The bot section encompasses all things regarding the bot. From embed styling, to command responses. For now, we'll focus on the two values that you need in order to get the bot up and running.

##### Development Mode
I created a development mode value in order to toggle features that make development testing easier for me. To put it simply, if you're wanting to link anything to local servers, or remove permission restrictions, you want to set this value to true.

##### Token
This is arguably the most important part of this configuration, as without it the entire service doesn't work.

In order to get a bot token, first go to [discord's](https://discord.com/developers/applications) developer portal and create a new application. Next, click the Bot tab, and click "Add Bot." After you do this, you should see a button called "copy token." Click this, and copy it into the configuration value.

Now that you have your token, you're going to want to invite your bot. To get your bot ID, go to the General Information tab, and click copy ID. You may then go [here](https://discordapi.com/permissions.html#2146958847), insert your client ID in the indicated box, and get a link to invite the bot.

### Initialization 
You now have your bot configured in a baseline mode. Go back to the command line you opened earlier and run
```bash
npm i && node bot.js
```
The console will either indicate that your bot has been started, or indicate that there was an error in starting it.


