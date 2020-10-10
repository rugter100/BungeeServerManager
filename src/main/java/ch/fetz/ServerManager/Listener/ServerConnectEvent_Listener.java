package ch.fetz.ServerManager.Listener;

import ch.fetz.ServerManager.ServerManager;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.event.ServerConnectEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.security.SecureRandom;

/**
 * Created by Noah Fetz on 27.05.2016.
 */
public class ServerConnectEvent_Listener implements Listener {
    private final ServerManager plugin;

    public ServerConnectEvent_Listener(ServerManager serverManager) {
        this.plugin = serverManager;
        plugin.getProxy().getPluginManager().registerListener(plugin, this);
    }

    @EventHandler
    public void onConnect(ServerConnectEvent e){
        try{
            String[] hostString = e.getPlayer().getPendingConnection().getVirtualHost().toString().split("\\.");
            if(hostString.length == 3 && plugin.forcedServers && e.getPlayer().getServer() == null){
                for(ServerInfo info : plugin.lobbies){
                    if(info.getName().equalsIgnoreCase(hostString[0])){
                        if(e.getPlayer().hasPermission("servermanager.server." + info.getName()) || e.getPlayer().hasPermission("servermanager.ignorerestricion")){
                            e.setTarget(info);
                        }
                    }
                }
                for(ServerInfo info : plugin.nonlobbies){
                    if(info.getName().equalsIgnoreCase(hostString[0])){
                        if(e.getPlayer().hasPermission("servermanager.server." + info.getName()) || e.getPlayer().hasPermission("servermanager.ignorerestricion")){
                            e.setTarget(info);
                        }
                    }
                }
                return;
            }
            if(e.getPlayer().getServer() == null && plugin.lobbies.size() > 0){
                e.setTarget(plugin.lobbies.get(new SecureRandom().nextInt(plugin.lobbies.size())));
            }else{
                if(plugin.getManager().isRestricted(e.getTarget().getName())){
                    if(!e.getPlayer().hasPermission("servermanager.server." + e.getTarget().getName()) && !e.getPlayer().hasPermission("servermanager.ignorerestricion")){
                        e.getPlayer().sendMessage(plugin.prefix + "§cYou're not allowed to join this server");
                        e.setCancelled(true);
                    }
                }
            }
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }
}
