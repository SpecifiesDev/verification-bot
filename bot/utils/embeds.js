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

const playerMessages = (title, desc, fields, uuid) => {
    let embed = new discord.MessageEmbed()
    .setColor(config.bot.color)
    .setTitle(title)
    .setDescription(desc)
    .setThumbnail(`https://crafatar.com/avatars/${uuid}`)
    .setTimestamp()
    .setFooter(config.bot.author);

    for(field of fields) {
        embed.addField(field.title, field.desc, field.inline);
    }

    return embed;

}

module.exports = { embed: newEmbed, playerMessages: playerMessages };