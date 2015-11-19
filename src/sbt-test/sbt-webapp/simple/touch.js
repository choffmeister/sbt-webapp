var fs = require('fs');
var name = process.argv[2];

fs.writeFileSync('./' + name, name);

process.exit(name === 'test' ? 1 : 0);
