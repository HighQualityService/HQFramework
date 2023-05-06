package kr.hqservice.framework.core

import org.bukkit.Bukkit
import org.bukkit.Server
import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Module
import org.koin.core.annotation.Single

@Module
@ComponentScan
class HQFrameworkModule {
    @Single
    fun provideServer(): Server {
        return Bukkit.getServer()
    }
}