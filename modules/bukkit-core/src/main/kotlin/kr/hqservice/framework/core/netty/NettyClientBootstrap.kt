package kr.hqservice.framework.core.netty

import kr.hqservice.framework.core.netty.event.AsyncNettyPacketReceivedEvent
import kr.hqservice.framework.core.netty.event.NettyClientDisconnectedEvent
import kr.hqservice.framework.core.netty.event.NettyPacketReceivedEvent
import kr.hqservice.framework.netty.HQServerBootstrap
import kr.hqservice.framework.netty.packet.server.HandShakePacket
import kr.hqservice.framework.netty.pipeline.BossHandler
import kr.hqservice.framework.netty.pipeline.ConnectionState
import kr.hqservice.framework.netty.pipeline.TimeOutHandler
import kr.hqservice.yaml.config.HQYamlConfiguration
import org.bukkit.plugin.Plugin
import java.util.concurrent.TimeUnit
import java.util.logging.Logger

class NettyClientBootstrap(
    private val plugin: Plugin,
    private val logger: Logger,
    private val config: HQYamlConfiguration
) {
    private var bootup = true

    fun initializing() {
        // TODO("준형님이 코루틴으로 바꿀 예정")
        val future = HQServerBootstrap(logger, config).initClient(bootup)
        bootup = false
        future.whenCompleteAsync { channel, throwable ->
            if(throwable != null) {
                logger.severe("failed to bootup successfully.")
                throwable.printStackTrace()
                try {
                    TimeUnit.SECONDS.sleep(3)
                    initializing()
                } catch (e: InterruptedException) { e.printStackTrace() }
            }

            val handlerBoss = channel.pipeline().get(BossHandler::class.java)
            handlerBoss.setDisconnectionHandler {
                if(!plugin.isEnabled) return@setDisconnectionHandler
                it.setEnabled(false)
                plugin.server.scheduler.runTask(plugin, Runnable {
                    plugin.server.pluginManager.callEvent(NettyClientDisconnectedEvent(it)) })
                try {
                    TimeUnit.SECONDS.sleep(3)
                    initializing()
                } catch (e: InterruptedException) { e.printStackTrace() }
            }

            handlerBoss.setPacketPreprocessHandler { packet, wrapper ->
                plugin.server.pluginManager.callEvent(AsyncNettyPacketReceivedEvent(wrapper, packet))
                plugin.server.scheduler.runTask(plugin, Runnable {
                    plugin.server.pluginManager.callEvent(NettyPacketReceivedEvent(wrapper, packet)) })
            }

            logger.info("netty-client initialization success!")
            handlerBoss.setConnectionState(ConnectionState.HANDSHAKING)
            channel.writeAndFlush(HandShakePacket(plugin.server.port))
            channel.pipeline().addFirst("timeout-handler", TimeOutHandler(5L, TimeUnit.SECONDS))
        }
    }

}