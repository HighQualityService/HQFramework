package kr.hqservice.framework.inventory.button

import kr.hqservice.framework.inventory.button.impl.HQButtonImpl
import kr.hqservice.framework.inventory.event.ButtonClickEvent
import kr.hqservice.framework.inventory.exception.IllegalMaterialException
import org.bukkit.ChatColor
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta

class HQButtonBuilder(
    private val itemStack: ItemStack
) {
    private var displayName: String = itemStack.itemMeta?.displayName ?: ""
    private var lore: MutableList<String> = itemStack.itemMeta?.lore ?: mutableListOf()
    private var itemFlags: MutableSet<ItemFlag> = itemStack.itemMeta?.itemFlags ?.toMutableSet()?: mutableSetOf()
    private var customModelData: Int = 0
    private var removable = false

    private var itemMetaEditScope: ItemMeta.() -> Unit = {}

    var glow = false
        set(value) {
            field = value
            if (value) itemStack.addUnsafeEnchantment(Enchantment.LURE, 1)
            else itemStack.removeEnchantment(Enchantment.LURE)
        }

    private var clickFunction: (ButtonClickEvent)->Unit = {}

    constructor(material: Material) : this(material, 1)

    constructor(material: Material, amount: Int) : this(ItemStack(material, amount)) {
        if (material == Material.AIR || !material.isItem) throw IllegalMaterialException(material)
    }

    @Deprecated(message = "setDurability")
    constructor(material: Material, amount: Int, data: Short) : this(material, amount) {
        itemStack.durability = data
    }

    fun setDisplayName(name: String): HQButtonBuilder {
        displayName = name
        return this
    }

    fun setLore(lore: List<String>): HQButtonBuilder {
        this.lore.clear()
        this.lore.addAll(lore)
        return this
    }

    fun addLore(lore: String): HQButtonBuilder {
        this.lore.add(lore)
        return this
    }

    fun getLore(): List<String> {
        return lore
    }

    fun setCustomModelData(customModelData: Int): HQButtonBuilder {
        this.customModelData = customModelData
        return this
    }

    fun addItemFlags(vararg itemFlag: ItemFlag): HQButtonBuilder {
        itemFlags.addAll(itemFlag)
        return this
    }

    fun setRemovable(removable: Boolean): HQButtonBuilder {
        this.removable = removable
        return this
    }

    fun setClickFunction(block: (ButtonClickEvent) -> Unit): HQButtonBuilder {
        clickFunction = block
        return this
    }

    fun setMeta(scope: ItemMeta.() -> Unit): HQButtonBuilder {
        this.itemMetaEditScope = scope
        return this
    }

    fun build(): HQButton {
        itemStack.itemMeta = itemStack.itemMeta?.also { meta ->
            meta.setDisplayName(ChatColor.translateAlternateColorCodes('&', displayName))
            meta.lore = lore.map { ChatColor.translateAlternateColorCodes('&', it) }
            meta.addItemFlags(*itemFlags.toTypedArray())
            try { meta.setCustomModelData(customModelData) } catch (_: Exception) {}
            meta.itemMetaEditScope()
        }
        return HQButtonImpl(itemStack, clickFunction, removable)
    }
}