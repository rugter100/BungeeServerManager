package ch.fetz.ServerManager.Commands;

import ch.fetz.ServerManager.ServerManager;
import com.google.common.collect.ImmutableSet;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.TabExecutor;

import java.security.SecureRandom;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by Noah Fetz on 20.05.2016.
 */
public class command_servers extends Command implements TabExecutor {
    private final ServerManager plugin;

    public command_servers(String servermanager, ServerManager serverManager) {
        super(servermanager);
        this.plugin = serverManager;
    }

    @Override
    public void execute(final CommandSender sender, final String[] args) {
        ProxyServer.getInstance().getScheduler().runAsync(plugin, () -> {
            if(sender instanceof ProxiedPlayer){
                ProxiedPlayer p = (ProxiedPlayer) sender;
                if(args.length == 0){
                    if(p.hasPermission("servermanager.help")) {
                        sendAvailableCommands(p);
                    }else {
                        p.sendMessage(plugin.prefix + plugin.getMessages().NO_PERMISSION);
                    }
                    return;
                }
                if(args.length == 1){
                    if(args[0].equalsIgnoreCase("list")) {
                        if (p.hasPermission("servermanager.servers.list")) {
                            p.sendMessage(plugin.prefix + plugin.getMessages().SERVERS_LIST_HEADER);
                            int i = 1;
                            for (String name : plugin.getManager().getAllServers()) {
                                p.sendMessage(plugin.getMessages().SERVER_LIST_INFO.replace("%POSITION%", String.valueOf(i)).replace("%NAME%", name).replace("%ISONLINE%", onlineOfflineString(plugin.getManager().isOnline(name))).replace("%ISENABLED%", trueFalseString(plugin.getManager().isActive(name))).replace("%ISLOBBY%", trueFalseString(plugin.getManager().isLobby(name))).replace("%DISPLAYNAME%", plugin.getManager().getDisplayName(name)).replace("%ISRESTRICTED%", trueFalseString(plugin.getManager().isRestricted(name))).replace("%IP%", plugin.getManager().getIp(name)).replace("%PORT%", String.valueOf(plugin.getManager().getPort(name))));
                                p.sendMessage(" ");
                                i++;
                            }
                        } else {
                            p.sendMessage(plugin.prefix + plugin.getMessages().NO_PERMISSION);
                        }
                    }else if(args[0].equalsIgnoreCase("reload")){
                        if (p.hasPermission("servermanager.servers.reload")) {
                            p.sendMessage(plugin.prefix + "§aExecuting...");
                            plugin.getManager().clearAllServers();
                            plugin.getManager().addAllServers();
                            p.sendMessage(plugin.prefix + "§aDone");
                            sendMessage(plugin.prefix + plugin.getMessages().ALL_SERVER_RELOAD_BROADCAST.replace("%PLAYER%", p.getName()));
                        }else{
                            p.sendMessage(plugin.prefix + plugin.getMessages().NO_PERMISSION);
                        }
                    }else{
                        sendAvailableCommands(p);
                    }
                    return;
                }
                if(args.length == 2){
                    if(args[0].equalsIgnoreCase("delete")){
                        if(p.hasPermission("servermanager.servers.delete")){
                            String name = args[1];
                            if(plugin.getManager().isInDatabase(name)){
                                if(plugin.getManager().isActive(name)){
                                    for(ProxiedPlayer all : ProxyServer.getInstance().getServerInfo(name).getPlayers()){
                                        all.connect(plugin.lobbies.get(new SecureRandom().nextInt(plugin.lobbies.size())));
                                        all.sendMessage(plugin.prefix + plugin.getMessages().PREVIOUS_SERVER_DELETED_INFO);
                                    }
                                }
                                p.sendMessage(plugin.prefix + "§aExecuting...");
                                plugin.getManager().removeServer(name);
                                p.sendMessage(plugin.prefix + "§aDone");
                                sendMessage(plugin.prefix + plugin.getMessages().SERVER_DELETED_BROADCAST.replace("%SERVER%", plugin.getManager().getDisplayName(name)).replace("%PLAYER%", p.getName()));
                            }else{
                                p.sendMessage(plugin.prefix + plugin.getMessages().SERVER_NOT_FOUND);
                            }
                        }else{
                            p.sendMessage(plugin.prefix + plugin.getMessages().NO_PERMISSION);
                        }
                    }else if(args[0].equalsIgnoreCase("reload")) {
                        if (p.hasPermission("servermanager.servers.reload")) {
                            String name = args[1];
                            if (plugin.getManager().isInDatabase(name)) {
                                p.sendMessage(plugin.prefix + "§aExecuting...");
                                plugin.getManager().removeServer(name, plugin.getManager().isLobby(name));
                                plugin.getManager().addServer(name);
                                p.sendMessage(plugin.prefix + "§aDone");
                                sendMessage(plugin.prefix + plugin.getMessages().SERVER_RELOADED_BROADCAST.replace("%DISPLAYNAME%", plugin.getManager().getDisplayName(name)).replace("%PLAYER%", p.getName()));
                            } else {
                                p.sendMessage(plugin.prefix + plugin.getMessages().SERVER_NOT_FOUND);
                            }
                        } else {
                            p.sendMessage(plugin.prefix + plugin.getMessages().NO_PERMISSION);
                        }
                    }else if(args[0].equalsIgnoreCase("enable")){
                        if(p.hasPermission("servermanager.servers.enable")){
                            String name = args[1];
                            if(plugin.getManager().isInDatabase(name)){
                                if(!plugin.getManager().isActive(name)) {
                                    p.sendMessage(plugin.prefix + "§aExecuting...");
                                    plugin.getManager().setIsActive(name, true);
                                    plugin.getManager().addServer(name);
                                    p.sendMessage(plugin.prefix + "§aDone");
                                    sendMessage(plugin.prefix + plugin.getMessages().SERVER_ENABLED_BROADCAST.replace("%DISPLAYNAME%", plugin.getManager().getDisplayName(name)).replace("%PLAYER%", p.getName()));
                                }else{
                                    p.sendMessage(plugin.prefix + plugin.getMessages().SERVER_ALREADY_ENABLED);
                                }
                            }else{
                                p.sendMessage(plugin.prefix + plugin.getMessages().SERVER_NOT_FOUND);
                            }
                        }else{
                            p.sendMessage(plugin.prefix + plugin.getMessages().NO_PERMISSION);
                        }
                    }else if(args[0].equalsIgnoreCase("disable")) {
                        if (p.hasPermission("servermanager.servers.disable")) {
                            String name = args[1];
                            if (plugin.getManager().isInDatabase(name)) {
                                if (plugin.getManager().isActive(name)) {
                                    p.sendMessage(plugin.prefix + "§aExecuting...");
                                    plugin.getManager().setIsActive(name, false);
                                    for(ProxiedPlayer all : ProxyServer.getInstance().getServerInfo(name).getPlayers()){
                                        all.connect(plugin.lobbies.get(new SecureRandom().nextInt(plugin.lobbies.size())));
                                        all.sendMessage(plugin.prefix + plugin.getMessages().PREVIOUS_SERVER_DISABLED);
                                    }
                                    plugin.getManager().removeServer(name, plugin.getManager().isLobby(name));
                                    p.sendMessage(plugin.prefix + "§aDone");
                                    sendMessage(plugin.prefix + plugin.getMessages().SERVER_DISABLED_BROADCAST.replace("%DISPLAYNAME%", plugin.getManager().getDisplayName(name)).replace("%PLAYER%", p.getName()));
                                } else {
                                    p.sendMessage(plugin.prefix + plugin.getMessages().SERVER_ALREADY_DISABLED);
                                }
                            } else {
                                p.sendMessage(plugin.prefix + plugin.getMessages().SERVER_NOT_FOUND);
                            }
                        } else {
                            p.sendMessage(plugin.prefix + plugin.getMessages().NO_PERMISSION);
                        }
                    }else if(args[0].equalsIgnoreCase("info")) {
                        if (p.hasPermission("servermanager.servers.info")) {
                            String name = args[1];
                            if (plugin.getManager().isInDatabase(name)) {
                                p.sendMessage(plugin.prefix + "§7Info about§8: " + plugin.getManager().getDisplayName(name));
                                p.sendMessage("§7Systemname§8:§a " + name);
                                p.sendMessage(" ");
                                p.sendMessage("§7Status§8: " + onlineOfflineString(plugin.getManager().isOnline(name)));
                                p.sendMessage(" ");
                                p.sendMessage("§7Displayname§8: " + plugin.getManager().getDisplayName(name));
                                p.sendMessage(" ");
                                p.sendMessage("§7Enabled§8: " + trueFalseString(plugin.getManager().isActive(name)));
                                p.sendMessage(" ");
                                p.sendMessage("§7Lobby§8: " + trueFalseString(plugin.getManager().isActive(name)));
                                p.sendMessage(" ");
                                p.sendMessage("§7Restricted§8: " + trueFalseString(plugin.getManager().isActive(name)));
                                p.sendMessage(" ");
                                p.sendMessage("§7IP§8:§a " + plugin.getManager().getIp(name));
                                p.sendMessage(" ");
                                p.sendMessage("§7Port§8:§a " + plugin.getManager().getPort(name));
                                p.sendMessage(" ");
                                String players = null;
                                if (plugin.getManager().isActive(name) && ProxyServer.getInstance().getServerInfo(name) != null) {
                                    ServerInfo serverInfo = ProxyServer.getInstance().getServerInfo(name);
                                    for (ProxiedPlayer all : serverInfo.getPlayers()) {
                                        if (players == null) {
                                            players = "§a" + all.getName();
                                        } else {
                                            players = players + "§7, " + all.getName();
                                        }
                                    }
                                } else {
                                    players = "§cOffline";
                                }
                                p.sendMessage("§7Players§8: " + players);
                            }else{
                                p.sendMessage(plugin.prefix + plugin.getMessages().SERVER_NOT_FOUND);
                            }
                        } else {
                            p.sendMessage(plugin.prefix + plugin.getMessages().NO_PERMISSION);
                        }
                    }else if(args[0].equalsIgnoreCase("kick")){
                        if(p.hasPermission("servermanager.servers.kick")){
                            String name = args[1];
                            if (plugin.getManager().isInDatabase(name)) {
                                if(plugin.getManager().isActive(name) && ProxyServer.getInstance().getServerInfo(name) != null){
                                    p.sendMessage(plugin.prefix + "§aExecuting...");
                                    for(ProxiedPlayer all : ProxyServer.getInstance().getServerInfo(name).getPlayers()){
                                        if(!all.hasPermission("servermanager.ignorekick")){
                                            all.connect(plugin.lobbies.get(new SecureRandom().nextInt(plugin.lobbies.size())));
                                            all.sendMessage(plugin.prefix + plugin.getMessages().PREVIOUS_SERVER_EMPTIED);
                                        }
                                    }
                                    p.sendMessage(plugin.prefix + "§aDone");
                                    sendMessage(plugin.prefix + "§7The server " + plugin.getManager().getDisplayName(name) + " §7has been emptied by §4" + p.getName());
                                }else{
                                    p.sendMessage(plugin.prefix + plugin.getMessages().SERVER_NOT_ACTIVE);
                                }
                            }else{
                                p.sendMessage(plugin.prefix + plugin.getMessages().SERVER_NOT_FOUND);
                            }
                        }else{
                            p.sendMessage(plugin.prefix + plugin.getMessages().NO_PERMISSION);
                        }
                    }else{
                        sendAvailableCommands(p);
                    }
                    return;
                }
                if(args.length == 3){
                    if(args[0].equalsIgnoreCase("setlobby")) {
                        if (p.hasPermission("servermanager.servers.setlobby")) {
                            String name = args[1];
                            if (plugin.getManager().isInDatabase(name)) {
                                Boolean isLobby;
                                try{
                                    isLobby = Boolean.parseBoolean(args[2]);
                                }catch (Exception ex){
                                    p.sendMessage(plugin.prefix + "§7isLobby has to be §atrue §7or §cfalse");
                                    return;
                                }
                                if(isLobby){
                                    if(!plugin.getManager().isLobby(name)){
                                        p.sendMessage(plugin.prefix + "§aExecuting...");
                                        plugin.getManager().removeServer(name, plugin.getManager().isLobby(name));
                                        plugin.getManager().setIsLobby(name, true);
                                        plugin.getManager().addServer(name);
                                        p.sendMessage(plugin.prefix + "§aDone");
                                        sendMessage(plugin.prefix + "§7The server " + plugin.getManager().getDisplayName(name) + " §7has been flagged as a lobby by §4" + p.getName());
                                    }else{
                                        p.sendMessage(plugin.prefix + "§7This server is already a lobby");
                                    }
                                }else{
                                    if(plugin.getManager().isLobby(name)){
                                        p.sendMessage(plugin.prefix + "§aExecuting...");
                                        plugin.getManager().removeServer(name, plugin.getManager().isLobby(name));
                                        plugin.getManager().setIsLobby(name, false);
                                        plugin.getManager().addServer(name);
                                        p.sendMessage(plugin.prefix + "§aDone");
                                        sendMessage(plugin.prefix + "§7The server " + plugin.getManager().getDisplayName(name) + " §7has been unflagged as a lobby by §4" + p.getName());
                                    }else{
                                        p.sendMessage(plugin.prefix + "§7This server is not a lobby");
                                    }
                                }
                            } else {
                                p.sendMessage(plugin.prefix + plugin.getMessages().SERVER_NOT_FOUND);
                            }
                        } else {
                            p.sendMessage(plugin.prefix + plugin.getMessages().NO_PERMISSION);
                        }
                    }else if(args[0].equalsIgnoreCase("setrestricted")) {
                        if (p.hasPermission("servermanager.servers.setrestricted")) {
                            String name = args[1];
                            if (plugin.getManager().isInDatabase(name)) {
                                Boolean isrestricted;
                                try{
                                    isrestricted = Boolean.parseBoolean(args[2]);
                                }catch (Exception ex){
                                    p.sendMessage(plugin.prefix + "§7isrestricted has to be §atrue §7or §cfalse");
                                    return;
                                }
                                if(isrestricted){
                                    if(!plugin.getManager().isRestricted(name)){
                                        p.sendMessage(plugin.prefix + "§aExecuting...");
                                        plugin.getManager().setIsRestricted(name, true);
                                        p.sendMessage(plugin.prefix + "§aDone");
                                        sendMessage(plugin.prefix + "§7The server " + plugin.getManager().getDisplayName(name) + " §7has been restricted by §4BungeeConsole");
                                    }else{
                                        p.sendMessage(plugin.prefix + "§7This server is already restricted");
                                    }
                                }else{
                                    if(plugin.getManager().isRestricted(name)){
                                        p.sendMessage(plugin.prefix + "§aExecuting...");
                                        plugin.getManager().setIsRestricted(name, false);
                                        p.sendMessage(plugin.prefix + "§aDone");
                                        sendMessage(plugin.prefix + "§7The server " + plugin.getManager().getDisplayName(name) + " §7has been unrestricted by §4BungeeConsole");
                                    }else{
                                        p.sendMessage(plugin.prefix + "§7This server is not restricted");
                                    }
                                }
                            } else {
                                p.sendMessage(plugin.prefix + plugin.getMessages().NO_PERMISSION);
                            }
                        } else {
                            p.sendMessage(plugin.prefix + plugin.getMessages().NO_PERMISSION);
                        }
                    }else{
                        sendAvailableCommands(p);
                    }
                    return;
                }
                if(args.length >= 6){
                    if(args[0].equalsIgnoreCase("add")){
                        if(p.hasPermission("servermanager.servers.add")){
                            String name = args[1];
                            String ip = args[2];
                            Integer port;
                            Boolean isLobby;
                            Boolean isEnabled;
                            Boolean isRestricted;

                            try{
                                port = Integer.parseInt(args[3]);
                            }catch(Exception ex){
                                p.sendMessage(plugin.prefix + "§7The port has to be a number");
                                return;
                            }
                            try{
                                isLobby = Boolean.parseBoolean(args[4]);
                            }catch(Exception ex){
                                p.sendMessage(plugin.prefix + "§7isLobby has to be §atrue §7or §cfalse");
                                return;
                            }
                            try{
                                isEnabled = Boolean.parseBoolean(args[5]);
                            }catch(Exception ex){
                                p.sendMessage(plugin.prefix + "§7isActive has to be §atrue §7or §cfalse");
                                return;
                            }
                            try{
                                isRestricted = Boolean.parseBoolean(args[6]);
                            }catch (Exception ex){
                                p.sendMessage(plugin.prefix + "§7isRestricted has to be §atrue §7or §cfalse");
                                return;
                            }
                            String displayname = "";
                            for(int i = 7; i < args.length; i++) {
                                displayname = displayname + " " + args[i];
                                displayname = ChatColor.translateAlternateColorCodes('&', displayname);
                            }
                            if(!plugin.getManager().isInDatabase(name)){
                                p.sendMessage(plugin.prefix + "§aExecuting...");
                                plugin.getManager().createServer(name, ip, port, plugin.motd, displayname, isLobby, isEnabled, isRestricted, false);
                                p.sendMessage(plugin.prefix + "§aDone");
                                sendMessage(plugin.prefix + "§7The server " + plugin.getManager().getDisplayName(name) + " §7has been added by §4" + p.getName());
                            }else{
                                p.sendMessage(plugin.prefix + "§7This server is already in the database");
                            }
                        }else{
                            p.sendMessage(plugin.prefix + plugin.getMessages().NO_PERMISSION);
                        }
                    }else{
                        sendAvailableCommands(p);
                    }
                    return;
                }else{
                    sendAvailableCommands(p);
                }
            }else{
                if(args.length == 0){
                    sendAvailableConsoleCommands(sender);
                    return;
                }
                if(args.length == 1){
                    if(args[0].equalsIgnoreCase("list")) {
                        sender.sendMessage(plugin.prefix + "§7The following servers are registered in the system§8:");
                        int i = 1;
                        for (String name : plugin.getManager().getAllServers()) {
                            sender.sendMessage(plugin.getMessages().SERVER_LIST_INFO.replace("%POSITION%", String.valueOf(i)).replace("%NAME%", name).replace("%ISONLINE%", onlineOfflineString(plugin.getManager().isOnline(name))).replace("%ISENABLED%", trueFalseString(plugin.getManager().isActive(name))).replace("%ISLOBBY%", trueFalseString(plugin.getManager().isLobby(name))).replace("%DISPLAYNAME%", plugin.getManager().getDisplayName(name)).replace("%ISRESTRICTED%", trueFalseString(plugin.getManager().isRestricted(name))).replace("%IP%", plugin.getManager().getIp(name)).replace("%PORT%", String.valueOf(plugin.getManager().getPort(name))));
                            sender.sendMessage(" ");
                            i++;
                        }
                    }else if(args[0].equalsIgnoreCase("reload")){
                        plugin.getManager().clearAllServers();
                        plugin.getManager().addAllServers();
                        sendMessage(plugin.prefix + plugin.getMessages().ALL_SERVER_RELOAD_BROADCAST.replace("%PLAYER%", "§4BungeeConsole"));
                    }else{
                        sendAvailableConsoleCommands(sender);
                    }
                    return;
                }
                if(args.length == 2){
                    if(args[0].equalsIgnoreCase("delete")){
                        String name = args[1];
                        if(plugin.getManager().isInDatabase(name)){
                            if(plugin.getManager().isActive(name)){
                                for(ProxiedPlayer all : ProxyServer.getInstance().getServerInfo(name).getPlayers()){
                                    all.connect(plugin.lobbies.get(new SecureRandom().nextInt(plugin.lobbies.size())));
                                    all.sendMessage(plugin.prefix + "§7The server you were previously on has been deleted! You have been connected to a lobby");
                                }
                            }
                            plugin.getManager().removeServer(name);
                            sendMessage(plugin.prefix + plugin.getMessages().SERVER_DELETED_BROADCAST.replace("%SERVER%", plugin.getManager().getDisplayName(name)).replace("%PLAYER%", "§4BungeeConsole"));
                        }else{
                            sender.sendMessage(plugin.prefix + plugin.getMessages().SERVER_NOT_FOUND);
                        }
                    }else if(args[0].equalsIgnoreCase("reload")) {
                        String name = args[1];
                        if (plugin.getManager().isInDatabase(name)) {
                            plugin.getManager().removeServer(name, plugin.getManager().isLobby(name));
                            plugin.getManager().addServer(name);
                            sendMessage(plugin.prefix + plugin.getMessages().SERVER_RELOADED_BROADCAST.replace("%DISPLAYNAME%", plugin.getManager().getDisplayName(name)).replace("%PLAYER%", "§4BungeeConsole"));
                        } else {
                            sender.sendMessage(plugin.prefix + plugin.getMessages().SERVER_NOT_FOUND);
                        }
                    }else if(args[0].equalsIgnoreCase("enable")){
                        String name = args[1];
                        if(plugin.getManager().isInDatabase(name)){
                            if(!plugin.getManager().isActive(name)) {
                                plugin.getManager().setIsActive(name, true);
                                plugin.getManager().addServer(name);
                                sendMessage(plugin.prefix + "§7The server " + plugin.getManager().getDisplayName(name) + " §7has been enabled by §4BungeeConsole");
                            }else{
                                sender.sendMessage(plugin.prefix + plugin.getMessages().SERVER_ALREADY_ENABLED);
                            }
                        }else{
                            sender.sendMessage(plugin.prefix + plugin.getMessages().SERVER_NOT_FOUND);
                        }
                    }else if(args[0].equalsIgnoreCase("disable")) {
                        String name = args[1];
                        if (plugin.getManager().isInDatabase(name)) {
                            if (plugin.getManager().isActive(name)) {
                                plugin.getManager().setIsActive(name, false);
                                for(ProxiedPlayer all : ProxyServer.getInstance().getServerInfo(name).getPlayers()){
                                    all.connect(plugin.lobbies.get(new SecureRandom().nextInt(plugin.lobbies.size())));
                                    all.sendMessage(plugin.prefix + "§7The server you were previously on has been disabled! You have been connected to a lobby");
                                }
                                plugin.getManager().removeServer(name, plugin.getManager().isLobby(name));
                                sendMessage(plugin.prefix + "§7The server " + plugin.getManager().getDisplayName(name) + " §7has been disabled by §4BungeeConsole");
                            } else {
                                sender.sendMessage(plugin.prefix + plugin.getMessages().SERVER_ALREADY_DISABLED);
                            }
                        } else {
                            sender.sendMessage(plugin.prefix + plugin.getMessages().SERVER_NOT_FOUND);
                        }
                    }else if(args[0].equalsIgnoreCase("info")) {
                        String name = args[1];
                        if (plugin.getManager().isInDatabase(name)) {
                            sender.sendMessage(plugin.prefix + "§7Info about§8: " + plugin.getManager().getDisplayName(name));
                            sender.sendMessage("§7Systemname§8:§a " + name);
                            sender.sendMessage(" ");
                            sender.sendMessage("§7Status§8: " + onlineOfflineString(plugin.getManager().isOnline(name)));
                            sender.sendMessage(" ");
                            sender.sendMessage("§7Displayname§8: " + plugin.getManager().getDisplayName(name));
                            sender.sendMessage(" ");
                            sender.sendMessage("§7Enabled§8: " + trueFalseString(plugin.getManager().isActive(name)));
                            sender.sendMessage(" ");
                            sender.sendMessage("§7Lobby§8: " + trueFalseString(plugin.getManager().isActive(name)));
                            sender.sendMessage(" ");
                            sender.sendMessage("§7Restricted§8: " + trueFalseString(plugin.getManager().isRestricted(name)));
                            sender.sendMessage(" ");
                            sender.sendMessage("§7IP§8:§a " + plugin.getManager().getIp(name));
                            sender.sendMessage(" ");
                            sender.sendMessage("§7Port§8:§a " + plugin.getManager().getPort(name));
                            sender.sendMessage(" ");
                            String players = null;
                            if (plugin.getManager().isActive(name) && ProxyServer.getInstance().getServerInfo(name) != null) {
                                ServerInfo serverInfo = ProxyServer.getInstance().getServerInfo(name);
                                for (ProxiedPlayer all : serverInfo.getPlayers()) {
                                    if (players == null) {
                                        players = "§a" + all.getName();
                                    } else {
                                        players = players + "§7, " + all.getName();
                                    }
                                }
                            } else {
                                players = "§cOffline";
                            }
                            sender.sendMessage("§7Players§8: " + players);
                        }else{
                            sender.sendMessage(plugin.prefix + plugin.getMessages().SERVER_NOT_FOUND);
                        }
                    }else if(args[0].equalsIgnoreCase("kick")){
                        String name = args[1];
                        if (plugin.getManager().isInDatabase(name)) {
                            if(plugin.getManager().isActive(name) && ProxyServer.getInstance().getServerInfo(name) != null){
                                for(ProxiedPlayer all : ProxyServer.getInstance().getServerInfo(name).getPlayers()){
                                    if(!all.hasPermission("servermanager.ignorekick")){
                                        all.connect(plugin.lobbies.get(new SecureRandom().nextInt(plugin.lobbies.size())));
                                        all.sendMessage(plugin.prefix + "§7The server you were previously on has been emptied by a staff member");
                                    }
                                }
                                sendMessage(plugin.prefix + "§7The server " + plugin.getManager().getDisplayName(name) + " §7has been emptied by §4BungeeConsole");
                            }else{
                                sender.sendMessage(plugin.prefix + plugin.getMessages().SERVER_NOT_ACTIVE);
                            }
                        }else{
                            sender.sendMessage(plugin.prefix + plugin.getMessages().SERVER_NOT_FOUND);
                        }
                    }else{
                        sendAvailableConsoleCommands(sender);
                    }
                    return;
                }
                if(args.length == 3){
                    if(args[0].equalsIgnoreCase("setlobby")) {
                        String name = args[1];
                        if (plugin.getManager().isInDatabase(name)) {
                            Boolean isLobby;
                            try{
                                isLobby = Boolean.parseBoolean(args[2]);
                            }catch (Exception ex){
                                sender.sendMessage(plugin.prefix + "§7isLobby has to be §atrue §7or §cfalse");
                                return;
                            }
                            if(isLobby){
                                if(!plugin.getManager().isLobby(name)){
                                    plugin.getManager().removeServer(name, plugin.getManager().isLobby(name));
                                    plugin.getManager().setIsLobby(name, true);
                                    plugin.getManager().addServer(name);
                                    sendMessage(plugin.prefix + "§7The server " + plugin.getManager().getDisplayName(name) + " §7has been flagged as a lobby by §4BungeeConsole");
                                }else{
                                    sender.sendMessage(plugin.prefix + "§7This server is already a lobby");
                                }
                            }else{
                                if(plugin.getManager().isLobby(name)){
                                    plugin.getManager().removeServer(name, plugin.getManager().isLobby(name));
                                    plugin.getManager().setIsLobby(name, false);
                                    plugin.getManager().addServer(name);
                                    sendMessage(plugin.prefix + "§7The server " + plugin.getManager().getDisplayName(name) + " §7has been unflagged as a lobby by §4BungeeConsole");
                                }else{
                                    sender.sendMessage(plugin.prefix + "§7This server is not a lobby");
                                }
                            }
                        } else {
                            sender.sendMessage(plugin.prefix + plugin.getMessages().NO_PERMISSION);
                        }
                    }else if(args[0].equalsIgnoreCase("setrestricted")) {
                        String name = args[1];
                        if (plugin.getManager().isInDatabase(name)) {
                            Boolean isrestricted;
                            try{
                                isrestricted = Boolean.parseBoolean(args[2]);
                            }catch (Exception ex){
                                sender.sendMessage(plugin.prefix + "§7isrestricted has to be §atrue §7or §cfalse");
                                return;
                            }
                            if(isrestricted){
                                if(!plugin.getManager().isRestricted(name)){
                                    plugin.getManager().setIsRestricted(name, true);
                                    sendMessage(plugin.prefix + "§7The server " + plugin.getManager().getDisplayName(name) + " §7has been restricted by §4BungeeConsole");
                                }else{
                                    sender.sendMessage(plugin.prefix + "§7This server is already restricted");
                                }
                            }else{
                                if(plugin.getManager().isRestricted(name)){
                                    plugin.getManager().setIsRestricted(name, false);
                                    sendMessage(plugin.prefix + "§7The server " + plugin.getManager().getDisplayName(name) + " §7has been unrestricted by §4BungeeConsole");
                                }else{
                                    sender.sendMessage(plugin.prefix + "§7This server is not restricted");
                                }
                            }
                        } else {
                            sender.sendMessage(plugin.prefix + plugin.getMessages().NO_PERMISSION);
                        }
                    }else{
                        sendAvailableConsoleCommands(sender);
                    }
                    return;
                }
                if(args.length >= 6){
                    if(args[0].equalsIgnoreCase("add")){
                        String name = args[1];
                        String ip = args[2];
                        Integer port;
                        Boolean isLobby;
                        Boolean isEnabled;
                        Boolean isRestricted;

                        try{
                            port = Integer.parseInt(args[3]);
                        }catch(Exception ex){
                            sender.sendMessage(plugin.prefix + "§7The port has to be a number");
                            return;
                        }
                        try{
                            isLobby = Boolean.parseBoolean(args[4]);
                        }catch(Exception ex){
                            sender.sendMessage(plugin.prefix + "§7isLobby has to be §atrue §7or §cfalse");
                            return;
                        }
                        try{
                            isEnabled = Boolean.parseBoolean(args[5]);
                        }catch(Exception ex){
                            sender.sendMessage(plugin.prefix + "§7isActive has to be §atrue §7or §cfalse");
                            return;
                        }
                        try{
                            isRestricted = Boolean.parseBoolean(args[6]);
                        }catch (Exception ex){
                            sender.sendMessage(plugin.prefix + "§7isRestricted has to be §atrue §7or §cfalse");
                            return;
                        }
                        String displayname = "";
                        for(int i = 7; i < args.length; i++) {
                            displayname = displayname + " " + args[i];
                            displayname = ChatColor.translateAlternateColorCodes('&', displayname);
                        }
                        if(!plugin.getManager().isInDatabase(name)){
                            plugin.getManager().createServer(name, ip, port, plugin.prefix, displayname, isLobby, isEnabled, isRestricted, false);
                            sendMessage(plugin.prefix + "§7The server " + plugin.getManager().getDisplayName(name) + " §7has been added by §4BungeeConsole");
                        }else{
                            sender.sendMessage(plugin.prefix + "§7This server is already in the database");
                        }
                    }else{
                        sendAvailableConsoleCommands(sender);
                    }
                    return;
                }else{
                    sendAvailableConsoleCommands(sender);
                }
            }
        });
    }

    private void sendAvailableCommands(ProxiedPlayer p){
        if(p.hasPermission("servermanager.help")){
            p.sendMessage(plugin.prefix + "§7The following commands are available to you§8: §e[optional] <needed>");
            p.sendMessage("§e/servermanager §8- §7Shows this help site");
            p.sendMessage("§e/servermanager add <servername> <ip> <port> <islobby> <isactive> <isrestricted> <displayname> §8- §7Adds a server to your network"); //Done
            p.sendMessage("§e/servermanager delete <servername> §8- §7Deletes a server from your network"); //Done
            p.sendMessage("§e/servermanager reload [servername] §8- §7Reloads the data of a specific server or all servers in the network"); //Done
            p.sendMessage("§e/servermanager list §8- §7Lists all servers in your network"); //Done
            p.sendMessage("§e/servermanager info <servername> §8- §7Shows some information about a specific server"); //Done
            p.sendMessage("§e/servermanager enable <servername> §8- §7Enables a server, so players are able to connect to it"); //Done
            p.sendMessage("§e/servermanager disable <servername> §8- §7Disables a server, so players can't connect to it"); //Done
            p.sendMessage("§e/servermanager setlobby <name> <isLobby> §8- §7You can add a server to the lobby group or remove is"); //Done
            p.sendMessage("§e/servermanager setrestricted <name> <isrestricted> §8- §7You can restrict a server so only people with the permission servermanager.server.<servername> can join on it"); //Done
            p.sendMessage("§e/servermanager kick <servername> §8- §7Kicks all players from the specific server to a random lobby"); //Done
        }else{
            p.sendMessage(plugin.prefix + "§cYou don't have permission to execute this command");
        }
    }

    private void sendAvailableConsoleCommands(CommandSender sender){
        sender.sendMessage(plugin.prefix + "§7The following commands are available to you§8: §e[optional] <needed>");
        sender.sendMessage("§e/servermanager §8- §7Shows this help site");
        sender.sendMessage("§e/servermanager add <servername> <ip> <port> <islobby> <isactive> <isrestricted> <displayname> §8- §7Adds a server to your network"); //Done
        sender.sendMessage("§e/servermanager delete <servername> §8- §7Deletes a server from your network"); //Done
        sender.sendMessage("§e/servermanager reload [servername] §8- §7Reloads the data of a specific server or all servers in the network"); //Done
        sender.sendMessage("§e/servermanager list §8- §7Lists all servers in your network"); //Done
        sender.sendMessage("§e/servermanager info <servername> §8- §7Shows some information about a specific server"); //Done
        sender.sendMessage("§e/servermanager enable <servername> §8- §7Enables a server, so players are able to connect to it"); //Done
        sender.sendMessage("§e/servermanager disable <servername> §8- §7Disables a server, so players can't connect to it"); //Done
        sender.sendMessage("§e/servermanager setlobby <name> <isLobby> §8- §7You can add a server to the lobby group or remove is"); //Done
        sender.sendMessage("§e/servermanager setrestricted <name> <isrestricted> §8- §7You can restrict a server so only people with the permission servermanager.server.<servername> can join on it"); //Done
        sender.sendMessage("§e/servermanager kick <servername> §8- §7Kicks all players from the specific server to a random lobby"); //Done
    }

    private String trueFalseString(Boolean bool){
        if(bool){
            return "§aTrue";
        }else{
            return "§cFalse";
        }
    }
    private String onlineOfflineString(Boolean bool){
        if(bool){
            return "§aOnline";
        }else{
            return "§cOffline";
        }
    }

    public Iterable<String> onTabComplete(CommandSender sender, String[] args){
        if ((args.length > 3) || (args.length == 0)){
            return ImmutableSet.of();
        }
        Set matches = new HashSet();
        String search;
        if (args.length == 2){
            search = args[0].toLowerCase();
            for (String server : ProxyServer.getInstance().getServers().keySet()){
                if (server.toLowerCase().startsWith(search)){
                    matches.add(server);
                }
            }
        }
        return matches;
    }

    private void sendMessage(String message){
        ProxyServer.getInstance().getConsole().sendMessage(message);
        for(ProxiedPlayer all : plugin.receiver){
            all.sendMessage(message);
        }
    }
}
