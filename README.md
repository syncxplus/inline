![logo](testbird.png) ![outline](https://getoutline.org/modern/img/outline-logo-and-text.png)
```
docker run --restart always --name inline -d --net host -p 8080:8080 --privileged -v /root/logs:/usr/bin/app/logs syncxplus/inline:${VERSION:-latest}
```
