/*
 *  xSolarExplosive - Plugin desenvolvido por xPlugins Org
 *  Copyright (c) 2026. Todos los derechos reservados.
 *  Prohibida su venta o distribución sin autorización.
 */

package es.xplugins.xsolarexplosive.listeners

import es.xplugins.xsolarexplosive.XSolarExplosive
import es.xplugins.xsolarexplosive.utils.ColorUtils
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.inventory.InventoryClickEvent

class GUIListener(private val plugin: XSolarExplosive) : Listener {

    @EventHandler
    fun onInventoryClick(event: InventoryClickEvent) {
        val player = event.whoClicked as? Player ?: return
        val title = event.view.title
        val expectedTitle = ColorUtils.translate(plugin.config.getString("gui.title"))

        if (title != expectedTitle) return

        event.isCancelled = true

        val clickedSlot = event.slot
        val actions = plugin.gui.getActions(clickedSlot) ?: return

        for (action in actions) {
            when {
                action.equals("[CLOSE]", true) -> player.closeInventory()
                action.startsWith("[PLAYER]", true) -> {
                    val cmd = action.substring(8).trim()
                    player.performCommand(cmd)
                }
                action.startsWith("[GIVE]", true) -> {
                    val id = action.substring(6).trim()
                    val item = plugin.pickaxeManager.getPickaxeItem(id)
                    if (item != null) {
                        player.inventory.addItem(item)
                    }
                }
            }
        }
    }
}
