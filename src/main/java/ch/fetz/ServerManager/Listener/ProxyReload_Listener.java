package ch.fetz.ServerManager.Listener;

import ch.fetz.ServerManager.ServerManager;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ProxyReloadEvent;
import net.md_5.bungee.api.plugin.Listener;
import org.bukkit.event.EventHandler;

public class ProxyReload_Listener implements Listener {

    private final ServerManager plugin;

    public ProxyReload_Listener(ServerManager serverManager) {
        this.plugin = serverManager;
        plugin.getProxy().getPluginManager().registerListener(serverManager, this);
    }

    @EventHandler
    public void onReload(ProxyReloadEvent e){
        plugin.getManager().clearAllServers();
        plugin.getManager().addAllServers();
        if(plugin.enableRedis){
            plugin.getRedisUtils().sendReloadAllServers();
        }
        sendMessage(plugin.prefix + plugin.getMessages().ALL_SERVER_RELOAD_BROADCAST.replace("%PLAYER%", e.getSender().getName()));
    }

    private void sendMessage(String message){
        ProxyServer.getInstance().getConsole().sendMessage(message);
        for(ProxiedPlayer all : plugin.receiver){
            all.sendMessage(message);
        }
        if(plugin.enableRedis){
            plugin.getRedisUtils().sendBroadcastMessage(message);
        }
    }
}
