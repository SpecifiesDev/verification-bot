const embeds = require('../utils/embeds');
const logger = require('../utils/Logger.js');
const utils = require("../utils/utils.js");
const fs = require('fs');
const axios = require('axios');

const config = JSON.parse(fs.readFileSync(`${__dirname.split('commands')[0]}manifest.json`));

const constants = config.bot.command_constants.verify;
const internal_error = config.bot.command_constants.internal_error;

const verify = async (message, args, pool, guild) => {

    if(args.length == 0) return await message.channel.send(embeds.embed("Invalid Arguments", utils.replacePlaceholders(constants.invalidargs, "verify")));

    pool.checkServerLink(guild, async (result, err) => {
        if(err) {
            logger.error(err);
            return await message.channel.send(embeds.embed("Internal Server Error", internal_error));
        }

        if(!result) return await message.channel.send(embeds.embed("Not Setup", config.bot.command_constants.invalidsetup));
        
        pool.checkVerificationCode(guild, args[0], async (res, err) => {
            if(err) {
                logger.error(err);
                return await message.channel.send(embeds.embed("Internal Server Error", internal_error));
            }

            if(res.length == 0) return await message.channel.send(embeds.embed("Invalid Code", constants.invalidcode));

            let data = res[0];
            let newInsertion = {linkStatus: 1, discordID: message.author.id};

            pool.updatePlayer(guild, data.uuid, newInsertion, async err => {
                if(err) {
                    logger.error(err);
                    return await message.channel.send(embeds.embed("Internal Server Error", internal_error));
                }

                await message.channel.send(embeds.embed("Successfully Linked", constants.linked));
            });

        });
    });
}

module.exports = { execute: verify };