/*
 *  xSolarExplosive - Plugin desenvolvido por xPlugins Org
 *  Copyright (c) 2026. Todos los derechos reservados.
 *  Prohibida su venta o distribución sin autorización.
 */

package es.xplugins.xsolarexplosive.commands

import es.xplugins.xsolarexplosive.XSolarExplosive
import es.xplugins.xsolarexplosive.utils.ColorUtils
import org.bukkit.Bukkit
import org.bukkit.command.Command
import org.bukkit.command.CommandExecutor
import org.bukkit.command.CommandSender
import org.bukkit.command.TabCompleter
import org.bukkit.entity.Player

class SolarCommand(private val plugin: XSolarExplosive) : CommandExecutor, TabCompleter {

    override fun onCommand(sender: CommandSender, command: Command, label: String, args: Array<out String>): Boolean {
        if (!sender.hasPermission("xsolarexplosive.admin")) {
            sender.sendMessage(ColorUtils.translate(plugin.config.getString("messages.no_permission")))
            return true
        }

        if (args.isEmpty()) {
            openGUI(sender)
            return true
        }

        when (args[0].lowercase()) {
            "help" -> {
                sendHelp(sender)
            }
            "reload" -> {
                plugin.reloadConfig()
                plugin.pickaxeManager.loadPickaxes()
                sender.sendMessage(ColorUtils.translate(plugin.config.getString("messages.reload")))
            }
            "gui" -> {
                openGUI(sender)
            }
            "give" -> {
                if (args.size < 3) {
                    sender.sendMessage(ColorUtils.translate(plugin.config.getString("messages.give_usage")))
                    return true
                }
                
                val target = Bukkit.getPlayer(args[1])
                if (target == null) {
                    sender.sendMessage(ColorUtils.translate(plugin.config.getString("messages.player_not_found")))
                    return true
                }
                
                val id = args[2]
                val item = plugin.pickaxeManager.getPickaxeItem(id)
                if (item == null) {
                    sender.sendMessage(ColorUtils.translate(plugin.config.getString("messages.pickaxe_not_found")))
                    return true
                }
                
                target.inventory.addItem(item)
                val msg = plugin.config.getString("messages.received")?.replace("%name%", item.itemMeta?.displayName ?: id)
                target.sendMessage(ColorUtils.translate(msg))
            }
            else -> sendHelp(sender)
        }

        return true
    }

    private fun openGUI(sender: CommandSender) {
        if (sender is Player) {
            plugin.gui.open(sender)
        } else {
            sender.sendMessage("Comando solo para jugadores.")
        }
    }

    private fun sendHelp(sender: CommandSender) {
        val help = plugin.config.getStringList("messages.help_message")
        help.forEach { sender.sendMessage(ColorUtils.translate(it)) }
    }

    override fun onTabComplete(sender: CommandSender, command: Command, alias: String, args: Array<out String>): List<String> {
        if (args.size == 1) {
            return listOf("help", "reload", "give", "gui").filter { it.startsWith(args[0].lowercase()) }
        }
        if (args.size == 2 && args[0].lowercase() == "give") {
            return Bukkit.getOnlinePlayers().map { it.name }.filter { it.lowercase().startsWith(args[1].lowercase()) }
        }
        if (args.size == 3 && args[0].lowercase() == "give") {
            return plugin.pickaxeManager.getAllPickaxes().map { it.id }.filter { it.lowercase().startsWith(args[2].lowercase()) }
        }
        return emptyList()
    }
}
