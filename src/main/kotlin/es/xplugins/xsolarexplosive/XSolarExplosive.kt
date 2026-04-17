/*
 *  xSolarExplosive - Plugin desenvolvido por xPlugins Org
 *  Copyright (c) 2026. Todos los derechos reservados.
 *  Prohibida su venta o distribución sin autorización.
 */

package es.xplugins.xsolarexplosive

import es.xplugins.xsolarexplosive.commands.SolarCommand
import es.xplugins.xsolarexplosive.gui.PickaxeGUI
import es.xplugins.xsolarexplosive.listeners.ExplosionDamageListener
import es.xplugins.xsolarexplosive.listeners.ExplosionListener
import es.xplugins.xsolarexplosive.listeners.GUIListener
import es.xplugins.xsolarexplosive.managers.ConfigManager
import es.xplugins.xsolarexplosive.managers.PickaxeManager
import es.xplugins.xsolarexplosive.utils.ColorUtils
import org.bukkit.Bukkit
import org.bukkit.plugin.java.JavaPlugin

class XSolarExplosive : JavaPlugin() {

    lateinit var configManager: ConfigManager
    lateinit var pickaxeManager: PickaxeManager
    lateinit var gui: PickaxeGUI

    override fun onEnable() {
        configManager = ConfigManager(this)
        pickaxeManager = PickaxeManager(this)
        pickaxeManager.loadPickaxes()
        gui = PickaxeGUI(this)

        val solarCommand = SolarCommand(this)
        val cmd = getCommand("solarpickaxe")
        cmd?.setExecutor(solarCommand)
        cmd?.tabCompleter = solarCommand

        server.pluginManager.registerEvents(ExplosionListener(this), this)
        server.pluginManager.registerEvents(ExplosionDamageListener(this), this)
        server.pluginManager.registerEvents(GUIListener(this), this)

        printLogo("activado")
    }

    override fun onDisable() {
        printLogo("desactivado")
    }

    private fun printLogo(status: String) {
        val logo = """
       &#FFAA00__       _             &#FFD400__            _           _           
&#FFAA00__  __/ _\ ___ | | __ _ _ __ &#FFD400/__\_  ___ __ | | ___  ___(_)_   _____ 
&#FFAA00\ \/ /\ \ / _ \| |/ _` | '__/&#FFD400_\ \ \/ / '_ \| |/ _ \/ __| \ \ / / _ \
 &#FFAA00>  < _\ \ (_) | | (_| | | //&#FFD400__  >  <| |_) | | (_) \__ \ |\ V /  __/
&#FFAA00/_/\_\\__/\___/|_|\__,_|_| \__&#FFD400/ /_/\_\ .__/|_|\___/|___/_| \_/ \___|
                                     &#FFD400|_|                            
        """.trimIndent()

        logo.split("\n").forEach { line ->
            Bukkit.getConsoleSender().sendMessage(ColorUtils.translate(line))
        }
        
        val statusMsg = if (status == "activado") 
            "&#FFAA00xSolarExplosive &fha sido &#77DD77$status &fcorrectamente! &7(v${description.version})"
        else 
            "&#FFAA00xSolarExplosive &fha sido &#FF6666$status&f. &7(v${description.version})"
            
        Bukkit.getConsoleSender().sendMessage(ColorUtils.translate(statusMsg))
    }
}
