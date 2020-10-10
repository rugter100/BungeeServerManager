package ch.fetz.ServerManager.Commands;

import ch.fetz.ServerManager.ServerManager;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

import java.security.SecureRandom;

/**
 * Created by Noah Fetz on 09.06.2016.
 */
public class command_hub extends Command {
    private final ServerManager plugin;

    public command_hub(String hub, ServerManager serverManager) {
        super(hub);
        this.plugin = serverManager;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if(sender instanceof ProxiedPlayer){
            ProxiedPlayer p = (ProxiedPlayer) sender;
            if(!plugin.lobbies.contains(p.getServer().getInfo())){
                p.connect(plugin.lobbies.get(new SecureRandom().nextInt(plugin.lobbies.size())));
            }else{
                p.sendMessage(plugin.getMessages().LOBBY_ALREADY_ON_LOBBY);
            }
        }else{
            sender.sendMessage(plugin.prefix + plugin.getMessages().ONLY_INGAME_COMMAND);
        }
    }
}
