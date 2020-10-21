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
manager.linkServer = (id, ip, callback) => {

    console.log(id);
    pool.query(`CREATE TABLE IF NOT EXISTS \`${id}\`(ip TEXT, verified TEXT)`, err => {
        if(err) return logger.error(err);

        logger.info(`A new server has been linked. ID: ${id}`);
        
        pool.query(`INSERT INTO \`${id}\` (ip, verified) VALUES(?, ?)`, [ip, true], err => {
            if(err) return callback(err);

            callback();
        });

    });

}

/**
 * Function to drop a server link. The more we progress with the data schema, the more tables we'll need to add to this drop list.
 * @param {String} id 
 * @param {callback} callback 
 */
manager.deleteServerLink = async(id, callback) => {

    pool.query(`DROP TABLE \`${id}\``, err => {
        if(err) return callback(err);

        logger.info(`A server has been dropped from the database. ID: ${id}`);

        callback();
    });
}

/**
 * Function to check if a server is linked.
 * @param {*} id 
 * @param {*} callback 
 */
manager.checkServerLink =  async (id, callback) => {

    pool.query(`SELECT table_name FROM information_schema.tables WHERE table_schema = ? AND table_name = ?`, [config.sql.database, id], (err, res) => {
        if(err) return callback("", err);
        if(res.length == 0) callback(false);
        else callback(true);
    });

    
}





module.exports = manager;
