var gulp = require('gulp'),
    fs = require('fs');

gulp.task('test', function () {
  console.log('test');
  fs.writeFileSync('./test', 'test');
});

gulp.task('build', function () {
  console.log('build');
  fs.writeFileSync('./build', 'build');
});

gulp.task('run', function () {
  console.log('run');
  fs.writeFileSync('./run', 'run');
});
