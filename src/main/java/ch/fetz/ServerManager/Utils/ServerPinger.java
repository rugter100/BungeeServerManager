package ch.fetz.ServerManager.Utils;

import ch.fetz.ServerManager.ServerManager;
import net.md_5.bungee.api.config.ServerInfo;

/**
 * Created by Noah Fetz on 24.01.2017.
 */
public class ServerPinger {
    private final ServerManager plugin;

    public ServerPinger(ServerManager serverManager) {
        this.plugin = serverManager;
    }

    public void checkAllServers(){
        try{
            for(ServerInfo server : plugin.lobbies){
                server.ping((serverPing, throwable) -> {
                    plugin.getManager().setIsOnline(server.getName() , throwable == null);
                });
            }
            for(ServerInfo server : plugin.nonlobbies){
                server.ping((serverPing, throwable) -> {
                    plugin.getManager().setIsOnline(server.getName() , throwable == null);
                });
            }
        }catch (Exception ex){
        }
    }
}
