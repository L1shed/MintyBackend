package example.com.plugins

import io.ktor.server.application.*
import io.ktor.server.routing.*
import io.ktor.server.websocket.*
import io.ktor.websocket.*
import java.time.Duration
import java.util.*
import kotlin.collections.LinkedHashSet

fun Application.configureSockets() {
    install(WebSockets) {
        pingPeriod = Duration.ofSeconds(15)
        timeout = Duration.ofSeconds(15)
        maxFrameSize = Long.MAX_VALUE
        masking = false
    }
    routing {
        val sessions = mutableListOf<WebSocketSession>()
        webSocket("/irc") {
            if (this !in sessions) sessions.add(this)

            for (frame in incoming) {
                if (frame is Frame.Text) {
                    val text = frame.readText()
//                    outgoing.send(Frame.Text(text))

                    sessions.forEach {
                        it.send(text)
                    }
                }
            }
        }
    }
}
