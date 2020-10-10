package ch.fetz.ServerManager.Utils;

import ch.fetz.ServerManager.ServerManager;

/**
 * Created by Noah Fetz on 03.08.2016.
 */
public class Messages {
    private final ServerManager plugin;
    public String NO_PERMISSION;
    public String SERVERS_LIST_HEADER;
    public String ALL_SERVER_RELOAD_BROADCAST;
    public String PREVIOUS_SERVER_DELETED_INFO;
    public String SERVER_DELETED_BROADCAST;
    public String SERVER_NOT_FOUND;
    public String SERVER_LIST_INFO;
    public String SERVER_RELOADED_BROADCAST;
    public String SERVER_ENABLED_BROADCAST;
    public String SERVER_DISABLED_BROADCAST;
    public String SERVER_ALREADY_ENABLED;
    public String PREVIOUS_SERVER_DISABLED;
    public String SERVER_ALREADY_DISABLED;
    public String SERVER_NOT_ACTIVE;
    public String PREVIOUS_SERVER_EMPTIED;
    public String GOTO_DESCRIPTION;
    public String GOTO_PLAYER_NOT_ONLINE;
    public String GOTO_CONNECTED;
    public String GOTO_ALREADY_ON_SERVER;
    public String ONLY_INGAME_COMMAND;
    public String LOBBY_ALREADY_ON_LOBBY;
    public String WHEREAMI_SERVER_INFO;
    public String WHEREAMI_CANNOT_FIND_SERVER;

    public Messages(ServerManager serverManager) {
        this.plugin = serverManager;
    }

    public void loadMessages(){
        /*NO_PERMISSION = "§cYou don't have permission to execute this command";
        SERVERS_LIST_HEADER = "§7The following servers are registered in the system§8:";
        ALL_SERVER_RELOAD_BROADCAST = "§7All servers have been reloaded by §4%PLAYER%";
        PREVIOUS_SERVER_DELETED_INFO = "§7The server you were previously on has been disabled! You have been connected to a lobby";
        SERVER_DELETED_BROADCAST = "§7The server %SERVER% §7has been deleted by §4%PLAYER%";
        SERVER_NOT_FOUND = "§7The server could not be found";
        SERVER_LIST_INFO = "§e%POSITION% §8- §7Name§8:§e %NAME% §7Status§8: %ISONLINE% §7Enabled§8: %ISENABLED% §7Lobby§8: %ISLOBBY% §7Restricted§8: %ISRESTRICTED% §7Displayname§8:§e %DISPLAYNAME% §7IP§8:§e %IP% §7Port§8:§e %PORT%";
        SERVER_RELOADED_BROADCAST = "§7The server %DISPLAYNAME% §7has been reloaded by §4%PLAYER%";
        SERVER_ENABLED_BROADCAST = "§7The server %DISPLAYNAME% §7has been enabled by §4%PLAYER%";
        SERVER_ALREADY_ENABLED = "§7This server is already enabled";
        SERVER_DISABLED_BROADCAST = "§7The server %DISPLAYNAME% §7has been disabled by §4%PLAYER%";
        PREVIOUS_SERVER_DISABLED = "§7The server you were previously on has been disabled! You have been connected to a lobby";
        SERVER_ALREADY_DISABLED = "§7This server is already disabled";
        SERVER_NOT_ACTIVE = "§7The server is not active";
        PREVIOUS_SERVER_EMPTIED = "§7The server you were previously on has been emptied by a staff member";
        GOTO_DESCRIPTION = "§e/goto <playername> §8- §7Connects you to the players current server";
        GOTO_PLAYER_NOT_ONLINE = "§cThe player §a%PLAYER% §cis not online";
        GOTO_CONNECTED = "§7You have been connected to §a%PLAYER%'s §7server";
        GOTO_ALREADY_ON_SERVER = "§cYou're already on §a%PLAYER%'s §cserver";
        ONLY_INGAME_COMMAND = "§cOnly ingame available";
        LOBBY_ALREADY_ON_LOBBY = "§cYou are already connected to a lobby";
        WHEREAMI_SERVER_INFO = "§7You're currently on the server§8: §e%SERVER%";
        WHEREAMI_CANNOT_FIND_SERVER = "§cCould not find your current server";*/

        NO_PERMISSION = plugin.config.getString("Language.NO_PERMISSION");
        SERVERS_LIST_HEADER = plugin.config.getString("Language.SERVERS_LIST_HEADER");
        ALL_SERVER_RELOAD_BROADCAST = plugin.config.getString("Language.ALL_SERVER_RELOAD_BROADCAST");
        PREVIOUS_SERVER_DELETED_INFO = plugin.config.getString("Language.PREVIOUS_SERVER_DELETED_INFO");
        SERVER_DELETED_BROADCAST = plugin.config.getString("Language.SERVER_DELETED_BROADCAST");
        SERVER_NOT_FOUND = plugin.config.getString("Language.SERVER_NOT_FOUND");
        SERVER_LIST_INFO = plugin.config.getString("Language.SERVER_LIST_INFO");
        SERVER_RELOADED_BROADCAST = plugin.config.getString("Language.SERVER_RELOADED_BROADCAST");
        SERVER_ENABLED_BROADCAST = plugin.config.getString("Language.SERVER_ENABLED_BROADCAST");
        SERVER_ALREADY_ENABLED = plugin.config.getString("Language.SERVER_ALREADY_ENABLED");
        SERVER_DISABLED_BROADCAST = plugin.config.getString("Language.SERVER_DISABLED_BROADCAST");
        PREVIOUS_SERVER_DISABLED = plugin.config.getString("Language.PREVIOUS_SERVER_DISABLED");
        SERVER_ALREADY_DISABLED = plugin.config.getString("Language.SERVER_ALREADY_DISABLED");
        SERVER_NOT_ACTIVE = plugin.config.getString("Language.SERVER_NOT_ACTIVE");
        PREVIOUS_SERVER_EMPTIED = plugin.config.getString("Language.PREVIOUS_SERVER_EMPTIED");
        GOTO_DESCRIPTION = plugin.config.getString("Language.GOTO_DESCRIPTION");
        GOTO_PLAYER_NOT_ONLINE = plugin.config.getString("Language.GOTO_PLAYER_NOT_ONLINE");
        GOTO_CONNECTED = plugin.config.getString("Language.GOTO_CONNECTED");
        GOTO_ALREADY_ON_SERVER = plugin.config.getString("Language.GOTO_ALREADY_ON_SERVER");
        ONLY_INGAME_COMMAND = plugin.config.getString("Language.ONLY_INGAME_COMMAND");
        LOBBY_ALREADY_ON_LOBBY = plugin.config.getString("Language.LOBBY_ALREADY_ON_LOBBY");
        WHEREAMI_SERVER_INFO = plugin.config.getString("Language.WHEREAMI_SERVER_INFO");
        WHEREAMI_CANNOT_FIND_SERVER = plugin.config.getString("Language.WHEREAMI_CANNOT_FIND_SERVER");
    }
}
