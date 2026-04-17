/*
 *  xSolarExplosive - Plugin desenvolvido por xPlugins Org
 *  Copyright (c) 2026. Todos los derechos reservados.
 *  Prohibida su venta o distribución sin autorización.
 */

package es.xplugins.xsolarexplosive.gui

import es.xplugins.xsolarexplosive.XSolarExplosive
import es.xplugins.xsolarexplosive.utils.ColorUtils
import es.xplugins.xsolarexplosive.utils.HeadUtils
import org.bukkit.Bukkit
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

class PickaxeGUI(private val plugin: XSolarExplosive) {

    private val slotActions = mutableMapOf<Int, List<String>>()

    fun open(player: Player) {
        val config = plugin.config
        val title = ColorUtils.translate(config.getString("gui.title", "&8Configuración Gral."))
        val size = config.getInt("gui.size", 54)
        
        val inventory = Bukkit.createInventory(null, size, title)
        slotActions.clear()

        val itemsSection = config.getConfigurationSection("gui.items")
        if (itemsSection != null) {
            for (key in itemsSection.getKeys(false)) {
                val path = "gui.items.$key"
                val slot = config.getInt("$path.slot")
                val matStr = config.getString("$path.material", "STONE") ?: "STONE"
                val name = config.getString("$path.name", " ") ?: " "
                val lore = config.getStringList("$path.lore")
                val base64 = config.getString("$path.base64")
                val actions = config.getStringList("$path.actions")

                if (slot < 0 || slot >= size) continue

                val item = if (base64 != null && matStr == "PLAYER_HEAD") {
                    HeadUtils.getCustomHead(base64)
                } else {
                    ItemStack(Material.matchMaterial(matStr) ?: Material.STONE)
                }

                val meta = item.itemMeta
                if (meta != null) {
                    meta.setDisplayName(ColorUtils.translate(name))
                    meta.lore = ColorUtils.translateList(lore)
                    item.itemMeta = meta
                }

                inventory.setItem(slot, item)
                if (actions.isNotEmpty()) {
                    slotActions[slot] = actions
                }
            }
        }

        val pickaxes = plugin.pickaxeManager.getAllPickaxes()
        var currentSlot = 19
        
        for (solar in pickaxes) {
            while (currentSlot < size && (inventory.getItem(currentSlot) != null || currentSlot == 49)) {
                currentSlot++
            }
            
            if (currentSlot >= size) break
            
            val item = plugin.pickaxeManager.getPickaxeItem(solar.id) ?: continue
            inventory.setItem(currentSlot, item)
            slotActions[currentSlot] = listOf("[GIVE] ${solar.id}")
            currentSlot++
        }

        player.openInventory(inventory)
    }

    fun getActions(slot: Int): List<String>? = slotActions[slot]
}
