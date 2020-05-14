package ru.skillbranch.skillarticles.extensions

fun String?.indexesOf(substr: String, ignoreCase: Boolean = true): List<Int> {
    val listIndex = mutableListOf<Int>()
    var startIndex = 0

    if (this != null && substr.isNotBlank()) {
        while (startIndex != -1) {
            startIndex = this.indexOf(substr, startIndex + 1, ignoreCase)
            if (startIndex != -1)listIndex.add(startIndex)
        }
    }

    return listIndex
}