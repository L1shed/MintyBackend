package example.com

import example.com.plugins.*
import io.ktor.server.application.*

fun main(args: Array<String>) {
    io.ktor.server.netty.EngineMain.main(args)

    embeddedServer(Netty, port = 8080) {
        install(WebSockets)
        install(CallLogging)

        routing {
            webSocket("/chat") {
                val connections = mutableListOf<WebSocketSession>()

                connections.add(this)

                try {
                    for (frame in incoming) {
                        if (frame is Frame.Text) {
                            val receivedText = frame.readText()
                            for (connection in connections) {
                                if (connection != this) {
                                    connection.send(Frame.Text(receivedText))
                                }
                            }
                        }
                    }
                } finally {
                    connections.remove(this)
                }
            }
        }
    }.start(wait = true)
}

fun Application.module() {
    configureSockets()
    configureRouting()
}
