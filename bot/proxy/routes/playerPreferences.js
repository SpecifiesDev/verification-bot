const express = require('express');
const manager = require('../../utils/SQLManager');
const logger = require('../../utils/Logger');


// setup the router
const router = express.Router();

router.get('/preferences/player/:UUID', (req, res) => {
    // Ensure that credentials were received
    if(!req.headers.token) return res.status(403).json({message: "No credentials were sent."});

    // parse the headers for the data we need
    let token = req.headers.token;
    let uuid = req.params.UUID;
    let id = req.headers.server;

    if(!uuid || !id) return res.json({success: false, message: "Missing Parameters."});

    manager.getServerTokens(id, (result, err) => {
        if(err) {
            logger.error(err);
            return res.json({success: false, message: "Internal Server Error."});
        }

        if(result.length == 0) return res.status(403);
        
        let parsedJSON = JSON.parse(result);
    
        if(!(parsedJSON.tokens.includes(token))) {
            if(!parsedJSON.server_token == token) return res.status(403);
        }

        manager.getPlayerPreferences(id, uuid, (result, err) => {
            if(err) {
                logger.error(err);
                return res.status(500).json({success: false, message: "Internal Server Error", code: "ISE"});
            }
    
            
            if(result.length == 0) return res.json({success: false, message: "Player Doesn't Exist", code: "DNE"});
    
            let data = result[0];
    
            data.success = true;
            
            res.json(data);
    
        });
    });

});

router.post('/preferences/players/set', (req, res) => {
        // parse the body for the values
        let uuid = req.body.UUID;
        let token = req.body.token;
        let id = req.body.server;

        let status = req.body.status;
        let message = req.body.message;
        let chat = req.body.chat;
    
        // ensure credentials were sent
        if(!token) return res.status(403).json({message: "No credentials were sent."});
    
        // ensure the values aren't null
        if(!uuid || !id || !status || !message || !chat) return res.status(400).json({message: "Invalid Request"});

        manager.getServerTokens(id, (result, err) => {
            if(err) return res.status(500);
            if(result.length == 0) return res.status(403);

            let parsedJSON = JSON.parse(result);
    
            if(!(parsedJSON.tokens.includes(token))) {
                if(!parsedJSON.server_token == token) return res.status(403);
            }
        
            manager.setPlayerPreferences(id, {status: status, message: message, chat: chat, uuid: uuid}, err => {
                if(err) {
                    logger.error(err);
                    return res.json({success: false, message: "Internal Server Error"});
                }

                res.json({success: true, message: "Player preferences updated."});
            });

        });
});

module.exports = router;