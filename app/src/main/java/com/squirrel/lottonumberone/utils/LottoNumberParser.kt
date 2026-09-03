package com.squirrel.lottonumberone.utils

object LottoNumberParser {

    private val numberPattern = Regex("(?<![0-9])([1-9]|[1-3][0-9]|4[0-5])(?![0-9])")

    /**
     * OCR 텍스트에서 로또 번호 6개(1~45, 중복 없음)를 추출한다.
     */
    fun parse(text: String): List<Int>? {
        text.lines()
            .map { line -> extractNumbers(line) }
            .firstOrNull { isValidLottoRow(it) }
            ?.let { return normalizeRow(it) }

        val allNumbers = extractNumbers(text)
        if (isValidLottoRow(allNumbers)) {
            return normalizeRow(allNumbers)
        }

        return findSixUniqueNumbers(allNumbers)
    }

    private fun extractNumbers(source: String): List<Int> {
        return numberPattern.findAll(source).map { it.value.toInt() }.toList()
    }

    private fun isValidLottoRow(numbers: List<Int>): Boolean {
        if (numbers.size < 6) return false
        return numbers.take(6).distinct().size == 6
    }

    private fun normalizeRow(numbers: List<Int>): List<Int> {
        return numbers.take(6).distinct().sorted()
    }

    private fun findSixUniqueNumbers(numbers: List<Int>): List<Int>? {
        val unique = numbers.distinct()
        if (unique.size < 6) return null

        val valid = unique.filter { it in 1..45 }
        if (valid.size < 6) return null

        return valid.take(6).sorted()
    }
}
