package ch.fetz.ServerManager.Utils;

import ch.fetz.ServerManager.ServerManager;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import java.net.InetSocketAddress;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

/**
 * Created by Noah Fetz on 20.05.2016.
 */
public class Manager {
    private final ServerManager plugin;

    public Manager(ServerManager serverManager) {
        this.plugin = serverManager;
    }

    public boolean isInDatabase(String name){

        boolean isInDatabase = false;

        ResultSet rs = plugin.getMySQL().getResult("SELECT * FROM servermanager_servers WHERE systemname = ?", new ArrayList<SQLStatementParameter>() {{add(new SQLStatementParameter(SQLStatementParameterType.STRING, 1, name));}});

        try {
            isInDatabase = rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return isInDatabase;
    }

    /**
     * Adds a server to the database
     *
     * @param name
     * @param ip
     * @param port
     * @param motd
     * @param displayname
     * @param isLobby
     * @param isActive
     * @param isrestricted
     */
    public void createServer(String name, String ip, Integer port, String motd, String displayname, Boolean isLobby, Boolean isActive, Boolean isrestricted, Boolean isonline){
        ResultSet rs = plugin.getMySQL().getResult("SELECT systemname FROM servermanager_servers WHERE systemname = ?", new ArrayList<SQLStatementParameter>() {{add(new SQLStatementParameter(SQLStatementParameterType.STRING, 1, name));}});
        try {
            if(!rs.next()){
                ArrayList<SQLStatementParameter> parameters = new ArrayList<>();
                parameters.add(new SQLStatementParameter(SQLStatementParameterType.STRING, 1, name));
                parameters.add(new SQLStatementParameter(SQLStatementParameterType.STRING, 2, ip));
                parameters.add(new SQLStatementParameter(SQLStatementParameterType.INT, 3, port));
                parameters.add(new SQLStatementParameter(SQLStatementParameterType.STRING, 4, displayname));
                parameters.add(new SQLStatementParameter(SQLStatementParameterType.STRING, 5, motd));
                parameters.add(new SQLStatementParameter(SQLStatementParameterType.BOOL, 6, isActive));
                parameters.add(new SQLStatementParameter(SQLStatementParameterType.BOOL, 7, isLobby));
                parameters.add(new SQLStatementParameter(SQLStatementParameterType.BOOL, 8, isrestricted));
                parameters.add(new SQLStatementParameter(SQLStatementParameterType.BOOL, 9, isonline));

                plugin.getMySQL().update("INSERT INTO servermanager_servers (systemname, ip, port, displayname, motd, isactive, islobby, isrestricted, isonline) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)", parameters);
                if(isActive){
                    addServer(name);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Adds a player to the database
     *
     * @param p
     */
    public void createPlayer(ProxiedPlayer p){
        ResultSet rs = plugin.getMySQL().getResult("SELECT uuid FROM servermanager_players WHERE uuid = ?", new ArrayList<SQLStatementParameter>() {{add(new SQLStatementParameter(SQLStatementParameterType.STRING, 1, p.getUniqueId().toString()));}});
        try {
            if(!rs.next()){
                ArrayList<SQLStatementParameter> parameters = new ArrayList<>();
                parameters.add(new SQLStatementParameter(SQLStatementParameterType.STRING, 1, p.getUniqueId().toString()));
                parameters.add(new SQLStatementParameter(SQLStatementParameterType.STRING, 2, p.getName()));
                parameters.add(new SQLStatementParameter(SQLStatementParameterType.BOOL, 3, false));

                plugin.getMySQL().update("INSERT INTO servermanager_players (uuid, name, notify) VALUES (?, ?, ?)", parameters);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Checks if the current player name is up-to-date
     *
     * @param p
     */
    public void checkPlayerName(ProxiedPlayer p){
        ResultSet rs = plugin.getMySQL().getResult("SELECT * FROM servermanager_players WHERE uuid = ?", new ArrayList<SQLStatementParameter>() {{add(new SQLStatementParameter(SQLStatementParameterType.STRING, 1, p.getUniqueId().toString()));}});
        try {
            while(!rs.next()){
                if(rs.getString("name").equals(p.getName())){
                    ArrayList<SQLStatementParameter> parameters = new ArrayList<>();
                    parameters.add(new SQLStatementParameter(SQLStatementParameterType.STRING, 1, p.getName()));
                    parameters.add(new SQLStatementParameter(SQLStatementParameterType.STRING, 2, p.getUniqueId().toString()));

                    plugin.getMySQL().update("UPDATE servermanager_players SET name = ? WHERE uuid = ?", parameters);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Sets the notification status for a player
     *
     * @param p
     * @param notify
     */
    public void setNotificationStatus(ProxiedPlayer p, boolean notify){
        ArrayList<SQLStatementParameter> parameters = new ArrayList<>();
        parameters.add(new SQLStatementParameter(SQLStatementParameterType.BOOL, 1, notify));
        parameters.add(new SQLStatementParameter(SQLStatementParameterType.STRING, 2, p.getUniqueId().toString()));

        plugin.getMySQL().update("UPDATE servermanager_players SET notify = ? WHERE uuid = ?", parameters);
    }

    /**
     * Returns the notification status of a player
     *
     * @param p
     * @return
     */
    public Boolean getNotificationStatus(ProxiedPlayer p){
        ResultSet rs = plugin.getMySQL().getResult("SELECT * FROM servermanager_players WHERE uuid = ?", new ArrayList<SQLStatementParameter>() {{add(new SQLStatementParameter(SQLStatementParameterType.STRING, 1, p.getUniqueId().toString()));}});
        try {
            while (rs.next()){
                return rs.getBoolean("notify");
            }
        } catch (SQLException e){
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Removes a server from the database
     *
     * @param name
     */
    public void removeServer(String name){
        if(isActive(name)){
            removeServer(name, isLobby(name));
        }
        plugin.getMySQL().update("DELETE FROM servermanager_servers WHERE systemname = ?", new ArrayList<SQLStatementParameter>() {{add(new SQLStatementParameter(SQLStatementParameterType.STRING, 1, name));}});
    }

    /**
     * Returns all servers in an ArrayList
     *
     * @return
     */
    public ArrayList<String> getAllServers(){
        ArrayList<String> servers = new ArrayList<>();
        ResultSet rs = plugin.getMySQL().getResult("SELECT * FROM servermanager_servers", new ArrayList<>());
        try {
            while(rs.next()){
                servers.add(rs.getString("systemname"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return servers;
    }

    /**
     * Gets the ip of a server
     *
     * @param name
     * @return
     */
    public String getIp(String name){
        ResultSet rs = plugin.getMySQL().getResult("SELECT * FROM servermanager_servers WHERE systemname = ?", new ArrayList<SQLStatementParameter>() {{add(new SQLStatementParameter(SQLStatementParameterType.STRING, 1, name));}});
        try {
            while(rs.next()){
                return rs.getString("ip");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Sets the port of a server
     *
     * @param name
     * @param port
     */
    public void setPort(String name, int port){
        ArrayList<SQLStatementParameter> parameters = new ArrayList<>();
        parameters.add(new SQLStatementParameter(SQLStatementParameterType.INT, 1, port));
        parameters.add(new SQLStatementParameter(SQLStatementParameterType.STRING, 2, name));

        plugin.getMySQL().update("UPDATE servermanager_servers SET port = ? WHERE systemname = ?", parameters);
    }

    /**
     * Returns the port of a server
     *
     * @param name
     * @return
     */
    public Integer getPort(String name){
        ResultSet rs = plugin.getMySQL().getResult("SELECT * FROM servermanager_servers WHERE systemname = ?", new ArrayList<SQLStatementParameter>() {{add(new SQLStatementParameter(SQLStatementParameterType.STRING, 1, name));}});
        try {
            while(rs.next()){
                return rs.getInt("port");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Sets the ip of a server
     *
     * @param name
     * @param ip
     */
    public void setIp(String name, String ip){
        ArrayList<SQLStatementParameter> parameters = new ArrayList<>();
        parameters.add(new SQLStatementParameter(SQLStatementParameterType.STRING, 1, ip));
        parameters.add(new SQLStatementParameter(SQLStatementParameterType.STRING, 2, name));

        plugin.getMySQL().update("UPDATE servermanager_servers SET ip = ? WHERE systemname = ?", parameters);
    }

    /**
     * Gets the displayname of a server
     *
     * @param name
     * @return
     */
    public String getDisplayName(String name){
        ResultSet rs = plugin.getMySQL().getResult("SELECT * FROM servermanager_servers WHERE systemname = ?", new ArrayList<SQLStatementParameter>() {{add(new SQLStatementParameter(SQLStatementParameterType.STRING, 1, name));}});
        try {
            while(rs.next()){
                return rs.getString("displayname");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Sets the displayname of a server
     *
     * @param name
     * @param displayname
     */
    public void setDisplayname(String name, String displayname){
        ArrayList<SQLStatementParameter> parameters = new ArrayList<>();
        parameters.add(new SQLStatementParameter(SQLStatementParameterType.STRING, 1, displayname));
        parameters.add(new SQLStatementParameter(SQLStatementParameterType.STRING, 2, name));

        plugin.getMySQL().update("UPDATE servermanager_servers SET displayname = ? WHERE systemname = ?", parameters);
    }

    /**
     * Returns the MOTD of a server
     *
     * @param name
     * @return
     */
    public String getMOTD(String name){
        ResultSet rs = plugin.getMySQL().getResult("SELECT * FROM servermanager_servers WHERE systemname = ?", new ArrayList<SQLStatementParameter>() {{add(new SQLStatementParameter(SQLStatementParameterType.STRING, 1, name));}});
        try {
            while(rs.next()){
                return rs.getString("motd");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Sets the MOTD of a server
     *
     * @param name
     * @param motd
     */
    public void setMOTD(String name, String motd){
        ArrayList<SQLStatementParameter> parameters = new ArrayList<>();
        parameters.add(new SQLStatementParameter(SQLStatementParameterType.STRING, 1, motd));
        parameters.add(new SQLStatementParameter(SQLStatementParameterType.STRING, 2, name));

        plugin.getMySQL().update("UPDATE servermanager_servers SET motd = ? WHERE systemname = ?", parameters);
    }

    /**
     * Returns wheter a server is a lobby or not
     *
     * @param name
     * @return
     */
    public Boolean isLobby(String name){
        ResultSet rs = plugin.getMySQL().getResult("SELECT * FROM servermanager_servers WHERE systemname = ?", new ArrayList<SQLStatementParameter>() {{add(new SQLStatementParameter(SQLStatementParameterType.STRING, 1, name));}});
        try {
            while(rs.next()){
                return rs.getBoolean("islobby");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Returns wheter a server is active or not
     *
     * @param name
     * @return
     */
    public Boolean isActive(String name){
        ResultSet rs = plugin.getMySQL().getResult("SELECT * FROM servermanager_servers WHERE systemname = ?", new ArrayList<SQLStatementParameter>() {{add(new SQLStatementParameter(SQLStatementParameterType.STRING, 1, name));}});
        try {
            while(rs.next()){
                return rs.getBoolean("isactive");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Returns wheter a server is restricted or not
     *
     * @param name
     * @return
     */
    public Boolean isRestricted(String name){
        ResultSet rs = plugin.getMySQL().getResult("SELECT * FROM servermanager_servers WHERE systemname = ?", new ArrayList<SQLStatementParameter>() {{add(new SQLStatementParameter(SQLStatementParameterType.STRING, 1, name));}});
        try {
            while(rs.next()){
                return rs.getBoolean("isrestricted");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Returns the online status from the database
     *
     * @param name
     * @return
     */
    public Boolean isOnline(String name){
        ResultSet rs = plugin.getMySQL().getResult("SELECT * FROM servermanager_servers WHERE systemname = ?", new ArrayList<SQLStatementParameter>() {{add(new SQLStatementParameter(SQLStatementParameterType.STRING, 1, name));}});
        try {
            while(rs.next()){
                return rs.getBoolean("isonline");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Sets the active status of a server
     *
     * @param name
     * @param isactive
     */
    public void setIsActive(String name, Boolean isactive){
        ArrayList<SQLStatementParameter> parameters = new ArrayList<>();
        parameters.add(new SQLStatementParameter(SQLStatementParameterType.BOOL, 1, isactive));
        parameters.add(new SQLStatementParameter(SQLStatementParameterType.STRING, 2, name));

        plugin.getMySQL().update("UPDATE servermanager_servers SET isactive = ? WHERE systemname = ?", parameters);
    }

    /**
     * Sets the lobby status of a server
     *
     * @param name
     * @param lobby
     */
    public void setIsLobby(String name, Boolean lobby){
        ArrayList<SQLStatementParameter> parameters = new ArrayList<>();
        parameters.add(new SQLStatementParameter(SQLStatementParameterType.BOOL, 1, lobby));
        parameters.add(new SQLStatementParameter(SQLStatementParameterType.STRING, 2, name));

        plugin.getMySQL().update("UPDATE servermanager_servers SET islobby = ? WHERE systemname = ?", parameters);
    }

    /**
     * Sets the restriction status of a server
     *
     * @param name
     * @param restricted
     */
    public void setIsRestricted(String name, Boolean restricted){
        ArrayList<SQLStatementParameter> parameters = new ArrayList<>();
        parameters.add(new SQLStatementParameter(SQLStatementParameterType.BOOL, 1, restricted));
        parameters.add(new SQLStatementParameter(SQLStatementParameterType.STRING, 2, name));

        plugin.getMySQL().update("UPDATE servermanager_servers SET isrestricted = ? WHERE systemname = ?", parameters);
    }

    /**
     * Sets the online status in the Database
     *
     * @param name
     * @param isonline
     */
    public void setIsOnline(String name, Boolean isonline){
        ArrayList<SQLStatementParameter> parameters = new ArrayList<>();
        parameters.add(new SQLStatementParameter(SQLStatementParameterType.BOOL, 1, isonline));
        parameters.add(new SQLStatementParameter(SQLStatementParameterType.STRING, 2, name));

        plugin.getMySQL().update("UPDATE servermanager_servers SET isonline = ? WHERE systemname = ?", parameters);
    }

    /**
     * Adds all servers to BungeeCord
     */
    public void addAllServers(){
        for(String name : getAllServers()){
            if(isActive(name)){
                ProxyServer.getInstance().getServers().put(name, ProxyServer.getInstance().constructServerInfo(name, new InetSocketAddress(getIp(name), getPort(name)), getMOTD(name), false));
                if(isLobby(name)) {
                    plugin.lobbies.add(ProxyServer.getInstance().getServerInfo(name));
                }else{
                    plugin.nonlobbies.add(ProxyServer.getInstance().getServerInfo(name));
                }
            }
        }
    }

    /**
     * Adds a server to BungeeCord
     *
     * @param name
     */
    public void addServer(String name){
        ProxyServer.getInstance().getServers().put(name, ProxyServer.getInstance().constructServerInfo(name, new InetSocketAddress(getIp(name), getPort(name)), getMOTD(name), false));
        if(isLobby(name)) {
            plugin.lobbies.add(ProxyServer.getInstance().getServerInfo(name));
        }else{
            plugin.nonlobbies.add(ProxyServer.getInstance().getServerInfo(name));
        }
    }

    /**
     * Removes a server from BungeeCord
     *
     * @param name
     * @param lobby
     */
    public void removeServer(String name, Boolean lobby){
        ProxyServer.getInstance().getServers().remove(name);
        if(lobby) {
            plugin.lobbies.remove(ProxyServer.getInstance().getServerInfo(name));
        }else{
            plugin.nonlobbies.remove(ProxyServer.getInstance().getServerInfo(name));
        }
    }

    /**
     * Removes all servers from BungeeCord
     */
    public void clearAllServers(){
        ProxyServer.getInstance().getServers().clear();
        plugin.lobbies.clear();
        plugin.nonlobbies.clear();
    }
}
