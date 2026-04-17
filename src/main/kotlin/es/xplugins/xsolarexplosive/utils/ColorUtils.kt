/*
 *  xSolarExplosive - Plugin desenvolvido por xPlugins Org
 *  Copyright (c) 2026. Todos los derechos reservados.
 *  Prohibida su venta o distribución sin autorización.
 */

package es.xplugins.xsolarexplosive.utils

import net.md_5.bungee.api.ChatColor
import java.util.regex.Pattern

object ColorUtils {
    private val hexPattern = Pattern.compile("&#([A-Fa-f0-9]{6})")

    fun translate(message: String?): String {
        val input: String = message ?: return ""
        
        var translated: String = input
        val matcher = hexPattern.matcher(translated)
        
        while (matcher.find()) {
            val hexCode = matcher.group(1) ?: continue
            val hexString = "#$hexCode"
            val replacement = ChatColor.of(hexString).toString()
            translated = translated.replace("&#$hexCode", replacement)
        }
        
        return ChatColor.translateAlternateColorCodes('&', translated)
    }

    fun translateList(list: List<String>): List<String> {
        return list.map { translate(it) }
    }
}
