plugins {
    id("hqframework.shared")
    id("hqframework.bukkit-resource-generator")
    id("hqframework.shadow")
}

bukkitResourceGenerator {
    main = "kr.hqservice.framework.bukkit.HQFrameworkBukkit"
    name = "HQFramework"
    apiVersion = "1.13"
    libraries = excludedRuntimeDependencies(
        libs.kotlin.stdlib.jdk8,
        libs.kotlin.reflect,
        libs.kotlinx.coroutines.core,
        libs.kotlinx.serialization.json,
        libs.koin.core,
        libs.koin.annotations,
        libs.exposed.core,
        libs.exposed.dao,
        libs.exposed.java.time,
        libs.exposed.jdbc,
        libs.hikaricp,
        libs.sqlite,
        libs.netty,
        libs.guava,
        libs.auto.service.annotations,
        libs.snakeyaml,
        libs.configurate.yaml
    )
}

dependencies {
    compileOnly(libs.spigot.api)

    implementationModule("bukkit", "core")
    runtimeOnlyModule("global", "core")
    runtimeOnlyModule("global", "netty")
    runtimeOnlyModule("global", "yaml")
    runtimeOnlyModule("bukkit", "inventory")
    runtimeOnlyModule("bukkit", "region")
    runtimeOnlyModule("bukkit", "coroutine")
    runtimeOnlyModule("bukkit", "database")
    runtimeOnlyModule("bukkit", "nms")
}