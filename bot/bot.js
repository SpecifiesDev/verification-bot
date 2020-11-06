// import libraries
const manager = require('./utils/SQLManager');
const logger = require('./utils/Logger')
const discord = require("discord.js");
const fs = require('fs');
const axios = require('axios');
const proxy = require('./proxy/server');

// custom routing methods for bot querying
const queries = require('./proxy/routes/botQuerying.js');


// parse the manifest
const config = JSON.parse(fs.readFileSync("./manifest.json"));

// values
const prefix = config.bot.prefix;
const token = config.bot.token;

// create a client
const client = new discord.Client();

// create a map to store commands in memory
let commands = new Map();

// create an map to create a status emit system
let active = new Map();

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

client.on('presenceUpdate', (old, user) => {
    
    // I should note that this code is incredibly theoretical and is more than likely going to run into issues. I'll be putting it through extensive testing.

    let resp = {};

    resp.status = user.status;
            
    for(activity of user.activities) {
        if(activity.type === 'LISTENING') {
            resp.song = `${activity.state.split(';')[0]} - ${activity.details}`;
        }
        if(activity.type === 'PLAYING') resp.game = activity.name;
        if(activity.type === 'CUSTOM_STATUS') resp.custom = activity.state;
    }

    if(active.has(user.userID)) {

        let array = active.get(user.userID);

        for(server of array) {
            resp.uuid = server.uuid;
            resp.guild = server.guild;
            emit(`http://${server.ip}/iproxy/status/emit`, resp);
        }

        // I want to come up with a more secure way of forcing data expiration. Because it's in a development state rn idm
        // but eventually we'll need to do something to periodically clear memory that's no longer being used
        //active.delete(user.userID);

    }
});


//queries.getUserStatus(1, 1, client, 1);
const proxyInit = () => {

    let app = proxy.app;

    app.get(`${proxy.url}/bot/username/:UUID`, (req, res) => queries.getUserName(req, res, client, manager));
    app.get(`${proxy.url}/bot/status/:UUID`, (req, res) => queries.getUserStatus(req, res, client, manager, active));
    app.post(`${proxy.url}/bot/handlers/message/`, (req, res) => queries.sendDM(req, res, client, manager));

    app.listen(proxy.port, () => {
        logger.info(`Proxy started on port: ${proxy.port}`);
    });

}

// handle new emit request
const emit = (ip, data) => {
    axios.post(ip, data).then(resp => {
        if(resp.data.success) logger.info(`Data change emitted with guild id: ${data.guild}`);
        else logger.warn(`A data emit was attempted, but something occured on the receiving end.`);
    }).catch(err => {
        logger.error(err);
    });
}


client.login(token);