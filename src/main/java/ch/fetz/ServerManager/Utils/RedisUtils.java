package ch.fetz.ServerManager.Utils;

import ch.fetz.ServerManager.ServerManager;
import com.imaginarycode.minecraft.redisbungee.RedisBungee;
import com.imaginarycode.minecraft.redisbungee.events.PubSubMessageEvent;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.security.SecureRandom;

/**
 * Created by Noah Fetz on 17.11.2016.
 */
public class RedisUtils implements Listener {
    private final ServerManager plugin;
    private final String channel = "BungeeServerManager";
    private final String split = "######";

    public RedisUtils(ServerManager serverManager) {
        this.plugin = serverManager;

        if ((ProxyServer.getInstance().getPluginManager().getPlugin("RedisBungee") != null) && (RedisBungee.getApi() != null))
        {
            ProxyServer.getInstance().getPluginManager().registerListener(serverManager, this);

            RedisBungee.getApi().registerPubSubChannels(channel);
            plugin.enableRedis = true;
        }
        else
        {
            plugin.enableRedis = false;
        }
    }

    @EventHandler
    public void onPubSubMessage(PubSubMessageEvent e)
    {
        if (!e.getChannel().equals(channel)) {
            return;
        }
        String[] message = e.getMessage().split(split);
        if (message[0].equalsIgnoreCase(RedisBungee.getApi().getServerId())) {
            return;
        }
        String messageType = message[1];
        switch (messageType)
        {
            case "information":
                broadcastIncomingInformationBroadcast(message[2]);
                break;
            case "reloadall":
                reloadAllServers();
                break;
            case "reload":
                reloadServer(message[2]);
                break;
            case "add":
                addServer(message[2]);
                break;
            case "remove":
                removeServer(message[2]);
                break;
            case "empty":
                emptyServer(message[2]);
                break;
            case "setlobby":
                setIsLobby(message[2], Boolean.valueOf(message[3]));
                break;
            default:
                ProxyServer.getInstance().getLogger().warning("Undeclared BungeeServerManager redis message recieved: " + messageType);
        }
    }

    public void sendBroadcastMessage(String content) {
        if (content.trim().length() == 0) {
            return;
        }
        final String message = RedisBungee.getApi().getServerId() + split + "information" + split + content;

        ProxyServer.getInstance().getScheduler().runAsync(plugin, () -> RedisBungee.getApi().sendChannelMessage(channel, message));
    }

    private void broadcastIncomingInformationBroadcast(String message){
        ProxyServer.getInstance().getConsole().sendMessage(message);
        for(ProxiedPlayer all : plugin.receiver){
            all.sendMessage(message);
        }
    }

    public void sendReloadAllServers(){
        final String message = RedisBungee.getApi().getServerId() + split + "reloadall";

        ProxyServer.getInstance().getScheduler().runAsync(plugin, new Runnable()
        {
            public void run()
            {
                RedisBungee.getApi().sendChannelMessage(channel, message);
            }
        });
    }

    private void reloadAllServers(){
        plugin.getManager().clearAllServers();
        plugin.getManager().addAllServers();
    }

    public void sendReloadServer(String server){
        if (server.trim().length() == 0) {
            return;
        }
        final String message = RedisBungee.getApi().getServerId() + split + "reload" + split + server;

        ProxyServer.getInstance().getScheduler().runAsync(plugin, new Runnable()
        {
            public void run()
            {
                RedisBungee.getApi().sendChannelMessage(channel, message);
            }
        });
    }

    private void reloadServer(String server){
        plugin.getManager().removeServer(server, plugin.getManager().isLobby(server));
        plugin.getManager().addServer(server);
    }

    public void sendAddServer(String server){
        if (server.trim().length() == 0) {
            return;
        }
        final String message = RedisBungee.getApi().getServerId() + split + "add" + split + server;

        ProxyServer.getInstance().getScheduler().runAsync(plugin, new Runnable()
        {
            public void run()
            {
                RedisBungee.getApi().sendChannelMessage(channel, message);
            }
        });
    }

    private void addServer(String server){
        plugin.getManager().addServer(server);
    }

    public void sendRemoveServer(String server){
        if (server.trim().length() == 0) {
            return;
        }
        final String message = RedisBungee.getApi().getServerId() + split + "remove" + split + server;

        ProxyServer.getInstance().getScheduler().runAsync(plugin, new Runnable()
        {
            public void run()
            {
                RedisBungee.getApi().sendChannelMessage(channel, message);
            }
        });
    }

    private void removeServer(String server){
        ServerInfo serverInfo = ProxyServer.getInstance().getServerInfo(server);
        if(serverInfo != null){
            for(ProxiedPlayer all : ProxyServer.getInstance().getServerInfo(server).getPlayers()){
                all.connect(plugin.lobbies.get(new SecureRandom().nextInt(plugin.lobbies.size())));
                all.sendMessage(plugin.prefix + plugin.getMessages().PREVIOUS_SERVER_DELETED_INFO);
            }
            ProxyServer.getInstance().getServers().remove(server);
            plugin.lobbies.remove(serverInfo);
            plugin.nonlobbies.remove(serverInfo);
        }
    }

    public void sendEmptyServer(String server){
        if (server.trim().length() == 0) {
            return;
        }
        final String message = RedisBungee.getApi().getServerId() + split + "empty" + split + server;

        ProxyServer.getInstance().getScheduler().runAsync(plugin, new Runnable()
        {
            public void run()
            {
                RedisBungee.getApi().sendChannelMessage(channel, message);
            }
        });
    }

    private void emptyServer(String server){
        for(ProxiedPlayer all : ProxyServer.getInstance().getServerInfo(server).getPlayers()){
            if(!all.hasPermission("servermanager.ignorekick")){
                all.connect(plugin.lobbies.get(new SecureRandom().nextInt(plugin.lobbies.size())));
                all.sendMessage(plugin.prefix + plugin.getMessages().PREVIOUS_SERVER_EMPTIED);
            }
        }
    }

    public void sendSetLobby(String server, Boolean isLobby){
        if (server.trim().length() == 0) {
            return;
        }
        if(isLobby == null){
            return;
        }
        final String message = RedisBungee.getApi().getServerId() + split + "setlobby" + split + server + split + isLobby.toString();

        ProxyServer.getInstance().getScheduler().runAsync(plugin, new Runnable()
        {
            public void run()
            {
                RedisBungee.getApi().sendChannelMessage(channel, message);
            }
        });
    }

    private void setIsLobby(String server, Boolean isLobby){
        boolean setLobby = isLobby;
        setLobby = !setLobby;
        plugin.getManager().removeServer(server, setLobby);
        plugin.getManager().addServer(server);
    }
}
