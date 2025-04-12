package ktest

import ktest.model.ApiResponse
import ktest.model.DataItem
import me.saro.ext.spring.rwc.RestWebClient
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Mono

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
