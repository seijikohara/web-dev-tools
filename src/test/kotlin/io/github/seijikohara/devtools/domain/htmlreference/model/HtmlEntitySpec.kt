package io.github.seijikohara.devtools.domain.htmlreference.model

import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withData
import io.kotest.matchers.shouldBe

class HtmlEntitySpec :
    FunSpec({

        context("HtmlEntity.entityReference() with single code") {
            data class SingleCodeCase(
                val codeValue: Long,
                val expectedReference: String,
                val description: String,
            )

            withData(
                SingleCodeCase(38L, "&#38;", "ampersand"),
                SingleCodeCase(60L, "&#60;", "less than"),
                SingleCodeCase(62L, "&#62;", "greater than"),
                SingleCodeCase(34L, "&#34;", "quotation mark"),
                SingleCodeCase(169L, "&#169;", "copyright symbol"),
            ) { (codeValue, expectedReference, _) ->
                val entity =
                    HtmlEntity(
                        id = 1L,
                        name = "test",
                        code = EntityCode.of(codeValue).getOrThrow(),
                        code2 = null,
                        standard = null,
                        dtd = null,
                        description = null,
                    )

                entity.entityReference() shouldBe expectedReference
            }
        }

        context("HtmlEntity.entityReference() with two codes") {
            data class DoubleCodeCase(
                val code1Value: Long,
                val code2Value: Long,
                val expectedReference: String,
                val description: String,
            )

            withData(
                DoubleCodeCase(55349L, 56320L, "&#55349;&#56320;", "surrogate pair example 1"),
                DoubleCodeCase(100L, 200L, "&#100;&#200;", "arbitrary double code"),
                DoubleCodeCase(1L, 2L, "&#1;&#2;", "minimal double code"),
            ) { (code1Value, code2Value, expectedReference, _) ->
                val entity =
                    HtmlEntity(
                        id = 1L,
                        name = "test",
                        code = EntityCode.of(code1Value).getOrThrow(),
                        code2 = EntityCode.of(code2Value).getOrThrow(),
                        standard = null,
                        dtd = null,
                        description = null,
                    )

                entity.entityReference() shouldBe expectedReference
            }
        }

        context("HtmlEntity data class properties") {
            test("should correctly store all properties") {
                val code = EntityCode.of(38L).getOrThrow()
                val code2 = EntityCode.of(39L).getOrThrow()

                val entity =
                    HtmlEntity(
                        id = 123L,
                        name = "amp",
                        code = code,
                        code2 = code2,
                        standard = "HTML5",
                        dtd = "HTML 4.01",
                        description = "ampersand",
                    )

                entity.id shouldBe 123L
                entity.name shouldBe "amp"
                entity.code shouldBe code
                entity.code2 shouldBe code2
                entity.standard shouldBe "HTML5"
                entity.dtd shouldBe "HTML 4.01"
                entity.description shouldBe "ampersand"
            }

            test("should handle null optional properties") {
                val code = EntityCode.of(60L).getOrThrow()

                val entity =
                    HtmlEntity(
                        id = 1L,
                        name = "lt",
                        code = code,
                        code2 = null,
                        standard = null,
                        dtd = null,
                        description = null,
                    )

                entity.code2 shouldBe null
                entity.standard shouldBe null
                entity.dtd shouldBe null
                entity.description shouldBe null
            }
        }
    })
