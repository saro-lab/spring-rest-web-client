package share.server

import me.saro.ext.spring.rwc.annotation.EnableRestWebClient
import org.springframework.boot.SpringBootConfiguration
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.context.annotation.ComponentScan
import org.springframework.web.reactive.config.EnableWebFlux

@SpringBootConfiguration
@EnableAutoConfiguration
@EnableWebFlux
@EnableRestWebClient(basePackages = ["jtest", "ktest"])
@ComponentScan(basePackages = ["share.*"])
class App