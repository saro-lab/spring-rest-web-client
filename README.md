# Spring Rest WebClient
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/me.saro/spring-rest-web-client/badge.svg)](https://maven-badges.herokuapp.com/maven-central/me.saro/spring-rest-web-client)
[![GitHub license](https://img.shields.io/github/license/saro-lab/spring-rest-web-client.svg)](https://github.com/saro-lab/spring-rest-web-client/blob/master/LICENSE)

# Requirements
- spring-boot 3.x

# QUICK START

## Gradle
```
implementation("me.saro:spring-rest-web-client:3.4.4.1")
```

## Maven
``` xml
<dependency>
  <groupId>me.saro</groupId>
  <artifactId>spring-rest-web-client</artifactId>
  <version>3.4.4.1</version>
</dependency>
```

## kotlin example
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

    @PutMapping("/param")
    fun justParam1(@RequestParam a: String, @RequestParam("b") d: String): Mono<ApiResponse<String>>

    @PutMapping("/param?a=\${token}")
    fun justParam2(@RequestParam("b") d: String): Mono<ApiResponse<String>>
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

    @GetMapping("/${env}/test/{testValue}")
    Mono<ApiResponse<String>> value1(@PathVariable("testValue") String testValue);

    // consumes -> Accept
    @GetMapping(path = "/str/test/{testValue}", consumes = MediaType.APPLICATION_JSON_VALUE)
    Mono<ApiResponse<String>> value2(@PathVariable("testValue") String testValue);

    @PostMapping("/data")
    Mono<ApiResponse<DataItem>> data1(@RequestBody DataItem dataItem);

    @DeleteMapping("/data")
    Mono<ApiResponse<String>> data2(@RequestParam DataItem dataItem);

    @PutMapping("/param")
    Mono<ApiResponse<String>> justParam1(@RequestParam("a") String a, @RequestParam("b") String d);

    @PutMapping("/param?a=${token}")
    Mono<ApiResponse<String>> justParam2(@RequestParam("b") String d);
}
```

## Kotlin Example / Test Code
- [BasicTest.kt](src/test/kotlin/ktest/BasicTest.kt)
- [TestKotlinClient.kt](src/test/kotlin/ktest/TestKotlinClient.kt)

## Java Example / Test Code
- [BasicTest.java](src/test/java/jtest/BasicTest.java)
- [TestJavaClient.java](src/test/java/jtest/TestJavaClient.java)

## repository
- https://search.maven.org/artifact/me.saro/spring-rest-web-client
- https://mvnrepository.com/artifact/me.saro/spring-rest-web-client

## see
- [가리사니의 조각들...](https://gs.saro.me)
- https://docs.spring.io/spring-boot/system-requirements.html


