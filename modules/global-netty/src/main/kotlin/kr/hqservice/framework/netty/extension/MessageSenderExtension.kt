package kr.hqservice.framework.netty.extension

import kr.hqservice.framework.netty.api.NettyChannel
import kr.hqservice.framework.netty.api.NettyPlayer
import kr.hqservice.framework.netty.api.PacketSender
import org.koin.java.KoinJavaComponent.getKoin

private val packetSender: PacketSender by getKoin().inject()

fun NettyPlayer.sendMessage(message: String, logging: Boolean = true) {
    packetSender.sendMessageToPlayer(this, message, logging)
}

fun NettyChannel.sendMessage(message: String, logging: Boolean = true) {
    packetSender.sendMessageToChannel(this, message, logging)
}