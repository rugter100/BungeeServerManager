package ch.fetz.ServerManager.Utils;

import ch.fetz.ServerManager.ServerManager;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;

import javax.swing.*;
import java.io.*;
import java.net.URL;
import java.net.URLConnection;

/**
 * Created by Noah Fetz on 10.08.2016.
 */
public class Updater {
    private final ServerManager plugin;

    public Updater(ServerManager serverManager) {
        this.plugin = serverManager;
    }

    public String update = null;

    public void checkForUpdate() {
        update = plugin.getDescription().getVersion();
        try {
            URL url = new URL("https://fetz-gr.ch/BungeeServerManager/version.php");
            String pr = new BufferedReader(new InputStreamReader(url.openStream())).readLine();
            if(pr.equals("SQLError")){
                ProxyServer.getInstance().getConsole().sendMessage(plugin.prefix + "§cThere was an error while checking for an update");
            }else{
                update = pr;
            }
        } catch (Exception e) {
            ProxyServer.getInstance().getConsole().sendMessage(plugin.prefix + "§cCouldn't check for an update");
        }

        Version current = new Version(plugin.getDescription().getVersion());
        Version testing = new Version(update);

        switch (current.compareTo(testing)){
            case 1: ProxyServer.getInstance().getConsole().sendMessage(plugin.prefix + "§aYou're on a beta version!");
                break;
            case 0: ProxyServer.getInstance().getConsole().sendMessage(plugin.prefix + "§aYou're on the latest version!");
                break;
            case -1: ProxyServer.getInstance().getScheduler().runAsync(this.plugin, new Runnable() {
                public void run() {
                    ProxyServer.getInstance().broadcast(plugin.prefix + "§aUpdate found! New Version§8: §ev" + update);
                    ProxyServer.getInstance().broadcast(plugin.prefix + "§aNew version is downloading!");
                    ProxyServer.getInstance().broadcast(plugin.prefix + "§cThe server will restart automaticly when the update is downloaded");

                    try {
                        downloadFile("https://fetz-gr.ch/BungeeServerManager/"+ update + "/ServerManager.jar");
                    } catch (Exception e) {
                        ProxyServer.getInstance().broadcast(plugin.prefix + "§cThe update can't be downloaded.");
                    }
                }
            });
        }
    }

    private void downloadFile(String http) throws Exception {
        URL url = new URL(http);
        URLConnection uc = url.openConnection();
        InputStream is = uc.getInputStream();
        ProgressMonitorInputStream pmis = new ProgressMonitorInputStream(null, "Downloading...", is);
        pmis.getProgressMonitor().setMaximum(uc.getContentLength());

        ProxyServer.getInstance().getScheduler().runAsync(this.plugin, new Runnable() {
            public void run() {
                ProxyServer.getInstance().broadcast(plugin.prefix + "§cThe server is going to restart now!");
                for(ProxiedPlayer all : ProxyServer.getInstance().getPlayers()){
                    all.disconnect("§cRestart due to an update!");
                }
            }
        });

        FileOutputStream out = new FileOutputStream("plugins/ServerManager.jar");

        byte[] buffer = new byte[1024];
        for (int n; (n = pmis.read(buffer)) != -1; out.write(buffer, 0, n));

        pmis.close();
        out.close();

        File jar = new File("plugins/ServerManager.jar");
        String path = jar.getAbsolutePath();
        String oldfile;

        oldfile = path.substring(0, path.length() - 12);
        jar.renameTo(new File(oldfile + "plugins/ServerManager.jar"));
        jar.setExecutable(true);

        ProxyServer.getInstance().stop();
    }
}
