const embeds = require('../utils/embeds');

const execute = async message => {
    const deleteAfter = await message.channel.send("Calculating...");

    await message.channel.send(embeds.embed("Pong.", `Response latency is ${deleteAfter.createdTimestamp - message.createdTimestamp}ms.`));

    (await deleteAfter).delete();

    return;
}

module.exports = { execute: execute };