package kr.hqservice.framework.nms.wrapper.item

import kr.hqservice.framework.nms.Version
import kr.hqservice.framework.nms.wrapper.NmsReflectionWrapper
import kr.hqservice.framework.nms.wrapper.getFunction
import kr.hqservice.framework.nms.wrapper.NmsWrapper

class NmsNBTTagCompoundWrapper(
    private val nbtTag: Any,
    reflectionWrapper: NmsReflectionWrapper
) : NmsWrapper {
    private val nbtTagClass = reflectionWrapper.getNmsClass("NBTTagCompound", Version.V_15.handle("nbt"))

    private val getStringFunction = reflectionWrapper.getFunction(nbtTagClass, "getString", listOf(String::class),
        Version.V_15.handleFunction("l") { setParameterClasses(String::class) })

    private val getIntFunction = reflectionWrapper.getFunction(nbtTagClass, "getInt", listOf(String::class),
        Version.V_15.handleFunction("i") { setParameterClasses(String::class)},
        Version.V_17.handleFunction("h") { setParameterClasses(String::class)})

    private val setIntFunction = reflectionWrapper.getFunction(nbtTagClass, "setInt", listOf(String::class, Int::class),
        Version.V_15.handleFunction("a") { setParameterClasses(String::class, Int::class)} )

    private val setStringFunction = reflectionWrapper.getFunction(nbtTagClass, "setString", listOf(String::class, String::class),
        Version.V_15.handleFunction("a") { setParameterClasses(String::class, String::class) })

    private val removeFunction = reflectionWrapper.getFunction(nbtTagClass, "remove", listOf(String::class),
        Version.V_15.handleFunction("r") { setParameterClasses(String::class) })

    private val containsFunction = reflectionWrapper.getFunction(nbtTagClass, "contains", listOf(String::class),
        Version.V_15.handleFunction("e") { setParameterClasses(String::class) })

    private val isEmptyFunction = reflectionWrapper.getFunction(nbtTagClass, "isEmpty",
        Version.V_15.handleFunction("f"),
        Version.V_19_3.handleFunction("g"))

    fun getString(key: String, def: String = ""): String {
        return getStringFunction.call(nbtTag, key) as? String?: def
    }

    fun getStringOrNull(key: String): String? {
        return getStringFunction.call(nbtTag, key) as? String?
    }

    fun setString(key: String, value: String) {
        setStringFunction.call(nbtTag, key, value)
    }

    fun getInt(key: String, def: Int = 0): Int {
        return getIntFunction.call(nbtTag, key) as? Int?: def
    }

    fun getIntOrNull(key: String): Int? {
        return getIntFunction.call(nbtTag, key) as? Int?
    }

    fun setInt(key: String, value: Int) {
        setIntFunction.call(nbtTag, key, value)
    }

    fun hasKey(key: String): Boolean {
        return containsFunction.call(nbtTag, key) as? Boolean?: false
    }

    fun remove(key: String) {
        removeFunction.call(nbtTag, key)
    }

    fun isEmpty(): Boolean {
        return isEmptyFunction.call(nbtTag) as? Boolean?: true
    }

    override fun getUnwrappedInstance(): Any {
        return nbtTag
    }
}