package ch.fetz.ServerManager.Commands;

import ch.fetz.ServerManager.ServerManager;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

/**
 * Created by Noah Fetz on 15.11.2016.
 */
public class command_notify extends Command {
    private final ServerManager plugin;

    public command_notify(String notify, ServerManager serverManager) {
        super(notify);
        this.plugin = serverManager;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if(sender instanceof ProxiedPlayer){
            ProxiedPlayer p = (ProxiedPlayer) sender;
            if(p.hasPermission("servermanager.receiveinfo")){
                if(plugin.getManager().getNotificationStatus(p) && plugin.receiver.contains(p)){
                    plugin.getManager().setNotificationStatus(p, false);
                    plugin.receiver.remove(p);
                    p.sendMessage(plugin.prefix + "§7You won't receive any notifications from now on");
                }else{
                    plugin.getManager().setNotificationStatus(p, true);
                    plugin.receiver.add(p);
                    p.sendMessage(plugin.prefix + "§7You're now going to receive notifications");
                }
            }else{
                p.sendMessage(plugin.prefix + plugin.getMessages().NO_PERMISSION);
            }
        }else{
            sender.sendMessage(plugin.prefix + plugin.getMessages().ONLY_INGAME_COMMAND);
        }
    }
}
