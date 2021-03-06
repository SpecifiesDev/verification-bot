// import libraries
const embeds = require('../utils/embeds');
const logger = require('../utils/Logger.js');
const utils = require("../utils/utils.js");
const fs = require('fs');
const axios = require('axios');

// import configuration
const config = JSON.parse(fs.readFileSync(`${__dirname.split('commands')[0]}manifest.json`));

// pull constants
const constants = config.bot.command_constants.setup;
const internal_error = config.bot.command_constants.internal_error;

// Execute function for setup command.
const setup = async (message, args, pool, guild) => {
    
    // Ensure that the proper arguments were passed to the command.
    if(!(args.length >= 2)) return await message.channel.send(embeds.embed("Invalid Arguments", utils.replacePlaceholders(constants.invalidargs, "setup")))

    // Split the ip and port for validation
    let ip = args[0].split(':')[0];
    let port = args[0].split(':')[1];

    // If there's no port, notify the user. We do this to create a strict system
    if(port == undefined) return await message.channel.send(embeds.embed("Invalid Port", constants.invalidport));

    // pulled from a w3 res
    let ipregexp = /^(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$/;

    // if the ip is invalid, a local machine ip, and the bot wasn't launched in dev mode, forbid the function
    if(!ipregexp.test(ip) || ip === '127.0.0.1' && !config.bot.development_mode) return await message.channel.send(embeds.embed("Invalid IP Range", constants.invalidrange));

    let linkingStatus = await axios.get(`http://${args[0]}/iproxy/setup/configuredserver`).catch(err => {logger.error(err.message)});

    // if the proxy returns a success code
    if(linkingStatus.data.success) {
        
        // if the id in the config is the same as the guild's, continue. If not, invalid ownership
        if(linkingStatus.data.id === guild) {

            // First check if the server is linked
            pool.checkServerLink(guild, async (res, err) => {
                // Standard error catching
                if(err) {
                    logger.error(err);
                    return await message.channel.send(embeds.embed("Internal Server Error", internal_error));
                }

                // If the server is already linked, notify the user of the fact
                if(res) return await message.channel.send(embeds.embed("Already Linked", constants.alreadylinked));

                // construct the json object to insert into the database
                if(!(args[1].length > 10)) return await message.channel.send(embeds.embed("Invalid Token Configuration", constants.invalidtoken));
                let tokens = JSON.stringify({tokens: [], server_token: args[1]});

                // Proceed to link server
                pool.linkServer(guild, args[0], tokens, async err => {
                    if(err) {
                        logger.error(err);
                        return await message.channel.send(embeds.embed("Internal Server Error", internal_error));
                    }

                    // Notify user of the linking success
                    return await message.channel.send(embeds.embed("Server Linked", constants.linked));
                });

            });
             
        } else message.channel.send(embeds.embed("Unable to prove Ownership", constants.invalidownership));

    } else await message.channel.send(embeds.embed("Internal Server Error", internal_error));

}

module.exports = { execute: setup };