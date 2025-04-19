package ktest

import me.saro.ext.spring.rwc.RestWebClient
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Mono
import share.model.ApiResponse
import share.model.DataItem

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

    // blocking
    @PostMapping(path = ["/body"], consumes = [MediaType.APPLICATION_FORM_URLENCODED_VALUE])
    fun body(@RequestParam a: String, @RequestParam("b") c: String): String
}
