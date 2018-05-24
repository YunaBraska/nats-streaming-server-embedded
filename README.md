# EmbeddedNatsServer
Embedded NatsServer for testing which contains the original NatsServer

[![License][License-Image]][License-Url]
[![Build][Build-Status-Image]][Build-Status-Url] 
[![Coverage][Coverage-image]][Coverage-Url] 
[![Coverage][Version-image]][Version-Url] 
[![Maintainable][Maintainable-image]][Maintainable-Url] 
[![Javadoc][javadoc-image]][javadoc-Url] 

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
* calculate on adding parameter
* Autoconfig for live usage
* Parameter table
* Read command errors for exceptions
* Port issues on CI - Seems that the test context is restarting much faster than the ports are shutting down - at least some times
* tag automatically new versions with [mvn scm:tag]

![EmbeddedNatsServer](src/test/resources/banner.png "EmbeddedNatsServer")

[License-Url]: https://www.apache.org/licenses/LICENSE-2.0
[License-Image]: https://img.shields.io/badge/License-Apache2-blue.svg
[github-release]: https://github.com/YunaBraska/EmbeddedNatsServer
[Build-Status-Url]: https://travis-ci.org/YunaBraska/EmbeddedNatsServer
[Build-Status-Image]: https://travis-ci.org/YunaBraska/EmbeddedNatsServer.svg?branch=master
[Coverage-Url]: https://codecov.io/gh/YunaBraska/EmbeddedNatsServer?branch=master
[Coverage-image]: https://codecov.io/gh/YunaBraska/EmbeddedNatsServer/branch/master/graphs/badge.svg
[Version-url]: https://github.com/YunaBraska/EmbeddedNatsServer
[Version-image]: https://badge.fury.io/gh/YunaBraska%2FEmbeddedNatsServer.svg
[Maintainable-Url]: https://codeclimate.com/github/YunaBraska/EmbeddedNatsServer
[Maintainable-image]: https://codeclimate.com/github/YunaBraska/EmbeddedNatsServer.svg
[Javadoc-url]: https://github.com/YunaBraska/EmbeddedNatsServer
[Javadoc-image]: http://javadoc.io/badge/github/YunaBraska/EmbeddedNatsServer.svg