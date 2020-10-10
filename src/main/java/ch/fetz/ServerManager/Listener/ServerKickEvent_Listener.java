package ch.fetz.ServerManager.Listener;

import ch.fetz.ServerManager.ServerManager;
import net.md_5.bungee.api.event.ServerKickEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.security.SecureRandom;

/**
 * Created by Noah Fetz on 06.09.2016.
 */
public class ServerKickEvent_Listener implements Listener {
    private final ServerManager plugin;

    public ServerKickEvent_Listener(ServerManager serverManager) {
        this.plugin = serverManager;
        plugin.getProxy().getPluginManager().registerListener(plugin, this);
    }

    @EventHandler
    public void onKick(ServerKickEvent e){
        if(e.getPlayer().getServer() == null && plugin.lobbies.size() > 0){
            e.getPlayer().connect(plugin.lobbies.get(new SecureRandom().nextInt(plugin.lobbies.size())));
        }
    }
}
