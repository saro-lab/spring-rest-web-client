package jtest;

import ktest.model.ApiResponse;
import ktest.model.DataItem;
import me.saro.ext.spring.rwc.RestWebClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

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
