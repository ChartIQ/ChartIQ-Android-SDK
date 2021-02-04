### How to build documentation

Documentation is built with Dokka using the `dokkaHtml` task. Configuration in chartiq/build.gradle.

Documentation also includes chartiq styling, which is in chartiq/docs/chartiq.css. In order to apply that to the docs, copy the contents and add them to chartiq/docs/styles/main.css after running `dokkaHtml`.