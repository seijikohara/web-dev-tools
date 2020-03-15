package net.relaxism.devtools.webdevtools.controller

import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import javax.servlet.http.HttpServletRequest

@Controller
@RequestMapping(path = ["/api/ip"])
class IpApiController() {

    @GetMapping
    fun getIp(request: HttpServletRequest): ResponseEntity<IpResponse> {
        val ipAddress = getRemoteAddress(request)
        return ResponseEntity.ok(IpResponse(ipAddress))
    }

    private fun getRemoteAddress(request: HttpServletRequest): String {
        val xForwardedFor: String? = request.getHeader("X-Forwarded-For")
        return xForwardedFor ?: request.remoteAddr;
    }

    data class IpResponse(val ipAddress: String)

}
