fun main() {
    val n = readln().toInt()
    val list = mutableListOf<Int>()
    val map = mutableMapOf<Int, MutableSet<Int>>()
    repeat(n) {
        list.add(readln().toInt())
    }

    for (i in list.indices) {
        val surrounding = mutableSetOf<Int>()
        if (i != 0) surrounding.add(list[i - 1])
        if (i != list.lastIndex) surrounding.add(list[i + 1])
        map.addOrCreate(list[i], surrounding)
    }

    val (p, m) = readln().split(" ").map { it.toInt() }

    println(if (map.occurNext(p, m)) "NO" else "YES")

}

fun MutableMap<Int, MutableSet<Int>>.occurNext(p: Int, m: Int): Boolean {
    return when {
        !this.containsKey(p) || !this.containsKey(m) -> false
        this[p]!!.contains(m) || this[m]!!.contains(p) -> true
        else -> false
    }
}

fun MutableMap<Int, MutableSet<Int>>.addOrCreate(v: Int, surrounding: MutableSet<Int>) {
    if (this.containsKey(v)) {
        this[v]?.addAll(surrounding)
    }
    else {
        this[v] = surrounding
    }
}