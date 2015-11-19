# sbt-webapp

[![build](https://img.shields.io/circleci/project/choffmeister/sbt-webapp/develop.svg)](https://circleci.com/gh/choffmeister/sbt-webapp/tree/develop)
[![maven](https://img.shields.io/maven-central/v/de.choffmeister/sbt-webapp.svg)](http://search.maven.org/#search%7Cga%7C1%7Cg%3A%22de.choffmeister%22%20AND%20a%3A%22sbt-webapp%22)
[![license](https://img.shields.io/badge/license-MIT-lightgrey.svg)](http://opensource.org/licenses/MIT)

Integrates [NodeJS][nodejs]/[NPM][npm] into your SBT build process.

## Usage

~~~ bash
# runs npm install
$ sbt npmInstall

# runs npm install (silent) and npm test
$ sbt npmTest

# runs npm install (silent) and npm run build
$ sbt npmBuild

# asynchronously runs npm start
$ sbt npmStart

# stop asynchronously started npm start
$ sbt npmStop
~~~

For more complex project examples please look into the `src/sbt-test/sbt-webapp` folder.

## License

Published under the permissive [MIT][mit] license.

[mit]: http://opensource.org/licenses/MIT
[nodejs]: https://nodejs.org/
[npm]: https://www.npmjs.org/
