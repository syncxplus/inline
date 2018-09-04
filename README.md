# Build
```bash
tag=`date "+%Y-%m-%d"`
docker build -t inline:${tag} .
```
# Run
```bash
docker run --name inline --restart always -p 8080:8080 \
  -v ~/.gradle/caches:/root/.gradle/caches -d inline:${tag} \
  --spring.application.json='{"outline.server":{"host":"ip","port":0,"key":"key"},"spring.security.user.password":"123456"}'
```
- **outline.server**
  - host: ss ip
  - port: ss port
  - key: ss secret key
- **spring.security.user.password**
  - password: password for REST API

# REST API
[Outline API Reference](outline.md)  
