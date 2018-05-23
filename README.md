# EmbeddedNatsServer
Embedded NatsServer for testing which contains the original NatsServer
[![License][License-Image]][License-Url]
[![Build][Build-Status-Image]][Build-Status-Url] 

### Features
* One annotation to setup server the powerful nats server
```
@SpringBootTest
@RunWith(SpringRunner.class)
@EnableNatsServer(port = 4222, natsServerConfig = {"user:admin", "password:admin"})
public class SomeTest {
    [...]
}
```

### TODO
* Autoconfig for live usage
* Parameter table
* Read command errors for exceptions

![EmbeddedNatsServer](src/test/resources/banner.png "EmbeddedNatsServer")

[License-Url]: https://www.apache.org/licenses/LICENSE-2.0
[License-Image]: https://img.shields.io/badge/License-Apache2-blue.svg
[github-release]: https://github.com/YunaBraska/EmbeddedNatsServer
[Build-Status-Url]: https://travis-ci.org/YunaBraska/EmbeddedNatsServer
[Build-Status-Image]: https://travis-ci.org/YunaBraska/EmbeddedNatsServer.svg?branch=master
