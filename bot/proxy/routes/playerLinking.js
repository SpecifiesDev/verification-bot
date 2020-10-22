const express = require('express');
const fs = require('fs');
const manager = require('../../utils/SQLManager');


// setup the router
const router = express.Router();

router.get('/linking/status/:UUID', (req, res) => {
    if(!req.headers.token) return res.status(403).json({message: "No credentials were sent."});

    let token = req.headers.token;
    let id = req.headers.server;

    if(!id) return res.status(400).json({message: "Invalid request."});

    manager.getServerTokens(id, (result, err) => {
        if(err) return res.status(500);

        if(result.length == 0) return res.status(403);

        if(!(JSON.parse(result).tokens.includes(token))) return res.status(403);

        manager.getPlayer(id, req.params.UUID, async (playerResult, err) => {
            if(err) return res.status(500);

            if(playerResult.length == 0) res.json({success: true, linked: false, pending: false})
            else { 
                let data = playerResult[0];

                if(data.linkStatus == 0) return res.json({success: true, linked: false, pending: true, code: data.code})
                else if(data.linkStatus == 1) return res.json({success: true, linked: true});
            }
        });

    });


});

router.post('/linking/setstatus/pending', (req, res) => {

    let uuid = req.body.UUID;
    let token = req.body.token;
    let id = req.body.server;

    if(!uuid || !token || !id) return res.status(400).json({message: "Invalid Request"});

    manager.getServerTokens(id, (result, err) => {
        if(err) return res.status(500);
        if(result.length == 0) return res.status(403);

        if(!(JSON.parse(result).tokens.includes(token))) return res.status(403);

        let code = `${genThree()}-${genThree()}-${genThree()}`;

        manager.createPlayer(id, uuid, code, err => {
            if(err) res.json({success: false, message: "Internal Server Error"});

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