const fs = require('fs');

const config = JSON.parse(fs.readFileSync(`${__dirname.split('utils')[0]}manifest.json`));

const replacePlaceholders = (string, command) => {

    // create a map of our placeholders
    let map = {
        "%prefix%": config.bot.prefix,
        "%command%": command
    };

    // run a regexp over the string, and replace all placeholders
    return string.replace(/%[^%]+%/g, match => {
        if(match in map) return(map[match])
        else return("");
    });

    

}



module.exports = { replacePlaceholders: replacePlaceholders };