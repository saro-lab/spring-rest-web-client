package ktest.server

import com.fasterxml.jackson.databind.ObjectMapper
import ktest.model.ApiResponse
import ktest.model.DataItem
import org.springframework.web.bind.annotation.*
import org.springframework.web.server.ServerWebExchange

@RestController
@RequestMapping("/api/v1/")
class TestController {
    val mapper = ObjectMapper()

    @RequestMapping("/local/test/{testValue}")
    fun testValue(@PathVariable testValue: String): ApiResponse<String> =
        ApiResponse.ok(testValue)

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
}