const discord = require("discord.js");
const fs = require('fs');

const config = JSON.parse(fs.readFileSync(`${__dirname.split('utils')[0]}manifest.json`));

const newEmbed = (title, desc) => {
    return new discord.MessageEmbed()
    .setColor(config.bot.color)
    .setTitle(title)
    .setDescription(desc)
    .setTimestamp()
    .setFooter(config.bot.author);
}

module.exports = { embed: newEmbed };