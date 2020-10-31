// import libraries
const manager = require('./utils/SQLManager');
const logger = require('./utils/Logger')
const discord = require("discord.js");
const fs = require('fs');
const proxy = require('./proxy/server');

// custom routing methods for bot querying
const queries = require('./proxy/routes/botQuerying.js');
const { report } = require('./proxy/routes/apiStatus');


// parse the manifest
const config = JSON.parse(fs.readFileSync("./manifest.json"));

// values
const prefix = config.bot.prefix;
const token = config.bot.token;

// create a client
const client = new discord.Client();

// create a map to store commands in memory
let commands = new Map();

// iterate over every command in ./commands and add them to the map
for(let file of fs.readdirSync('./commands').filter(file => file.endsWith('.js'))) { 
    commands.set(file.split('.js')[0], require(`./commands/${file}`));
}

client.on('ready', () => {
    logger.info("Bot is online.");
    proxyInit();
    client.user.setActivity(`${prefix}help`);
});

client.on('message', async message => {

    // Simplify it to cleaner variables
    let content = message.content;
    let guildId; 

    if(!(message.channel.type === 'dm')) guildId = message.guild.id;

    // Ignore it if the message is from the bot
    if(message.author.bot) return;

    // If the message contains our command prefix
    if(content.indexOf(prefix) == 0) {

        // first parse the message, containing the command as well
        const args = content.slice(prefix).trim().split(/ +/g);

        // now, grab the parsed command
        const command = args[0].toLowerCase();

        // finally, removing the command from player, making parsing of arguments more simple
        args.shift();

        // check for commands, if the command is found, execute it with the required arguments

        if(command === `${prefix}ping`) commands.get('ping').execute(message);

        if(command === `${prefix}setup`) commands.get('setup').execute(message, args, manager, guildId);

        if(command === `${prefix}verify`) commands.get('verify').execute(message, args, manager, guildId);

    }

    

});




//queries.getUserStatus(1, 1, client, 1);
const proxyInit = () => {

    let app = proxy.app;

    app.get(`${proxy.url}/bot/username/:UUID`, (req, res) => queries.getUserName(req, res, client, manager));
    app.get(`${proxy.url}/bot/status/:UUID`, (req, res) => queries.getUserStatus(req, res, client, manager));
    app.post(`${proxy.url}/bot/handlers/message/`, (req, res) => queries.sendDM(req, res, client, manager))

    app.listen(proxy.port, () => {
        logger.info(`Proxy started on port: ${proxy.port}`);
    });

}


client.login(token);