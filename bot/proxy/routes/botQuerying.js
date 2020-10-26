const getUserName = (req, res, client, manager) => {
    if(!req.headers.token) return res.status(403).json({message: "No credentials were sent."});

    let id = req.headers.server;
    let token = req.headers.token;

    manager.getServerTokens(id, (result, err) => {

        if(err) return res.status(500).json({success: false, message: "Internal Server Error."});

        if(result.length == 0) return res.status(403);

        if(!(JSON.parse(result).tokens.includes(token))) return res.status(403);


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

module.exports = { 
    getUserName: getUserName
}