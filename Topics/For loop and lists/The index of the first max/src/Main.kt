fun main() {
    val list = MutableList(readln().toInt()) { readln().toInt() }
    var max = 0

    for (i in 0 until list.size) {
        if (list[i] > list[max]) max = i
    }
    println(max)
}