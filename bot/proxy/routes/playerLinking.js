const express = require('express');
const manager = require('../../utils/SQLManager');


// setup the router
const router = express.Router();

router.get('/linking/status/:UUID', (req, res) => {
    // Ensure that credentials were received
    if(!req.headers.token) return res.status(403).json({message: "No credentials were sent."});

    // parse the headers for the data we need
    let token = req.headers.token;
    let id = req.headers.server;

    // Ensure that an id was sent
    if(!id) return res.status(400).json({message: "Invalid request."});

    // Get a server's deployed api tokens
    manager.getServerTokens(id, (result, err) => {
        // error handling
        if(err) return res.status(500);

        // if there are results, just return a 403
        if(result.length == 0) return res.status(403);

        // parse the json for the token array, if the sent token isn't in the server's cached tokens, 403 the request
        if(!(JSON.parse(result).tokens.includes(token))) return res.status(403);

        // get the player by the sent id
        manager.getPlayer(id, req.params.UUID, async (playerResult, err) => {
            if(err) return res.status(500);

            // If not result was found, the player isn't in the table. Tell the user that they need to be created.
            if(playerResult.length == 0) res.json({success: true, linked: false, pending: false})
            else { 
                // parse the object
                let data = playerResult[0];

                // 0 for not linked, 1 for linked. If it's a 0, and an object was found in the database we can assume that it's pending.
                if(data.linkStatus == 0) return res.json({success: true, linked: false, pending: true, code: data.code})
                else if(data.linkStatus == 1) return res.json({success: true, linked: true});
            }
        });

    });


});

router.post('/linking/setstatus/pending', (req, res) => {

    // parse the body for the values
    let uuid = req.body.UUID;
    let token = req.body.token;
    let id = req.body.server;

    // ensure credentials were sent
    if(!token) return res.status(403).json({message: "No credentials were sent."});

    // ensure the values aren't null
    if(!uuid || !id) return res.status(400).json({message: "Invalid Request"});

    // validate token
    manager.getServerTokens(id, (result, err) => {
        if(err) return res.status(500);
        if(result.length == 0) return res.status(403);

        if(!(JSON.parse(result).tokens.includes(token))) return res.status(403);

        // Generate a new code for the player
        let code = `${genThree()}-${genThree()}-${genThree()}`;

        // create the player
        manager.createPlayer(id, uuid, code, (result, err) => {
                if(err) return res.json({success: false, message: "Internal Server Error"});

                // if the player already exists, notify the person
                if(result) return res.json({success: false, message: "Player Already Exists"});

                // send a success message, with the code
                res.json({success: true, code: code});
        });



    });
    

});

let genThree = () => {

    let result = '';

    for(let i = 0; i < 3; i++) result += '1234567890'.charAt(Math.floor(Math.random() * 10));

    return result;
}


module.exports = router;