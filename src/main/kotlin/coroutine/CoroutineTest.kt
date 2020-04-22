package coroutine

import kotlinx.coroutines.*
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.produce
import kotlinx.coroutines.selects.select
import kotlin.system.measureTimeMillis

@InternalCoroutinesApi
fun main() = runBlocking(CoroutineName("main")) {
    val orders = listOf(Order("玩具"), Order("图书"), Order("衣服"), Order("海鲜"), Order("手机"))
    val time = measureTimeMillis {
        val oderChannel = produceOrders(orders)
        val packageChannel1 = packageDelivery("packer1", oderChannel, DeliveryRobot(this))
        val packageChannel2 = packageDelivery("packer2", oderChannel, DeliveryRobot(this))
        var channel1Open = true
        var channel2Open = true
        while (channel1Open || channel2Open) {
            select<Unit> {
                if (channel1Open) {
                    packageChannel1.onReceiveOrClosed {
                        if (it.isClosed) {
                            channel1Open = false
                        } else {
                            log("Package complete ${it.value}")
                        }
                    }
                }
                if (channel2Open) {
                    packageChannel2.onReceiveOrClosed {
                        if (it.isClosed) {
                            channel2Open = false
                        } else {
                            log("Package complete ${it.value}")
                        }
                    }
                }
            }
        }
    }
    println("Time cost $time")
}

fun CoroutineScope.produceOrders(orders: List<Order>) = produce {
    for (order in orders) {
        send(order)
    }
}

fun CoroutineScope.packageDelivery(tag: String, oderChannel: ReceiveChannel<Order>, robot: DeliveryRobot) =
    produce(CoroutineName(tag)) {
        for (order in oderChannel) {
            log("Process order $order")
            coroutineScope {
                val goods1 = async { robot.collectGoods("Warehouse 1", order) }
                val goods2 = async { robot.collectGoods("Warehouse 2", order) }
                send(Package(listOf(goods1.await(), goods2.await())))
            }
        }
    }

data class Order(val goods: String)
data class Goods(val name: String)
data class Package(val goods: List<Goods?>)

fun log(v: Any) = println("[${Thread.currentThread().name}] $v")
