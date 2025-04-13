package jtest;

import me.saro.ext.spring.rwc.EnableRestWebClient;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringBootConfiguration;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.reactive.config.EnableWebFlux;
import share.model.DataItem;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
@SpringBootConfiguration
@EnableAutoConfiguration
@EnableWebFlux
@EnableRestWebClient
@ComponentScan(basePackages = {"share.*"})
public class BasicTest {

    @Autowired
    private TestJavaClient testClient;

    @Value("${client.local.token}")
    private String token;

    @Test
    public void test01() {
        var res = testClient.value1("saro").block();
        Assertions.assertEquals("ok", res.getCode());
        Assertions.assertEquals("saro", res.getData());
        System.out.println(res.getData());
    }

    @Test
    public void test02() {
        var res = testClient.value2("abc").block();
        Assertions.assertEquals("ok", res.getCode());
        Assertions.assertEquals("abc", res.getData());
        System.out.println(res.getData());
    }

    @Test
    public void test03() {
        var input = new DataItem("saro", "1", "j@saro.me");
        var res = testClient.data1(input).block();
        Assertions.assertEquals("ok", res.getCode());
        Assertions.assertEquals(input, res.getData());
        System.out.println(res.getData());
    }

    @Test
    public void test04() {
        var input = new DataItem("saro2", "1", "j@saro.me");
        var res = testClient.data2(input).block();
        Assertions.assertEquals("ok", res.getCode());
        Assertions.assertEquals("saro2", res.getData());
        System.out.println(res.getData());
    }

    @Test
    public void test04_1() {
        var input = new DataItem("saro2", "1", "j@saro.me");
        var res = testClient.data3(input);
        Assertions.assertEquals("ok", res.getCode());
        Assertions.assertEquals(input, res.getData());
        System.out.println(res.getData());
    }

    @Test
    public void test05() {
        var res = testClient.justParam1("1", "2").block();
        Assertions.assertEquals("ok", res.getCode());
        Assertions.assertEquals("1-2", res.getData());
        System.out.println(res.getData());
    }

    @Test
    public void test06() {
        var res = testClient.justParam2("2").block();
        Assertions.assertEquals("ok", res.getCode());
        Assertions.assertEquals(token + "-2", res.getData());
        System.out.println(res.getData());
    }
}
