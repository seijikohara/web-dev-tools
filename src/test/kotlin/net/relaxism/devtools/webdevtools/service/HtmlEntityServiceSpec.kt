package net.relaxism.devtools.webdevtools.service

import com.ninjasquad.springmockk.MockkBean
import io.kotest.core.spec.style.StringSpec
import io.mockk.every
import net.relaxism.devtools.webdevtools.repository.HtmlEntity
import net.relaxism.devtools.webdevtools.repository.HtmlEntityRepository
import reactor.core.publisher.Flux
import reactor.test.StepVerifier

class HtmlEntityServiceSpec(
    @MockkBean private val htmlEntityRepository: HtmlEntityRepository
) : StringSpec() {

    init {

        "findAll : success" {
            val entity1 = HtmlEntity(1, "name1", 1, 1, "standard1", "dtd1", "desc1")
            val entity2 = HtmlEntity(2, "name2", 2, 2, "standard2", "dtd2", "desc2")

            every { htmlEntityRepository.findAll() } returns Flux.just(entity1, entity2)

            StepVerifier
                .create(htmlEntityRepository.findAll())
                .expectNext(entity1, entity2)
                .verifyComplete()
        }

    }

}
