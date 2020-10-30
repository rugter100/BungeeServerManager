package ch.fetz.ServerManager.Utils;

import ch.fetz.ServerManager.ServerManager;
import net.md_5.bungee.api.ProxyServer;

import java.sql.*;
import java.util.ArrayList;

/**
 * Created by Noah Fetz on 20.05.2016.
 */
public class MySQL {
    private final ServerManager plugin;

    public MySQL(ServerManager serverManager) {
        this.plugin = serverManager;
    }

    public void connect(){
        try {
            Connection con = null;
            if(plugin.mysqlDatabase.contains("?")) {
                con = DriverManager.getConnection("jdbc:mysql://" + plugin.mysqlHost + ":" + plugin.mysqlPort + "/" + plugin.mysqlDatabase + "&autoReconnect=true&dontTrackOpenResources=true", plugin.mysqlUser, plugin.mysqlPassword);
            }else{
                con = DriverManager.getConnection("jdbc:mysql://" + plugin.mysqlHost + ":" + plugin.mysqlPort + "/" + plugin.mysqlDatabase + "?autoReconnect=true&dontTrackOpenResources=true", plugin.mysqlUser, plugin.mysqlPassword);
            }

            con.setAutoCommit(false);
            plugin.setCon(con);
            ProxyServer.getInstance().getConsole().sendMessage(plugin.prefix + "§7MySQL §asuccessfully §7connected to the database");
        } catch (Exception ex) {
            ProxyServer.getInstance().getConsole().sendMessage(plugin.prefix + "§cCould not connect to MySQL! Plugin doesn't work! Please PM Noali2000 with the following StackTrace on the SpigotForums, if you belive this is a plugin error!");
            ProxyServer.getInstance().getConsole().sendMessage(plugin.prefix + "§7================================§8[§4COPY STACK TRACE FROM HERE§8]§7================================");
            ex.printStackTrace();
            ProxyServer.getInstance().getConsole().sendMessage(plugin.prefix + "§7================================§8[§4COPY STACK TRACE UP TO HERE§8]§7================================");
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
            ProxyServer.getInstance().getConsole().sendMessage(plugin.prefix + "§cCould not create the MySQL Table!");
        }
        try {
            plugin.getCon().createStatement().executeUpdate("ALTER TABLE servermanager_servers ADD isonline BOOLEAN");
            ProxyServer.getInstance().getConsole().sendMessage(plugin.prefix + "§aUpdated the MySQL Database to the newest plugin version");
        }catch (Exception ex){
        }
    }

    public void update(String qry, ArrayList<SQLStatementParameter> parameters){
        try {
            PreparedStatement ps = this.plugin.getCon().prepareStatement(qry);

            for(SQLStatementParameter parameter : parameters) {
                switch (parameter.type) {
                    case STRING:
                        ps.setString(parameter.index, (String)parameter.value);
                        break;

                    case INT:
                        ps.setInt(parameter.index, (int)parameter.value);
                        break;

                    case DOUBLE:
                        ps.setDouble(parameter.index, (double)parameter.value);
                        break;

                    case BOOL:
                        ps.setBoolean(parameter.index, (boolean)parameter.value);
                        break;
                }
            }

            ps.executeUpdate();
            this.plugin.getCon().commit();
        } catch (SQLException e) {
            e.printStackTrace();
            try {
                this.plugin.getCon().rollback();
            }catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }

    public ResultSet getResult(String qry, ArrayList<SQLStatementParameter> parameters){
        try {
            PreparedStatement ps = this.plugin.getCon().prepareStatement(qry);

            for(SQLStatementParameter parameter : parameters) {
                switch (parameter.type) {
                    case STRING:
                        ps.setString(parameter.index, (String)parameter.value);
                        break;

                    case INT:
                        ps.setInt(parameter.index, (int)parameter.value);
                        break;

                    case DOUBLE:
                        ps.setDouble(parameter.index, (double)parameter.value);
                        break;

                    case BOOL:
                        ps.setBoolean(parameter.index, (boolean)parameter.value);
                        break;
                }
            }

            return ps.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }
}
