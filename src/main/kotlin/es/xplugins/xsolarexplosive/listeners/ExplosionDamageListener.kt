/*
 *  xSolarExplosive - Plugin desenvolvido por xPlugins Org
 *  Copyright (c) 2026. Todos los derechos reservados.
 *  Prohibida su venta o distribución sin autorización.
 */

package es.xplugins.xsolarexplosive.listeners

import es.xplugins.xsolarexplosive.XSolarExplosive
import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.entity.EntityDamageEvent
import org.bukkit.event.entity.EntityExplodeEvent

class ExplosionDamageListener(private val plugin: XSolarExplosive) : Listener {

    @EventHandler(priority = EventPriority.LOWEST)
    fun onEntityDamage(event: EntityDamageEvent) {
        val cause = event.cause
        if (cause == EntityDamageEvent.DamageCause.BLOCK_EXPLOSION || cause == EntityDamageEvent.DamageCause.ENTITY_EXPLOSION) {
            val entity = event.entity
            
            // Check if any nearby player has the "solar_exploding" metadata
            val nearbyPlayers = entity.location.world?.getNearbyEntities(entity.location, 10.0, 10.0, 10.0) { it is Player }
            val isSolarExplosion = nearbyPlayers?.any { it.hasMetadata("solar_exploding") } ?: false
            
            if (isSolarExplosion) {
                event.isCancelled = true
            }
        }
    }

    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    fun onEntityExplode(event: EntityExplodeEvent) {
        // If it's a solar explosion (checked via source metadata or location tracking)
        // We want to ensure 100% block drops
        
        val isSolarExplosion = event.location.world?.getNearbyEntities(event.location, 2.0, 2.0, 2.0) { 
            it is Player && it.hasMetadata("solar_exploding") 
        }?.isNotEmpty() ?: false

        if (isSolarExplosion) {
            val blocks = event.blockList()
            blocks.forEach { block ->
                block.breakNaturally()
            }
            blocks.clear() // Clear to prevent standard explosion destruction (and random drops)
        }
    }
}
