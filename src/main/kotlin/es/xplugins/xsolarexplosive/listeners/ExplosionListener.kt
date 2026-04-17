/*
 *  xSolarExplosive - Plugin desenvolvido por xPlugins Org
 *  Copyright (c) 2026. Todos los derechos reservados.
 *  Prohibida su venta o distribución sin autorización.
 */

package es.xplugins.xsolarexplosive.listeners

import es.xplugins.xsolarexplosive.XSolarExplosive
import org.bukkit.Material
import org.bukkit.Particle
import org.bukkit.Sound
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.metadata.FixedMetadataValue
import org.bukkit.scheduler.BukkitRunnable

class ExplosionListener(private val plugin: XSolarExplosive) : Listener {

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    fun onBlockBreak(event: BlockBreakEvent) {
        val player = event.player
        val item = player.inventory.itemInMainHand
        val solar = plugin.pickaxeManager.getSolarType(item) ?: return

        val block = event.block
        val diameter = solar.radius
        if (diameter <= 1) return

        val world = block.world
        val center = block.location

        if (solar.mode == "TNT") {
            // Set metadata to identify this as a solar explosion (no damage + 100% drops)
            player.setMetadata("solar_exploding", FixedMetadataValue(plugin, true))
            
            // Create real explosion
            world.createExplosion(center, diameter.toFloat(), false, true, player)
            
            // Remove metadata in next tick
            object : BukkitRunnable() {
                override fun run() {
                    player.removeMetadata("solar_exploding", plugin)
                }
            }.runTaskLater(plugin, 1L)
            
        } else {
            // Standard Radius Mode
            val range = diameter / 2
            world.spawnParticle(Particle.FLAME, center, 20, 0.5, 0.5, 0.5, 0.1)
            world.playSound(center, Sound.ENTITY_GENERIC_EXPLODE, 0.5f, 1.2f)

            for (x in -range..range) {
                for (y in -range..range) {
                    for (z in -range..range) {
                        if (x == 0 && y == 0 && z == 0) continue
                        
                        val target = center.clone().add(x.toDouble(), y.toDouble(), z.toDouble()).block
                        if (target.type == Material.AIR || target.type == Material.BEDROCK || target.type == Material.BARRIER) continue
                        
                        target.breakNaturally(item)
                    }
                }
            }
        }
    }
}
