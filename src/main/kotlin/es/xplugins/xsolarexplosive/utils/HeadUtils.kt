/*
 *  xSolarExplosive - Plugin desenvolvido por xPlugins Org
 *  Copyright (c) 2026. Todos los derechos reservados.
 *  Prohibida su venta o distribución sin autorización.
 */

package es.xplugins.xsolarexplosive.utils

import org.bukkit.Material
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.SkullMeta
import java.util.*

object HeadUtils {
    /**
     * Creates a player head ItemStack from a base64 texture string using reflection
     * to avoid compile-time dependencies on authlib.
     */
    fun getCustomHead(base64: String): ItemStack {
        val head = ItemStack(Material.PLAYER_HEAD)
        val meta = head.itemMeta as? SkullMeta ?: return head

        try {
            val gameProfileClass = Class.forName("com.mojang.authlib.GameProfile")
            val propertyClass = Class.forName("com.mojang.authlib.properties.Property")
            
            val profile = gameProfileClass.getConstructor(UUID::class.java, String::class.java)
                .newInstance(UUID.randomUUID(), "")
            
            val properties = gameProfileClass.getMethod("getProperties").invoke(profile)
            
            val property = propertyClass.getConstructor(String::class.java, String::class.java)
                .newInstance("textures", base64)
            
            properties.javaClass.getMethod("put", Object::class.java, Object::class.java)
                .invoke(properties, "textures", property)

            val field = meta.javaClass.getDeclaredField("profile")
            field.isAccessible = true
            field.set(meta, profile)
        } catch (e: Exception) {
            // Log error
        }

        head.itemMeta = meta
        return head
    }
}
