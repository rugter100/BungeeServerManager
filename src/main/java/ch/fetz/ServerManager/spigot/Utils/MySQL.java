package ch.fetz.ServerManager.spigot.Utils;

import ch.fetz.ServerManager.spigot.SpigotServerManager;

import java.sql.*;

/**
 * Created by Noah Fetz on 04.12.2016.
 */
public class MySQL {
    private final SpigotServerManager plugin;

    public MySQL(SpigotServerManager spigotServerManager) {
        this.plugin = spigotServerManager;
    }

    public void connect(){
        try {
            Connection con = DriverManager.getConnection("jdbc:mysql://" + plugin.mysqlHost + ":" + plugin.mysqlPort + "/" + plugin.mysqlDatabase + "?autoReconnect=true" , plugin.mysqlUser, plugin.mysqlPassword);
            plugin.setCon(con);
            plugin.getServer().getConsoleSender().sendMessage(plugin.prefix + "§7MySQL §asuccessfully §7connected to the database");
        } catch (Exception ex) {
            plugin.getServer().getConsoleSender().sendMessage(plugin.prefix + "§cCould not connect to MySQL! Plugin doesn't work! Please PM Noali2000 with the following StackTrace on the SpigotForums, if you belive this is a plugin error!");
            plugin.getServer().getConsoleSender().sendMessage(plugin.prefix + "§7================================§8[§4COPY STACK TRACE FROM HERE§8]§7================================");
            ex.printStackTrace();
            plugin.getServer().getConsoleSender().sendMessage(plugin.prefix + "§7================================§8[§4COPY STACK TRACE UP TO HERE§8]§7================================");
        }
    }

    public void close(){
        try {
            plugin.getCon().close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    public boolean isConnected(){
        return plugin.getCon() != null;
    }

    public void createTable(){
        try {
            plugin.getCon().createStatement().executeUpdate("CREATE TABLE IF NOT EXISTS servermanager_servers(id INT NOT NULL AUTO_INCREMENT PRIMARY KEY, systemname TEXT, ip TEXT, port INT, displayname TEXT, motd TEXT, islobby BOOLEAN, isactive BOOLEAN, isrestricted BOOLEAN, isonline BOOLEAN)");
            plugin.getCon().createStatement().executeUpdate("CREATE TABLE IF NOT EXISTS servermanager_players(id INT NOT NULL AUTO_INCREMENT PRIMARY KEY, uuid TEXT, name TEXT, notify BOOLEAN)");
        } catch (Exception ex) {
            plugin.getServer().getConsoleSender().sendMessage(plugin.prefix + "§cCould not create the MySQL Table!");
        }
        try {
            plugin.getCon().createStatement().executeUpdate("ALTER TABLE servermanager_servers ADD isonline BOOLEAN");
            plugin.getServer().getConsoleSender().sendMessage(plugin.prefix + "§aUpdated the MySQL Database to the newest plugin version");
        }catch (Exception ex){
        }
    }

    public void update(String qry){
        try {
            PreparedStatement ps = plugin.getCon().prepareStatement(qry);
            ps.executeUpdate();
        } catch (SQLException e) {
            close();
            connect();
            update(qry);
        }
    }

    public ResultSet getResult(String qry){
        try {
            PreparedStatement ps = plugin.getCon().prepareStatement(qry);
            return ps.executeQuery();
        } catch (SQLException e) {
            close();
            connect();
            return getResult(qry);
        }
    }
}
