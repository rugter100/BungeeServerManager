package ch.fetz.ServerManager.Commands;

import ch.fetz.ServerManager.ServerManager;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.command.PlayerCommand;

/**
 * Created by Noah Fetz on 03.08.2016.
 */
public class command_goto extends PlayerCommand {
    private final ServerManager plugin;

    public command_goto(String aGoto, ServerManager serverManager) {
        super(aGoto);
        this.plugin = serverManager;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if(sender instanceof ProxiedPlayer){
            ProxiedPlayer p = (ProxiedPlayer) sender;
            if(p.hasPermission("servermanager.goto")){
                if(args.length != 1){
                    p.sendMessage(plugin.prefix + plugin.getMessages().GOTO_DESCRIPTION);
                    return;
                }else{
                    String pname = args[0];
                    ProxiedPlayer p2 = ProxyServer.getInstance().getPlayer(args[0]);
                    if(p2 == null){
                        p.sendMessage(plugin.prefix + plugin.getMessages().GOTO_PLAYER_NOT_ONLINE.replace("%PLAYER%", pname));
                    }else{
                        ServerInfo target = p2.getServer().getInfo();
                        if(p.getServer().getInfo() != p2.getServer().getInfo()){
                            p.connect(target);
                            p.sendMessage(plugin.prefix + plugin.getMessages().GOTO_CONNECTED.replace("%PLAYER%", p2.getName()));
                        }else{
                            p.sendMessage(plugin.prefix + plugin.getMessages().GOTO_ALREADY_ON_SERVER.replace("%PLAYER%", p2.getName()));
                        }
                    }
                }
            }else{
                p.sendMessage(plugin.prefix + plugin.getMessages().NO_PERMISSION);
            }
        }else{
            sender.sendMessage(plugin.prefix + plugin.getMessages().ONLY_INGAME_COMMAND);
        }
    }
}
