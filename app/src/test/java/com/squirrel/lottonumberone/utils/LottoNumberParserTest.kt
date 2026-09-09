package com.squirrel.lottonumberone.utils

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class LottoNumberParserTest {

    @Test
    fun `정상적인 로또 번호 6개 문자열이 오름차순으로 파싱된다`() {
        val input = "1, 7, 15, 23, 35, 42"
        val expected = listOf(1, 7, 15, 23, 35, 42)

        val actual = LottoNumberParser.parse(input)

        assertEquals(expected, actual)
    }

    @Test
    fun `순서가 섞인 로또 번호도 오름차순 정렬되어 파싱된다`() {
        val input = "42, 35, 1, 23, 7, 15"
        val expected = listOf(1, 7, 15, 23, 35, 42)

        val actual = LottoNumberParser.parse(input)

        assertEquals(expected, actual)
    }

    @Test
    fun `회차 정보(예 제10회)가 앞에 있어도 실제 로또 번호 6개를 잘 파싱한다`() {
        val input = "제10회 로또 당첨번호: 3, 12, 18, 25, 31, 40"
        val expected = listOf(3, 12, 18, 25, 31, 40)

        val actual = LottoNumberParser.parse(input)

        assertEquals(expected, actual)
    }

    @Test
    fun `한글이나 특수문자가 섞인 OCR 텍스트에서도 번호 6개를 잘 추출한다`() {
        val input = "제1050회 로또 당첨번호: [3], 14, 25, 33, 41, 44 축하합니다!"
        val expected = listOf(3, 14, 25, 33, 41, 44)

        val actual = LottoNumberParser.parse(input)

        assertEquals(expected, actual)
    }

    @Test
    fun `1미만 또는 45초과 숫자는 제외하고 1~45 범위 내 6개 번호만 추출한다`() {
        val input = "0, 3, 14, 25, 33, 41, 44, 99"
        val expected = listOf(3, 14, 25, 33, 41, 44)

        val actual = LottoNumberParser.parse(input)

        assertEquals(expected, actual)
    }

    @Test
    fun `중복된 숫자가 포함되어 고유 숫자가 6개 미만이면 null을 반환한다`() {
        val input = "5, 5, 10, 20, 30, 40"

        val actual = LottoNumberParser.parse(input)

        assertNull(actual)
    }

    @Test
    fun `추출 가능한 로또 번호가 6개 미만이면 null을 반환한다`() {
        val input = "1, 2, 3, 4, 5"

        val actual = LottoNumberParser.parse(input)

        assertNull(actual)
    }

    @Test
    fun `빈 문자열이 입력되면 null을 반환한다`() {
        val input = ""

        val actual = LottoNumberParser.parse(input)

        assertNull(actual)
    }

    @Test
    fun `여러 줄 텍스트에서 6개 유효 번호가 포함된 행을 우선해서 파싱한다`() {
        val input = """
            로또 추첨 결과
            0 99 100
            3 12 18 25 31 40
            1 2 3
        """.trimIndent()

        val expected = listOf(3, 12, 18, 25, 31, 40)

        val actual = LottoNumberParser.parse(input)

        assertEquals(expected, actual)
    }

    @Test
    fun `7개 이상의 숫자가 들어온 경우 상위 6개 고유 숫자만 추출하여 정렬한다`() {
        val input = "3 12 18 25 31 40 + 7"
        val expected = listOf(3, 12, 18, 25, 31, 40)

        val actual = LottoNumberParser.parse(input)

        assertEquals(expected, actual)
    }
}
