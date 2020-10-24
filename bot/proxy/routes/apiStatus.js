const express = require('express');
const manager = require('../../utils/SQLManager');


// setup the router
const router = express.Router();


router.get('/status', (req, res) => {
    res.json({success: true});
});


module.exports = router;