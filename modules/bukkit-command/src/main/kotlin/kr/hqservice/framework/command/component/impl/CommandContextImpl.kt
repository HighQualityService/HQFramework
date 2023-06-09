package kr.hqservice.framework.command.component.impl

import com.google.common.collect.ArrayListMultimap
import com.google.common.collect.Multimap
import kr.hqservice.framework.command.component.CommandContext
import kr.hqservice.framework.command.component.ContextKey
import org.bukkit.command.CommandSender
import kotlin.reflect.KClassifier
import kotlin.reflect.KParameter
import kotlin.reflect.full.findAnnotation

class CommandContextImpl(
    private val commandSender: CommandSender,
    private val parameterMap: Map<String, KParameter>
) : CommandContext {
    //                     contextKey, argument
    private val arguments: Map<String, String> = parameterMap.map { (argument, kParameter)->
        getContextKey(kParameter) to argument
    }.toMap()

    private val argumentsByType: Multimap<KClassifier, String> = ArrayListMultimap.create<KClassifier, String>().apply {
        parameterMap.values.forEach { kParameter ->
            this.put(kParameter.type.classifier, getContextKey(kParameter))
        }
    }

    private fun getContextKey(kParameter: KParameter): String {
        return kParameter.findAnnotation<ContextKey>()?.key ?: kParameter.name!!
    }

    override fun getCommandSender(): CommandSender {
        return commandSender
    }

    override fun findArgument(key: String): String? {
        return arguments[key]
    }

    override fun getArgument(key: String): String {
        return arguments[key] ?: throw IllegalArgumentException()
    }

    override fun getArguments(): Collection<String> {
        return arguments.values
    }

    override fun getArgumentsByType(kClassifier: KClassifier): Collection<String> {
        return argumentsByType.get(kClassifier)
    }
}