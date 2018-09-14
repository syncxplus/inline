# Build
```bash
tag=`date "+%Y-%m-%d"`
docker build -t inline:${tag} .
```
# Run
```bash
docker run --name inline --restart always -p 8080:8080 \
  -v ~/.gradle/caches:/root/.gradle/caches -d inline:${tag} \
  --spring.application.json='{"outline.server.host":"${ip}"}'
```

# REST API
[Outline API Reference](outline.md)  
