# Spring Rest WebClient
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/me.saro/spring-rest-web-client/badge.svg)](https://maven-badges.herokuapp.com/maven-central/me.saro/spring-rest-web-client)
[![GitHub license](https://img.shields.io/github/license/saro-lab/spring-rest-web-client.svg)](https://github.com/saro-lab/spring-rest-web-client/blob/master/LICENSE)

## Reactive(WebClient) Based REST Client for Spring
- Support Non-Blocking and Blocking 

# Requirements
- Spring-Boot 3.x

# QUICK START
## Gradle
```
implementation("me.saro:spring-rest-web-client:3.4.4.2")
```

## Maven
``` xml
<dependency>
  <groupId>me.saro</groupId>
  <artifactId>spring-rest-web-client</artifactId>
  <version>3.4.4.2</version>
</dependency>
```

## Kotlin example
``` kotlin
@RestWebClient(
    uri = "\${client.local.uri}/api/v1",
    environmentAliases = [
        "env=spring.profiles.active",
        "token=client.local.token",
    ],
)
interface TestKotlinClient {
    @GetMapping("/\${env}/test/{testValue}")
    fun value1(testValue: String): Mono<ApiResponse<String>>

    // consumes -> Accept
    @GetMapping(path = ["/str/test/{testValue}"], consumes = [MediaType.APPLICATION_JSON_VALUE])
    fun value2(testValue: String): Mono<ApiResponse<String>>

    @PostMapping("/data")
    fun data1(@RequestBody dataItem: DataItem): Mono<ApiResponse<DataItem>>

    @DeleteMapping("/data")
    fun data2(@RequestParam dataItem: DataItem): Mono<ApiResponse<String>>

    // blocking
    @PostMapping("/data")
    fun data3(@RequestBody dataItem: DataItem): ApiResponse<DataItem>

    @PutMapping("/param")
    fun justParam1(@RequestParam a: String, @RequestParam("b") d: String): Mono<ApiResponse<String>>

    @PutMapping("/param?a=\${token}")
    fun justParam2(@RequestParam("b") d: String): Mono<ApiResponse<String>>

    // blocking
    @PatchMapping("/patch")
    fun patch(@RequestParam a: String): String
}
```

## Java Example
``` java
@RestWebClient(
        uri = "${client.local.uri}/api/v1",
        environmentAliases = {
                "env=spring.profiles.active",
                "token=client.local.token"
        }
)
public interface TestJavaClient {
    // ko: 컴파일 시 -parameter을 넣는 경우 Java에서도 "@BindParam", "@RestParam.name"을 생략할 수 있다.
    // en: When compiling, if you add -parameter, you can omit "@BindParam" and "@RestParam.name" in Java.

    @GetMapping("/${env}/test/{testValue}")
    Mono<ApiResponse<String>> value1(@BindParam("testValue") String testValue);

    // consumes -> Accept
    @GetMapping(path = "/str/test/{testValue}", consumes = MediaType.APPLICATION_JSON_VALUE)
    Mono<ApiResponse<String>> value2(@BindParam("testValue") String testValue);

    @PostMapping("/data")
    Mono<ApiResponse<DataItem>> data1(@RequestBody DataItem dataItem);

    @DeleteMapping("/data")
    Mono<ApiResponse<String>> data2(@RequestParam DataItem dataItem);

    // blocking
    @PostMapping("/data")
    ApiResponse<DataItem> data3(@RequestBody DataItem dataItem);

    @PutMapping("/param")
    Mono<ApiResponse<String>> justParam1(@RequestParam("a") String a, @RequestParam("b") String d);

    @PutMapping("/param?a=${token}")
    Mono<ApiResponse<String>> justParam2(@RequestParam("b") String d);

    // blocking
    @PatchMapping("/patch")
    String patch(@RequestParam("a") String a);
}
```

## Kotlin Example / Test Code
- [BasicTest.kt](src/test/kotlin/ktest/BasicTest.kt)
- [TestKotlinClient.kt](src/test/kotlin/ktest/TestKotlinClient.kt)

## Java Example / Test Code
- [BasicTest.java](src/test/java/jtest/BasicTest.java)
- [TestJavaClient.java](src/test/java/jtest/TestJavaClient.java)

## Test Controller (Server)
- [TestController.kt](src/test/kotlin/share/server/TestController.kt)

## repository
- https://search.maven.org/artifact/me.saro/spring-rest-web-client
- https://mvnrepository.com/artifact/me.saro/spring-rest-web-client

## see
- [가리사니의 조각들...](https://gs.saro.me)
- https://docs.spring.io/spring-boot/system-requirements.html


