package ch.fetz.ServerManager.Listener;

import ch.fetz.ServerManager.ServerManager;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ServerPing;
import net.md_5.bungee.api.event.ProxyPingEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

/**
 * Created by Noah Fetz on 13.08.2016.
 */
public class Ping_Listener implements Listener {

    private final ServerManager plugin;

    public Ping_Listener(ServerManager serverManager) {
        this.plugin = serverManager;
        plugin.getProxy().getPluginManager().registerListener(serverManager, this);
    }

    @EventHandler
    public void onPing(ProxyPingEvent e){
        ServerPing ping = e.getResponse();
        ServerPing.Players players = ping.getPlayers();
        if(plugin.enableMOTD){
            ping.setDescription(ChatColor.translateAlternateColorCodes('&', plugin.motd));
        }
        if(plugin.onemoreplayer){
            players.setMax(plugin.getProxy().getOnlineCount() + 1);
        }
    }
}
