const express = require('express');
const bodyparser = require('body-parser');
const cors = require('cors');
const fs = require('fs');
const logger = require('../utils/Logger');

// routes
const playerLinking = require('./routes/playerLinking.js');
const apiStatus = require('./routes/apiStatus.js');

// setup and configure the server objects
const app = express();
app.use(bodyparser.json());
app.use(bodyparser.urlencoded({extended : true}));
app.use(cors());

// pull configuration
const config = JSON.parse(fs.readFileSync(`${__dirname.split('proxy')[0]}manifest.json`));

const url = config.proxy.base_url;

// Use the routes
app.use(url, playerLinking);
app.use(url, apiStatus);

// Initialize server
const init = () => {
    app.listen(config.proxy.port, () => {
        logger.info(`Internal Proxy started on port: ${config.proxy.port}`);
    });
}


module.exports = { start: init };
