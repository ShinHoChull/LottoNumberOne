package com.squirrel.lottonumberone.utils

object LottoNumberParser {

    // "제10회", "100회차", "제 5 회" 등 회차 번호 표기 제거용 정규식
    private val roundPattern = Regex("""(제\s*)?\d+\s*(회|회차)""")
    private val numberPattern = Regex("(?<![0-9])([1-9]|[1-3][0-9]|4[0-5])(?![0-9])")

    /**
     * OCR 텍스트에서 로또 번호 6개(1~45, 중복 없음)를 추출한다.
     */
    fun parse(text: String): List<Int>? {
        // 1. 회차 표기 사전 제거
        val sanitizedText = text.replace(roundPattern, "")

        // 2. 줄 단위로 먼저 6개 이상 유효한 번호 행이 존재하는지 탐색
        sanitizedText.lines()
            .map { line -> extractNumbers(line) }
            .firstOrNull { isValidLottoRow(it) }
            ?.let { return normalizeRow(it) }

        // 3. 줄 구분 없이 전체 텍스트에서 번호 추출
        val allNumbers = extractNumbers(sanitizedText)
        if (isValidLottoRow(allNumbers)) {
            return normalizeRow(allNumbers)
        }

        // 4. 고유한 6개 이상의 1~45 범위 숫자가 있는 경우 최종 추출
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
