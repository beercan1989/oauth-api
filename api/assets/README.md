# Assets Endpoint

Covers the endpoint that provides all the static assets, in a cached form, could be replaced later by a different setup.

Ultimate idea would be that all the static assets are published to a CDN and not committed into this module. 

## How to build the assets

### [src/main/resources/static/js/authentication.js](src/main/resources/static/js/authentication.js)

 ```bash
cd ../../user-interface/authentication

npm run build

cp build/static/js/main.*.js ../../api/assets/src/main/resources/static/js/authentication.js
 ```

Reverse helper for those on Windows because of https://youtrack.jetbrains.com/issue/IDEA-294997
```bash
cp build/static/js/main.*.js ../../api/assets/src/main/resources/static/js/authentication.js

npm run build

cd ../../user-interface/authentication
```