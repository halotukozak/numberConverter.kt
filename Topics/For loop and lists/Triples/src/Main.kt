fun main() {
    val list = MutableList(readln().toInt()) { readln().toInt() }
    var triples = 0
    if (list.size > 2) {
        for (i in 2 until (list.size)) {
            if (list[i - 2] + 1 == list[i - 1] && list[i - 1] + 1 == list[i]) {
                triples++
            }
        }
    }

    println(triples)
}