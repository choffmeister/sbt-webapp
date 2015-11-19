var fs = require('fs');
var name = process.argv[2];

fs.writeFileSync('./' + name, name);
