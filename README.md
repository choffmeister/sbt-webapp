# sbt-webapp

Integrates [NodeJS][nodejs]/[NPM][npm] into your SBT build process.

## Usage

~~~ bash
# runs npm install and npm test
$ sbt npmTest

# runs npm install and npm run build
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
