package share.server

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono
import share.model.ApiResponse
import share.model.DataItem

@RestController
@RequestMapping("/api/v1")
class TestController {
    val mapper = ObjectMapper()

    @GetMapping("/local/test/{testValue}")
    fun testValue(@PathVariable testValue: String): Mono<ApiResponse<String>> =
        Mono.just(ApiResponse.ok(testValue))

    @RequestMapping("/str/test/{testValue}")
    fun testValueStr(@PathVariable testValue: String): String =
        mapper.writeValueAsString(ApiResponse.ok(testValue))

    @PostMapping("/data")
    fun dataItem1(@RequestBody dataItem: DataItem): ApiResponse<DataItem> =
        ApiResponse.ok(dataItem)

    @DeleteMapping("/data")
    fun dataItem2(exchange: ServerWebExchange): ApiResponse<String> =
        ApiResponse.ok(exchange.request.queryParams.getFirst("provider"))

    @PutMapping("/param")
    fun justParam1(@RequestParam a: String, @RequestParam("b") c: String): ApiResponse<String> =
        ApiResponse.ok("$a-$c")

    @PatchMapping("/patch")
    fun patch(@RequestParam a: String): String = a

    @PostMapping("/body", produces = [MediaType.APPLICATION_FORM_URLENCODED_VALUE])
    fun body(@RequestBody(required = false) body: String?): String = body ?: ""

}