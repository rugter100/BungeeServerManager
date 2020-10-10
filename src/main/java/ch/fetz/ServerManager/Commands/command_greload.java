package ch.fetz.ServerManager.Commands;

import ch.fetz.ServerManager.ServerManager;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

/**
 * Created by Noah Fetz on 13.11.2016.
 */
public class command_greload extends Command {
    private final ServerManager plugin;

    public command_greload(String greload, ServerManager serverManager) {
        super(greload);
        this.plugin = serverManager;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if(sender instanceof ProxiedPlayer){
            ProxiedPlayer p = (ProxiedPlayer) sender;
            if(sender.hasPermission("bungeecord.command.reload")){
                p.sendMessage(plugin.prefix + "§cTo prevent a BungeeCord-Bug, this command has been blocked by " + plugin.prefix);
            }else{
                p.sendMessage(plugin.prefix + plugin.getMessages().NO_PERMISSION);
            }
        }else{
            sender.sendMessage(plugin.prefix + "§cTo prevent a BungeeCord-Bug, this command has been blocked by " + plugin.prefix);
        }
    }
}
