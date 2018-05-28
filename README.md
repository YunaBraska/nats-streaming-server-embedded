# Nats Streaming Server Embedded

[![License][License-Image]][License-Url]
[![Build][Build-Status-Image]][Build-Status-Url] 
[![Coverage][Coverage-image]][Coverage-Url] 
[![Version][Version-image]][Version-Url] 
[![Maintainable][Maintainable-image]][Maintainable-Url] 
[![Javadoc][javadoc-image]][javadoc-Url]

### Description
Embedded [Nats streaming server](https://github.com/nats-io/nats-streaming-server) for testing which contains the original [Nats streaming server](https://github.com/nats-io/nats-streaming-server) 

### Usage
* One annotation to setup the powerful [Nats streaming server](https://github.com/nats-io/nats-streaming-server)
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
* Spring boot autoconfig for live usage
* Parameter class with description
* Read command errors for exceptions
* Port issues on CI - Seems that the test context is restarting much faster than the ports are shutting down - at least some times

![nats-streaming-server-embedded](src/test/resources/banner.png "nats-streaming-server-embedded")

[License-Url]: https://www.apache.org/licenses/LICENSE-2.0
[License-Image]: https://img.shields.io/badge/License-Apache2-blue.svg
[github-release]: https://github.com/YunaBraska/nats-streaming-server-embedded
[Build-Status-Url]: https://travis-ci.org/YunaBraska/nats-streaming-server-embedded
[Build-Status-Image]: https://travis-ci.org/YunaBraska/nats-streaming-server-embedded.svg?branch=master
[Coverage-Url]: https://codecov.io/gh/YunaBraska/nats-streaming-server-embedded?branch=master
[Coverage-image]: https://codecov.io/gh/YunaBraska/nats-streaming-server-embedded/branch/master/graphs/badge.svg
[Version-url]: https://github.com/YunaBraska/nats-streaming-server-embedded
[Version-image]: https://badge.fury.io/gh/YunaBraska%2Fnats-streaming-server-embedded.svg
[Maintainable-Url]: https://codeclimate.com/github/YunaBraska/nats-streaming-server-embedded
[Maintainable-image]: https://codeclimate.com/github/YunaBraska/nats-streaming-server-embedded.svg
[Javadoc-url]: https://github.com/YunaBraska/nats-streaming-server-embedded
[Javadoc-image]: http://javadoc.io/badge/github/YunaBraska/nats-streaming-server-embedded.svg