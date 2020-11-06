const logger = require("../../utils/Logger");
const embeds = require('../../utils/embeds');

const getUserName = (req, res, client, manager) => {
    if(!req.headers.token) return res.status(403).json({message: "No credentials were sent."});

    let id = req.headers.server;
    let token = req.headers.token;

    manager.getServerTokens(id, (result, err) => {

        if(err) {
            logger.error(err);
            return res.status(500).json({success: false, message: "Internal Server Error."});
        }

        if(result.length == 0) return res.status(403);

        let parsedJSON = JSON.parse(result);
    
        if(!(parsedJSON.tokens.includes(token))) {
            if(!(parsedJSON.server_token == token)) return res.status(403).json({success: false, message: "Invalid Authetication"});
        }


        // Get an object of the player so we can get their discord id.
        manager.getPlayer(id, req.params.UUID, (result, err) => {
            if(err) {
                logger.error(err);
                return res.status(500).json({success: false, message: "Internal Server Error."});
            }
    
            // if the player isn't in the db, return
            if(result.length == 0) return res.json({success: false, message: "Player Doesn't Exist."});

            // fetch the user
            client.users.fetch(result[0].discordID).then(user => {
                // send a formatted response
                res.json({success: true, tag: `${user.username}#${user.discriminator}`});
            }).catch(err => {
                // user can't be found in any guilds
                logger.err(err);
                res.json({success: false, message: "Not found."});
            });
            
        });
    });
}

const sendDM = async(req, res, client, manager) => {

    if(!req.body.token) return res.status(403).json({message: "No credentials were sent."});

    // get all of the body

    // id of the linked server
    let id = req.body.server;

    // api-token
    let token = req.body.token;
    
    // uuid of the target user to send the message to
    let uuid = req.body.UUID;

    // the message to send to the target user
    let message = req.body.message;

    // sender data
    let sender_uuid = req.body.sender_uuid;
    let sender_name = req.body.sender_name;

    if(!id || !uuid || !message || !sender_uuid || !sender_name) return res.json({success: false, message: "Missing Parameters"});


    manager.getServerTokens(id, (result, err) => {
        if(err) {
            logger.error(err);
            return res.status(500).json({success: false, message: "Internal Server Error"});
        }

        if(result.length == 0) return res.status(403);

        let parsedJSON = JSON.parse(result);
    
        if(!(parsedJSON.tokens.includes(token))) {
            if(!(parsedJSON.server_token == token)) return res.status(403).json({success: false, message: "Invalid Authetication"});
        }

        // get an object of the player in order 
        manager.getPlayer(id, uuid, (result, err) => {
            if(err) {
               logger.error(err);   
               return res.status(500).json({success: false, message: "Internal Server Error."});
            }

            if(result.length == 0) return res.json({success: false, message: "Player Doesn't Exist."});
            
            // check if they have the feature turned off, send a custom code so that the plugin can indicate to the command issuer
            if(result[0].message == 0) return res.json({success: false, message: "Player has the feature turned off", code: 1});

            // fetch the guild
            client.guilds.fetch(id).then(guild => {

                // construct a new embed
                let embed = embeds.playerMessages(`Linked Discord Message from ${guild.name}`, `The player "${sender_name}" has messaged you in-game. You have 30 seconds to respond to them.`, [{title: "Message", desc: message, inline: true}], sender_uuid);

                // send the user the embed
                guild.member(result[0].discordID).send(embed).then(message => {

                    // create a collector with a timeout of 30 seconds. Max it at one, and then handle this. Will create seamless communication between both players.
                    const collector = message.channel.createMessageCollector(m => m, {max: 1, time: 30000});
                    
                    collector.on('collect', m => {
                        // Whenever the functionality is complete we'll simply just send a request to the server indicating the response message
                        console.log(m);
                    });
        
        
                    res.json({success: true});
                });
            });

            
        });
    });

}

const getStatus = async (req, res, client, manager, active) => {
    if(!req.headers.token) return res.status(403).json({message: "No credentials were sent."});

    // pull all the data we need
    let id = req.headers.server;
    let token = req.headers.token;


    manager.getServerTokens(id, (result, err) => {
        if(err) {
            logger.error(err);   
            return res.status(500).json({success: false, message: "Internal Server Error."});
        }

        if(result.length == 0) return res.status(403);

        let parsedJSON = JSON.parse(result);

        if(!(parsedJSON.tokens.includes(token))) {
            if(!(parsedJSON.server_token == token)) return res.status(403).json({success: false, message: "Invalid Authetication"});
        }

        // get the client id of the queried user
        manager.getClientID(id, req.params.UUID, (result, err) => {
            if(err) {
                logger.error(err);
                return res.status(500).json({success: false, message: "Internal Server Error"});
            }
            
            manager.getServerIP(id, (response, err) => {
                if(err) {
                    logger.error(err);
                    return res.status(500).json({success: false, message: "Internal Server Error"});
                }

                if(response.length == 0) return res.json({success: false, message: "It would appear this server isn't linked properly."});

                // put a new response object, making this easier
                let resp = {};
        
                // ensure that the user is actually in the database.
                if(result.length == 0) return res.json({success: false, message: "User wasn't found in the database."});
        
                // get the id
                let cid = result[0].discordID;
                
                // get the cached user
                let user = client.users.cache.get(cid);

                // if the user isn't cached, send a response saying so
                if(user == undefined) return res.json({success: false, message: "No cached data on the user."});
                
                // get the status (dnd/away/online etc..)
                resp.status = user.presence.status;
                
                // the response will be successful
                resp.success = true;

                // loop through the activities and construct them into an appropriate response
                // I'm aware that if they're playing more than one game, only one will be sent, but I'm not terribly worried about it
                for(activity of user.presence.activities) {
                    if(activity.type === 'LISTENING') {
                        resp.song = `${activity.state.split(';')[0]} - ${activity.details}`;
                    }
                    if(activity.type === 'PLAYING') resp.game = activity.name;
                    if(activity.type === 'CUSTOM_STATUS') resp.custom = activity.state;
                }

                if(active.has(cid)) {
                    let array = active.get(cid);
                    let count = 0;
                    for(object of array) {
                        if(object.guild == id) count++;
                    }
                    if(count == 0) array.push({id: cid, uuid: req.params.UUID, guild: id, ip: response[0].ip})
                    active.set(cid, array);
                } else active.set(cid, [{id: cid, uuid: req.params.UUID, guild: id, ip: response[0].ip}]);
                

                // response
                res.json(resp);

            });
            
    
        });

    });


}


module.exports = { 
    getUserName: getUserName,
    getUserStatus: getStatus,
    sendDM: sendDM
};