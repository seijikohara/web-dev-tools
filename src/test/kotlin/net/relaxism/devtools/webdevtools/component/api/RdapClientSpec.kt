package net.relaxism.devtools.webdevtools.component.api

import com.fasterxml.jackson.databind.ObjectMapper
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.shouldBe
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
class RdapIpClientSpec(
    @Autowired private val rdapClient: RdapClient,
) : StringSpec() {

    private val objectMapper = ObjectMapper()

    init {

        "get : success" {
            @Suppress("BlockingMethodInNonBlockingContext")
            mapToJson(
                rdapClient.getRdapByIpAddress("0.0.0.0").block()
            ) shouldBe "{\"lang\":\"en\",\"remarks\":[{\"title\":\"Remark\",\"description\":[\"The country is really worldwide.\"]},{\"title\":\"Remark\",\"description\":[\"This address space is assigned at various other places in\"]},{\"title\":\"Remark\",\"description\":[\"the world and might therefore not be in the RIPE database.\"]},{\"title\":\"Remark\",\"description\":[\"data has been transferred from RIPE Whois Database 20050221\"]}],\"notices\":[{\"title\":\"ABOUT\",\"description\":[\"This is the AfriNIC RDAP server.\"],\"links\":[{\"value\":\"https://rdap.afrinic.net/rdap/ip/0.0.0.0\",\"rel\":\"describedby\",\"href\":\"https://www.afrinic.net/support/whois-db/reference-manual\",\"hreflang\":[\"en\"],\"type\":\"text/html\",\"title\":\"AFRINIC Database Reference Manual\",\"media\":\"screen\"}]}],\"handle\":\"0.0.0.0 - 255.255.255.255\",\"status\":[\"active\"],\"port43\":\"whois.afrinic.net\",\"name\":\"ORG-IANA1-AFRINIC\",\"type\":\"ALLOCATED UNSPECIFIED\",\"country\":\"EU\",\"entities\":[{\"lang\":\"en\",\"remarks\":[{\"title\":\"Remark\",\"description\":[\"For more information on IANA services\"]},{\"title\":\"Remark\",\"description\":[\"go to IANA web site at http://www.iana.org.\"]},{\"title\":\"Remark\",\"description\":[\"data has been transferred from RIPE Whois Database 20050221\"]}],\"handle\":\"IANA1-AFRINIC\",\"status\":[\"active\"],\"port43\":\"whois.afrinic.net\",\"vcardArray\":[\"vcard\",[[\"version\",{},\"text\",\"4.0\"],[\"kind\",{},\"text\",\"group\"],[\"fn\",{},\"text\",\"Internet Assigned Numbers Authority\"],[\"email\",{\"type\":\"work\"},\"text\",\"afrinic-dbm@afrinic.net\"],[\"adr\",{\"type\":\"work\"},\"text\",[\"see http://www.iana.org.\",\"\",\"\",\"\",\"\",\"\",\"\"]]]],\"roles\":[\"administrative\",\"technical\"],\"objectClassName\":\"entity\",\"rdapConformance\":[]},{\"lang\":\"en\",\"remarks\":[{\"title\":\"Remark\",\"description\":[\"The IANA allocates IP addresses and AS number blocks to RIRs\"]},{\"title\":\"Remark\",\"description\":[\"see http://www.iana.org/ipaddress/ip-addresses.htm\"]},{\"title\":\"Remark\",\"description\":[\"and http://www.iana.org/assignments/as-numbers\"]},{\"title\":\"Remark\",\"description\":[\"data has been transferred from RIPE Whois Database 20050221\"]}],\"handle\":\"ORG-IANA1-AFRINIC\",\"status\":[\"active\"],\"port43\":\"whois.afrinic.net\",\"vcardArray\":[\"vcard\",[[\"version\",{},\"text\",\"4.0\"],[\"kind\",{},\"text\",\"org\"],[\"fn\",{},\"text\",\"Internet Assigned Numbers Authority\"],[\"email\",{\"type\":\"work\"},\"text\",\"bitbucket@ripe.net\"],[\"adr\",{\"type\":\"work\"},\"text\",[\"see http://www.iana.org\",\"\",\"\",\"\",\"\",\"\",\"\"]]]],\"roles\":[\"registrant\"],\"objectClassName\":\"entity\",\"rdapConformance\":[]}],\"startAddress\":\"0.0.0.0\",\"endAddress\":\"255.255.255.255\",\"ipVersion\":\"v4\",\"parentHandle\":\"0.0.0.0 - 255.255.255.255\",\"cidr0_cidrs\":[{\"v4prefix\":\"0.0.0.0\",\"length\":0}],\"objectClassName\":\"ip network\",\"rdapConformance\":[\"rdap_level_0\"]}"
        }

    }

    private fun mapToJson(map: Map<String, Any?>?): String {
        return objectMapper.writeValueAsString(map)
    }

}
