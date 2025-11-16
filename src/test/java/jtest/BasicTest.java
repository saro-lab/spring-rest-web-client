package jtest;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import share.model.DataItem;
import share.server.App;
import tools.jackson.databind.ObjectMapper;

@SpringBootTest(classes = {App.class}, webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT)
public class BasicTest {

    @Autowired
    private TestJavaClient testClient;

    @Autowired
    private ObjectMapper objectMapper;

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

    @Test
    public void test07() {
        var res = testClient.patch("28372982");
        Assertions.assertEquals("28372982", res);
        System.out.println(res);
    }

    @Test
    public void test08() {
        var res = testClient.body("111&1", "22=22");
        Assertions.assertEquals("a=111%261&b=22%3D22", res);
        System.out.println(res);
    }

    @Test
    public void test09() {
        var res = testClient.noParam().block();
        Assertions.assertTrue(res.getSuccess());
        System.out.println(res);
    }

    @Test
    public void test10() {
        var res = testClient.json().block();
        Assertions.assertEquals("1", res.at("/a").asText());
        Assertions.assertEquals(3, res.at("/b").size());
        System.out.println(objectMapper.writeValueAsString(res));
    }
}
