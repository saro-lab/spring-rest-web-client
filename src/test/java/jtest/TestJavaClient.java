package jtest;

import me.saro.ext.spring.rwc.RestWebClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;
import share.model.ApiResponse;
import share.model.DataItem;
import tools.jackson.databind.JsonNode;

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

    @PostMapping(path = "/data", consumes = MediaType.APPLICATION_JSON_VALUE)
    ApiResponse<DataItem> data31(@RequestBody String dataItem);

    @PutMapping("/param")
    Mono<ApiResponse<String>> justParam1(@RequestParam("a") String a, @RequestParam("b") String d);

    @PutMapping("/param?a=${token}")
    Mono<ApiResponse<String>> justParam2(@RequestParam("b") String d);

    // blocking
    @PatchMapping("/patch")
    String patch(@RequestParam String a);

    // blocking
    @PostMapping(path = "/body", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    String body(@RequestParam String a, @RequestParam("b") String c);

    @GetMapping(path = "/no-param")
    Mono<ApiResponse<String>> noParam();

    @GetMapping(path = "/json")
    Mono<JsonNode> json();
}
