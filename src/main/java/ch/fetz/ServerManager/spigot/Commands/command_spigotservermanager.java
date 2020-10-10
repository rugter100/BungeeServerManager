package ch.fetz.ServerManager.spigot.Commands;

import ch.fetz.ServerManager.spigot.SpigotServerManager;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Created by Noah Fetz on 04.12.2016.
 */
public class command_spigotservermanager implements CommandExecutor {
    private final SpigotServerManager plugin;

    public command_spigotservermanager(SpigotServerManager spigotServerManager) {
        this.plugin = spigotServerManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String s, String[] args) {
        if(sender instanceof Player){
            Player p = (Player) sender;
            if(p.hasPermission("smc.use")){
                plugin.getMenuManager().openMainMenu(p, 1);
            }else{
                p.sendMessage(plugin.prefix + "§cYou don't have permission to execute this command");
            }
        }else{
            sender.sendMessage(plugin.prefix + "§cThis command can only be executed as a player");
        }
        return true;
    }
}
