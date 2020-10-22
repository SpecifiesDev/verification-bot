// import libraries
const embeds = require('../utils/embeds');
const logger = require('../utils/Logger.js');
const utils = require("../utils/utils.js");
const fs = require('fs');

// import configuration
const config = JSON.parse(fs.readFileSync(`${__dirname.split('commands')[0]}manifest.json`));

// pull message constants
const constants = config.bot.command_constants.verify;
const internal_error = config.bot.command_constants.internal_error;

// Execute function for verify command.
const verify = async (message, args, pool, guild) => {

    // Ensure that the command is passed enough arguments
    if(args.length == 0) return await message.channel.send(embeds.embed("Invalid Arguments", utils.replacePlaceholders(constants.invalidargs, "verify")));

    // Check if the server is linked
    pool.checkServerLink(guild, async (result, err) => {
        // Standard error logging.. might clean this up later to have a dedicated function to make this one line
        if(err) {
            logger.error(err);
            return await message.channel.send(embeds.embed("Internal Server Error", internal_error));
        }

        // If the guild is linked to a server, notify them of the fact
        if(!result) return await message.channel.send(embeds.embed("Not Setup", config.bot.command_constants.invalidsetup));
        
        // Pull the verification information of a player by the inserted code
        pool.checkVerificationCode(guild, args[0], async (res, err) => {
            if(err) {
                logger.error(err);
                return await message.channel.send(embeds.embed("Internal Server Error", internal_error));
            }

            // If the pulled array is empty, it means that there were no objects found.
            if(res.length == 0) return await message.channel.send(embeds.embed("Invalid Code", constants.invalidcode));

            // parse the object
            let data = res[0];

            // create a new insertion map
            let newInsertion = {linkStatus: 1, discordID: message.author.id};

            // update the player
            pool.updatePlayer(guild, data.uuid, newInsertion, async err => {
                if(err) {
                    logger.error(err);
                    return await message.channel.send(embeds.embed("Internal Server Error", internal_error));
                }

                // notify the user that their accounts were linked
                await message.channel.send(embeds.embed("Successfully Linked", constants.linked));
            });

        });
    });
}

module.exports = { execute: verify };