package ktest

import me.saro.ext.spring.rwc.EnableRestWebClient
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.SpringBootConfiguration
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.ComponentScan
import org.springframework.web.reactive.config.EnableWebFlux
import share.model.DataItem

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@SpringBootConfiguration
@EnableAutoConfiguration
@EnableWebFlux
@EnableRestWebClient
@ComponentScan(basePackages = ["share.*"])
class BasicTest{
    @Autowired
    private lateinit var testClient: TestKotlinClient

    @Value("\${client.local.token}")
    private lateinit var token: String

    @Test
    fun test01() {
        val res = testClient.value1("saro").block()
        Assertions.assertEquals(res.code, "ok")
        Assertions.assertEquals(res.data, "saro")
        println(res.data)
    }

    @Test
    fun test02() {
        val res = testClient.value2("abc").block()
        Assertions.assertEquals(res.code, "ok")
        Assertions.assertEquals(res.data, "abc")
        println(res.data)
    }

    @Test
    fun test03() {
        val res = testClient.data1(DataItem("saro", "1", "j@saro.me")).block()
        Assertions.assertEquals(res.code, "ok")
        Assertions.assertEquals(res.data, DataItem("saro", "1", "j@saro.me"))
        println(res.data)
    }

    @Test
    fun test04() {
        val res = testClient.data2(DataItem("saro2", "1", "j@saro.me")).block()
        Assertions.assertEquals(res.code, "ok")
        Assertions.assertEquals(res.data, "saro2")
        println(res.data)
    }

    @Test
    fun test05() {
        val res = testClient.justParam1("1", "2").block()
        Assertions.assertEquals(res.code, "ok")
        Assertions.assertEquals(res.data, "1-2")
        println(res.data)
    }

    @Test
    fun test06() {
        val res = testClient.justParam2("2").block()
        Assertions.assertEquals(res.code, "ok")
        Assertions.assertEquals(res.data, "$token-2")
        println(res.data)
    }
}
