/*
 *  xSolarExplosive - Plugin desenvolvido por xPlugins Org
 *  Copyright (c) 2026. Todos los derechos reservados.
 *  Prohibida su venta o distribución sin autorización.
 */

package es.xplugins.xsolarexplosive.managers

import es.xplugins.xsolarexplosive.XSolarExplosive

class ConfigManager(private val plugin: XSolarExplosive) {
    init {
        plugin.saveDefaultConfig()
    }

    fun reload() {
        plugin.reloadConfig()
    }
}
