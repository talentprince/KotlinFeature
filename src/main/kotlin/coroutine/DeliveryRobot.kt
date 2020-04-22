package coroutine

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.SendChannel
import kotlinx.coroutines.channels.actor
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.delay
import kotlinx.coroutines.selects.select

class DeliveryRobot(val coroutineScope: CoroutineScope) : CoroutineScope by coroutineScope {
    val robot1: SendChannel<CollectRequest> = actor() {
        consumeEach {
            log("Collected goods from robot1 in ${it.tag} for ${it.order}")
            delay(100)
            it.channel.send(Goods("Goods from ${it.tag} for ${it.order}"))
            it.channel.close()
        }
    }
    val robot2: SendChannel<CollectRequest> = actor() {
        consumeEach {
            log("Collected goods from robot2 for ${it.order}")
            delay(100)
            it.channel.send(Goods("Goods from ${it.tag} for ${it.order}"))
            it.channel.close()
        }
    }

    suspend fun collectGoods(tag: String, order: Order) = select<Goods> {
        val channel = Channel<Goods>()
        robot1.onSend(CollectRequest(tag, order, channel)) {
            channel.receive()
        }
        robot2.onSend(CollectRequest(tag, order, channel)) {
            channel.receive()
        }
    }
}

class CollectRequest(val tag: String, val order: Order, val channel: Channel<Goods>)