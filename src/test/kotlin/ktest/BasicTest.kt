package ktest

import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.SpringBootTest
import share.model.ApiResponse
import share.model.DataItem
import share.server.App
import tools.jackson.databind.ObjectMapper


@SpringBootTest(classes = [App::class], webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
class BasicTest {
    @Autowired
    private lateinit var testClient: TestKotlinClient

    @Autowired private lateinit var objectMapper: ObjectMapper

    @Value("\${client.local.token}")
    private lateinit var token: String

    @Test
    fun test01() {
        val res = testClient.value1("saro").block()
        Assertions.assertEquals(res?.code, "ok")
        Assertions.assertEquals(res?.data, "saro")
        println(res?.data)
    }

    @Test
    fun test02() {
        val res = testClient.value2("abc").block()
        Assertions.assertEquals(res?.code, "ok")
        Assertions.assertEquals(res?.data, "abc")
        println(res?.data)
    }

    @Test
    fun test03() {
        val res = testClient.data1(DataItem("saro", "1", "j@saro.me")).block()
        Assertions.assertEquals(res?.code, "ok")
        Assertions.assertEquals(res?.data, DataItem("saro", "1", "j@saro.me"))
        println(res?.data)
    }

    @Test
    fun test04() {
        val res = testClient.data2(DataItem("saro2", "1", "j@saro.me")).block()
        Assertions.assertEquals(res?.code, "ok")
        Assertions.assertEquals(res?.data, "saro2")
        println(res?.data)
    }

    @Test
    fun test04_1() {
        val input = DataItem("saro", "1", "j@saro.me")
        val res = testClient.data3(input)
        Assertions.assertEquals(res?.code, "ok")
        Assertions.assertEquals(res?.data, DataItem("saro", "1", "j@saro.me"))
        println(res?.data)
    }

    @Test
    fun test05() {
        val res = testClient.justParam1("1", "2").block()
        Assertions.assertEquals(res?.code, "ok")
        Assertions.assertEquals(res?.data, "1-2")
        println(res?.data)
    }

    @Test
    fun test06() {
        val res = testClient.justParam2("2").block()
        Assertions.assertEquals(res?.code, "ok")
        Assertions.assertEquals(res?.data, "$token-2")
        println(res?.data)
    }

    @Test
    fun test07() {
        val res = testClient.patch("12346232561")
        Assertions.assertEquals(res, "12346232561")
        println(res)
    }

    @Test
    fun test08() {
        val res: String = testClient.body("111&1", "22=22")
        Assertions.assertEquals("a=111%261&b=22%3D22", res)
        println(res)
    }

    @Test
    fun test09() {
        val res: ApiResponse<String> = testClient.noParam().block()!!
        Assertions.assertTrue(res.success)
        println(res)
    }

    @Test
    fun test10() {
        val res = testClient.json().block()
        Assertions.assertEquals("1", res!!.at("/a").asText())
        Assertions.assertEquals(3, res.at("/b").size())
        println(objectMapper.writeValueAsString(res))
    }
}
