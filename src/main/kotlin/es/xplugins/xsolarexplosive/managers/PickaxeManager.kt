/*
 *  xSolarExplosive - Plugin desenvolvido por xPlugins Org
 *  Copyright (c) 2026. Todos los derechos reservados.
 *  Prohibida su venta o distribución sin autorización.
 */

package es.xplugins.xsolarexplosive.managers

import es.xplugins.xsolarexplosive.XSolarExplosive
import es.xplugins.xsolarexplosive.utils.ColorUtils
import org.bukkit.Material
import org.bukkit.NamespacedKey
import org.bukkit.enchantments.Enchantment
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType

data class SolarPickaxe(
    val id: String,
    val name: String,
    val lore: List<String>,
    val radius: Int,
    val material: Material,
    val enchantments: Map<Enchantment, Int>,
    val unbreakable: Boolean,
    val hideFlags: Boolean,
    val mode: String // "RADIUS" or "TNT"
)

class PickaxeManager(private val plugin: XSolarExplosive) {
    private val pickaxes = mutableMapOf<String, SolarPickaxe>()
    private val pickaxeKey = NamespacedKey(plugin, "solar_type")

    fun loadPickaxes() {
        pickaxes.clear()
        val section = plugin.config.getConfigurationSection("pickaxes") ?: return
        for (key in section.getKeys(false)) {
            val name = section.getString("$key.display_name") ?: key
            val lore = section.getStringList("$key.lore") ?: emptyList()
            val radius = section.getInt("$key.radius", 1)
            val matStr = section.getString("$key.material") ?: "NETHERITE_PICKAXE"
            val material = Material.matchMaterial(matStr) ?: Material.NETHERITE_PICKAXE
            val unbreakable = section.getBoolean("$key.unbreakable", false)
            val hideFlags = section.getBoolean("$key.hide_flags", false)
            val mode = section.getString("$key.mode", "RADIUS")?.uppercase() ?: "RADIUS"
            
            val enchantsMap = mutableMapOf<Enchantment, Int>()
            val enchantsList = section.getStringList("$key.enchantments") ?: emptyList()
            for (line in enchantsList) {
                if (line == null) continue
                val parts = line.split(":")
                if (parts.size < 2) continue
                
                val enchantName = parts[0]
                val level = parts[1].toIntOrNull() ?: 1
                
                val enchant = getEnchantment(enchantName)
                if (enchant != null) {
                    enchantsMap[enchant] = level
                }
            }
            
            pickaxes[key] = SolarPickaxe(key, name, lore, radius, material, enchantsMap, unbreakable, hideFlags, mode)
        }
    }

    private fun getEnchantment(enchantName: String): Enchantment? {
        if (enchantName.contains(":")) {
            val parts = enchantName.split(":")
            if (parts.size >= 2) {
                val key = try { NamespacedKey(parts[0], parts[1]) } catch (e: Exception) { null }
                if (key != null) return Enchantment.getByKey(key)
            }
        }
        val legacy = Enchantment.getByName(enchantName.uppercase())
        if (legacy != null) return legacy
        return try { Enchantment.getByKey(NamespacedKey.minecraft(enchantName.lowercase())) } catch (e: Exception) { null }
    }

    fun getPickaxeItem(id: String): ItemStack? {
        val solar = pickaxes[id] ?: return null
        val item = ItemStack(solar.material)
        val meta = item.itemMeta ?: return null
        
        meta.setDisplayName(ColorUtils.translate(solar.name))
        meta.lore = ColorUtils.translateList(solar.lore)
        
        solar.enchantments.forEach { (enchant, level) ->
            meta.addEnchant(enchant, level, true)
        }

        meta.isUnbreakable = solar.unbreakable
        if (solar.hideFlags) {
            meta.addItemFlags(*ItemFlag.values())
        }
        
        meta.persistentDataContainer.set(pickaxeKey, PersistentDataType.STRING, id)
        item.itemMeta = meta
        return item
    }

    fun getSolarType(item: ItemStack?): SolarPickaxe? {
        if (item == null || !item.hasItemMeta()) return null
        val meta = item.itemMeta ?: return null
        val id = meta.persistentDataContainer.get(pickaxeKey, PersistentDataType.STRING) ?: return null
        return pickaxes[id]
    }

    fun getAllPickaxes(): Collection<SolarPickaxe> = pickaxes.values
}
