package ch.fetz.ServerManager.spigot.Listener;

import ch.fetz.ServerManager.spigot.SpigotServerManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

/**
 * Created by Noah Fetz on 05.12.2016.
 */
public class InventoryClickEvent_Listener implements Listener {
    private final SpigotServerManager plugin;

    public InventoryClickEvent_Listener(SpigotServerManager spigotServerManager) {
        this.plugin = spigotServerManager;
        plugin.getServer().getPluginManager().registerEvents(this, spigotServerManager);
    }

    @EventHandler
    public void onClick(InventoryClickEvent e){
        try{
            if(e.getView().getTitle().contains("§aServers §8(§7Page §e")){
                e.setCancelled(true);
                if(e.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase("§7Next Page")){
                    int page = Integer.parseInt(e.getView().getTitle().replace("§aServers §8(§7Page §e", "").replace("§8)", ""));
                    e.getWhoClicked().closeInventory();
                    plugin.getMenuManager().openMainMenu((Player) e.getWhoClicked(), page+1);
                }
                if(e.getCurrentItem().getItemMeta().getDisplayName().equalsIgnoreCase("§7Previous Page")){
                    int page = Integer.parseInt(e.getView().getTitle().replace("§aServers §8(§7Page §e", "").replace("§8)", ""));
                    e.getWhoClicked().closeInventory();
                    plugin.getMenuManager().openMainMenu((Player) e.getWhoClicked(), page-1);
                }
            }else{
                e.setCancelled(false);
            }
        }catch (Exception ex){
        }
    }
}
