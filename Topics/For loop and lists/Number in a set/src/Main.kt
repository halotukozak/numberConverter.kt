fun main() {
    val size = readln().toInt()
    val list = mutableListOf<Int>()
    repeat(size) {
        list.add(readln().toInt())
    }
    println(if (list.contains(readln().toInt())) "YES" else "NO")
}