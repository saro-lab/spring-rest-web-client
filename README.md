# Spring Rest WebClient
[![Maven Central](https://maven-badges.herokuapp.com/maven-central/me.saro/spring-rest-web-client/badge.svg)](https://maven-badges.herokuapp.com/maven-central/me.saro/spring-rest-web-client)
[![GitHub license](https://img.shields.io/github/license/saro-lab/spring-rest-web-client.svg)](https://github.com/saro-lab/spring-rest-web-client/blob/master/LICENSE)

#### Reactive(WebClient) Based REST Client for Spring
- Support Non-Blocking, Blocking 
- Spring-Boot 4.x

# QUICK START
### Gradle
```
implementation("me.saro:spring-rest-web-client:4.0.0.1")
```

### Maven
``` xml
<dependency>
  <groupId>me.saro</groupId>
  <artifactId>spring-rest-web-client</artifactId>
  <version>4.0.0.1</version>
</dependency>
```

### Enable
```
@EnableRestWebClient
```

### Kotlin example
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

### Java runtime and test cautions
- https://github.com/spring-projects/spring-boot/wiki/Spring-Boot-2.2-Release-Notes
- ko
  ```
  코틀린과 달리 자바에선 -parameters 옵션을 넣지 않으면 리플랙션시 Methods 의 ParameterName 을 알 수 없습니다.
  다행히 스프링부트 2.2 부터는 gradle 을 통해 -parameters 옵션을 기본으로 사용합니다.
  하지만 gradle을 통하지 않고 runtime 하거나 test 할 경우 -parameters 옵션을 넣거나 아래 예제처럼 @BindParam("name"), @RequestParam("name") 을 명시하시기 바랍니다.
  ```
- en
  ```
  Unlike Kotlin, in Java, if the -parameters option is not enabled, you cannot retrieve method parameter names via reflection.
  Fortunately, starting from Spring Boot 2.2, the -parameters option is enabled by default when using Gradle.
  However, if you run or test your application without Gradle, make sure to explicitly enable the -parameters option or specify parameter names using annotations like `@BindParam("name")` or `@RequestParam("name")`, as shown in the example below.
  ```
- use -parameters option
    ```java
    @GetMapping("/{b}")
    Mono<String> value1(String b, @RequestParam String p);
    ```
- without -parameters option
    ```java
    @GetMapping("/{b}")
    Mono<String> value1(@BindParam("b") String b, @RequestParam("p") String p);
    ```

### Java Example
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
    Mono<ApiResponse<String>> value1(String testValue);

    // consumes -> Accept
    @GetMapping(path = "/str/test/{testValue}", consumes = MediaType.APPLICATION_JSON_VALUE)
    Mono<ApiResponse<String>> value2(String testValue);

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
    String patch(@RequestParam String a);
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


