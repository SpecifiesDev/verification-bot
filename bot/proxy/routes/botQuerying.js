const logger = require("../../utils/Logger");

const getUserName = (req, res, client, manager) => {
    if(!req.headers.token) return res.status(403).json({message: "No credentials were sent."});

    let id = req.headers.server;
    let token = req.headers.token;

    manager.getServerTokens(id, (result, err) => {

        if(err) return res.status(500).json({success: false, message: "Internal Server Error."});

        if(result.length == 0) return res.status(403);

        let parsedJSON = JSON.parse(result);
    
        if(!(parsedJSON.tokens.includes(token))) {
            if(!parsedJSON.server_token == token) return res.status(403);
        }


        manager.getPlayer(id, req.params.UUID, (result, err) => {
            if(err) return res.status(500).json({success: false, message: "Internal Server Error."});
    
            if(result.length == 0) return res.json({success: false, message: "Player Doesn't Exist."});
            client.users.fetch(result[0].discordID).then(user => {
                res.json({success: true, tag: `${user.username}#${user.discriminator}`});
            }).catch(err => {
                logger.err(err);
                res.json({success: false, message: "Not found."});
            });
            
        });
    });
}

const getStatus = async (req, res, client, manager) => {
    if(!req.headers.token) return res.status(403).json({message: "No credentials were sent."});

    let id = req.headers.server;
    let token = req.headers.token;
    manager.getServerTokens(id, (result, err) => {

        if(err) return res.status(500).json({success: false, message: "Internal Server Error."});

        if(result.length == 0) return res.status(403);

        let parsedJSON = JSON.parse(result);
    
        if(!(parsedJSON.tokens.includes(token))) {
            if(!parsedJSON.server_token == token) return res.status(403);
        }

        manager.getClientID(id, req.params.UUID, (result, err) => {
            if(err) return res.status(500).json({success: false, message: "Internal Server Error"});
    
            let resp = {};
    
            if(result.length == 0) return res.json({success: false, message: "User wasn't found in the database."});
    
            let cid = result[0].discordID;
            
            let user = client.users.cache.get(cid);

            if(user == undefined) return res.json({success: false, message: "No cached data on the user."});
            
            resp.status = user.presence.status;
            resp.success = true;
            for(activity of user.presence.activities) {
                if(activity.type === 'LISTENING') {
                    resp.song = `${activity.state.split(';')[0]} - ${activity.details}`;
                }
                if(activity.type === 'PLAYING') resp.game = activity.name;
                if(activity.type === 'CUSTOM_STATUS') resp.custom = activity.state;
            }

            res.json(resp);
            
    
        });

    });


}

module.exports = { 
    getUserName: getUserName,
    getUserStatus: getStatus
}