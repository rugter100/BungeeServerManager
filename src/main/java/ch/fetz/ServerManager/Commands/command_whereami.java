package ch.fetz.ServerManager.Commands;

import ch.fetz.ServerManager.ServerManager;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

/**
 * Created by Noah Fetz on 17.06.2016.
 */
public class command_whereami extends Command {
    private final ServerManager plugin;

    public command_whereami(String whereami, ServerManager serverManager) {
        super(whereami);
        this.plugin = serverManager;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if(sender instanceof ProxiedPlayer){
            ProxiedPlayer p = (ProxiedPlayer) sender;
            try{
                if(p.getServer().getInfo() != null){
                    p.sendMessage(plugin.prefix + plugin.getMessages().WHEREAMI_SERVER_INFO.replace("%SERVER%", plugin.getManager().getDisplayName(p.getServer().getInfo().getName())));
                }else{
                    p.sendMessage(plugin.prefix + plugin.getMessages().WHEREAMI_CANNOT_FIND_SERVER);
                }
            }catch (Exception e){
                p.sendMessage(plugin.prefix + "§cAn issue occured while executing the command");
            }
        }else{
            sender.sendMessage(plugin.prefix + plugin.getMessages().ONLY_INGAME_COMMAND);
        }
    }
}
