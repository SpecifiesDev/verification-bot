const sql = require('mysql');
const fs = require('fs');
const logger = require('./Logger.js');

const config = JSON.parse(fs.readFileSync(`${__dirname.split('utils')[0]}manifest.json`));

let pool = sql.createPool({
    host: config.sql.host,
    user: config.sql.user,
    password: config.sql.password,
    database: config.sql.database
});

let manager = {};

/**
 * Function to link a discord server to a minecraft server. The more we progress with the data schema, the more tables we'll need to add to this.
 * @param {String} id 
 * @param {String} ip 
 * @param {callback} callback 
 */
manager.linkServer = (id, ip, tokens, callback) => {

    pool.query(`CREATE TABLE IF NOT EXISTS \`${id}\`(ip TEXT, verified TEXT, tokens TEXT)`, err => {
        if(err) {
            logger.error(err);
            return callback(err);
        }

        logger.info(`A new server has been linked. ID: ${id}`);
        
        pool.query(`CREATE TABLE IF NOT EXISTS \`${id}_players\`(uuid TEXT, linkStatus TEXT, discordID TEXT, code TEXT, status TEXT, message TEXT, chat TEXT)`, err => {
            if(err) {
                logger.error(err);
                return callback(err);
            }

            pool.query(`INSERT INTO \`${id}\` (ip, verified, tokens) VALUES(?, ?, ?)`, [ip, true, tokens], err => {
                if(err) return callback(err);
    
                callback();
            });
        })

    });

}

manager.getClientID = (id, uuid, callback) => {

    pool.query(`SELECT discordID from \`${id}_players\` WHERE uuid = ?`, (err, res) => {

        if(err) return callback("", err);

        callback(res);

    });
}

/**
 * Function to pull a player's data from a connected discord server.
 * @param {String} id 
 * @param {String} uuid 
 * @param {callback} callback 
 */
manager.getPlayer = (id, uuid, callback) => {


    pool.query(`SELECT * FROM \`${id}_players\` WHERE uuid = ?`, [uuid], (err, res) => {
        if(err) return callback("", err);
        if(res.length == 0) return callback([]);
        callback(res);
    });

}

/**
 * Function to delete a player from a server table.
 * @param {String} id 
 * @param {String} uuid 
 * @param {callback} callback 
 */
manager.deletePlayer = (id, uuid, callback) => {

    pool.query(`DELETE FROM \`${id}_players\` WHERE uuid = ?`, [uuid], err => {
        if(err) return callback(err);
        
        logger.info(`A player from the server ${id} has been removed from the players table.`);
        callback();
    });

}

/**
 * Function to update a player's link status. Might rename this later for more clarity.
 * @param {String} id 
 * @param {String} uuid 
 * @param {Object} insertion 
 * @param {callback} callback 
 */
manager.updatePlayer = (id, uuid, insertion, callback) => {

    pool.query(`UPDATE \`${id}_players\` SET linkStatus = ?, discordID = ? WHERE uuid = ?`, [insertion.linkStatus, insertion.discordID, uuid], err => {
        if(err) return callback(err);

        callback();
    });
}

/**
 * Function to check verification status via a player code.
 * @param {String} id 
 * @param {String} code 
 * @param {callback} callback 
 */
manager.checkVerificationCode = (id, code, callback) => {

    pool.query(`SELECT * FROM \`${id}_players\` WHERE code = ?`, [code], (err, res) => {
        if(err) return callback("", err);
        
        callback(res);
    });
}

/**
 * Function to create a new player in the database.
 * @param {String} id 
 * @param {String} uuid 
 * @param {String} code 
 * @param {callback} callback 
 */
manager.createPlayer = (id, uuid, code, callback) => {

    manager.getPlayer(id, uuid, (res, err) => {
        if(err) return callback("", err);

        if(res.length > 1) return callback("Already exists");

        pool.query(`INSERT INTO \`${id}_players\` (uuid, linkStatus, discordID, code, status, message, chat) VALUES (?, 0, 0, ?, 1, 1, 1)`, [uuid, code], err => {
            if(err) {
                logger.error(err);
                return callback(err);
            }
    
            callback();
        });
    });

}

/**
 * Function to get a server's valid tokens. Querying body should do standard null checks to ensure that there are tokens in storage.
 * @param {String} id 
 * @param {callback} callback 
 */
manager.getServerTokens = (id, callback) => {

    pool.query(`SELECT tokens FROM \`${id}\``, (err, res) => {
        if(err) return callback("", err);

        callback(res[0].tokens);
    });

}

manager.getPlayerPreferences = (id, uuid, callback) => {

    pool.query(`SELECT status, message, chat from \`${id}_players\` WHERE uuid = ?`, [uuid], (err, res) => {
        if(err) return callback("", err);

        callback(res);

    });
}

/**
 * Function to drop a server link. The more we progress with the data schema, the more tables we'll need to add to this drop list.
 * @param {String} id 
 * @param {callback} callback 
 */
manager.deleteServerLink = async(id, callback) => {

    pool.query(`DROP TABLE IF EXISTS \`${id}\``, err => {
        if(err) return callback(err);

        pool.query(`DROP TABLE IF EXISTS \`${id}_players\``, err => {
            if(err) return callback(err);

            logger.info(`A server has been dropped from the database. ID: ${id}`);

            callback();
        });
    });
}

/**
 * Function to check if a server is linked.
 * @param {String} id 
 * @param {String} callback 
 */
manager.checkServerLink =  async (id, callback) => {

    pool.query(`SELECT table_name FROM information_schema.tables WHERE table_schema = ? AND table_name = ?`, [config.sql.database, id], (err, res) => {
        if(err) return callback("", err);
        if(res.length == 0) callback(false);
        else callback(true);
    });

    
}





module.exports = manager;
