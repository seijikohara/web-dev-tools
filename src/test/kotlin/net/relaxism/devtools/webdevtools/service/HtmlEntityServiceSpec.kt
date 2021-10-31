package net.relaxism.devtools.webdevtools.service

import com.ninjasquad.springmockk.MockkBean
import io.kotest.core.spec.style.StringSpec
import io.mockk.every
import net.relaxism.devtools.webdevtools.repository.HtmlEntity
import net.relaxism.devtools.webdevtools.repository.HtmlEntityRepository
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.test.StepVerifier

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.NONE)
class HtmlEntityServiceSpec(
    @MockkBean private val htmlEntityRepository: HtmlEntityRepository,
    @Autowired private val htmlEntityService: HtmlEntityService
) : StringSpec() {

    init {

        "findAll : success" {
            val entity1 = HtmlEntity(1, "name1", 1, 1, "standard1", "dtd1", "desc1")
            val entity2 = HtmlEntity(2, "name2", 2, 2, "standard2", "dtd2", "desc2")

            every { htmlEntityRepository.findAll() } returns Flux.just(entity1, entity2)

            StepVerifier
                .create(htmlEntityService.findAll())
                .expectNext(entity1, entity2)
                .verifyComplete()
        }

        "findByNameContaining : success" {
            val entity1 = HtmlEntity(1, "name1", 1, 1, "standard1", "dtd1", "desc1")
            val entity2 = HtmlEntity(2, "name2", 2, 2, "standard2", "dtd2", "desc2")
            val pageable: Pageable = PageRequest.of(0, 10, Sort.by(Sort.Order.asc("id")))

            every { htmlEntityRepository.countByNameContaining(name = any()) } returns Mono.just(2L)
            every {
                htmlEntityRepository.findByNameContaining(
                    name = any(),
                    pageable = any()
                )
            } returns Flux.just(
                entity1,
                entity2
            )

            StepVerifier
                .create(htmlEntityService.findByNameContaining("a", pageable))
                .expectNext(PageImpl(listOf(entity1, entity2), pageable, 2))
                .verifyComplete()

        }

    }

}
