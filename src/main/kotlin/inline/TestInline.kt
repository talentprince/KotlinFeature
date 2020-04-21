package inline

/**
 * inline noinline crossinline reified infix
 */

fun main() {
    hello1 {
        println("haha")
    }
    hello2({
        println("haha")
    }, {
        println("haha")
        return@hello2
    })
    hello3 {
        println("haha")
        return@hello3
    }



    println(A("A") isEqual "B")
    println("A" isEqual "B")

    println(find<Son>(listOf(Son(), Daughter())))
}

inline fun hello() {
    println("hello")
}

inline fun hello1(block: () -> Unit) {
    println("hello1 start")
    block()
    println("hello1 end")
}

inline fun hello2(block1: () -> Unit, noinline block2: () -> Unit) {
    println("hello2 start")
    block1()
    block2()
    println("hello2 end")
}

inline fun hello3(crossinline block: () -> Unit) {
    println("hello3 start")
    block()
    println("hello3 end")
}

interface Mother
class Son : Mother
class Daughter : Mother

@Suppress("UNCHECKED_CAST")
inline fun <reified T> find(list: List<Mother>): T? {
    for (item in list) {
        if (item is T) {
            return item
        }
    }
    return null
}

class A(val a: String) {
    infix fun isEqual(b: String): Boolean {
        return a == b
    }
}

infix fun String.isEqual(b: String) = this == b