package es.xplugins.xsolarexplosive

import es.xplugins.xsolarexplosive.commands.SolarCommand
import es.xplugins.xsolarexplosive.gui.PickaxeGUI
import es.xplugins.xsolarexplosive.listeners.ExplosionListener
import es.xplugins.xsolarexplosive.listeners.GUIListener
import es.xplugins.xsolarexplosive.managers.ConfigManager
import es.xplugins.xsolarexplosive.managers.PickaxeManager
import org.bukkit.plugin.java.JavaPlugin

class XSolarExplosive : JavaPlugin() {

    lateinit var configManager: ConfigManager
    lateinit var pickaxeManager: PickaxeManager
    lateinit var gui: PickaxeGUI

    override fun onEnable() {
        // Initialize Managers
        configManager = ConfigManager(this)
        pickaxeManager = PickaxeManager(this)
        pickaxeManager.loadPickaxes()
        
        // Initialize GUI
        gui = PickaxeGUI(this)

        // Register Commands
        val solarCommand = SolarCommand(this)
        getCommand("solarpickaxe")?.setExecutor(solarCommand)
        getCommand("solarpickaxe")?.tabCompleter = solarCommand

        // Register Listeners
        server.pluginManager.registerEvents(ExplosionListener(this), this)
        server.pluginManager.registerEvents(GUIListener(this), this)

        logger.info("xSolarExplosive ha sido activado correctamente!")
    }

    override fun onDisable() {
        logger.info("xSolarExplosive ha sido desactivado.")
    }
}
