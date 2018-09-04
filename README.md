# Build
```bash
tag=`date "+%Y-%m-%d"`
docker build -t inline:${tag} .
```
# Run
```bash
docker run --name inline --restart always -p 8080:8080 \
  -v ~/.gradle/caches:/root/.gradle/caches -d inline:${tag} \
  --spring.application.json='{"outline.server":{"host":"ip","port":0,"key":"key"},"api-key":"username:password"}'
```
- **outline.server**
  - host: ss ip
  - port: ss port
  - key: ss secret key
- **api-key**
  - [BASIC AUTH](https://en.wikipedia.org/wiki/Basic_access_authentication#Client_side) for REST API

# REST API
[Outline API Reference](outline.md)  
