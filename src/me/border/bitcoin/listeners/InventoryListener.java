package me.border.bitcoin.listeners;

import me.border.bitcoin.Bitcoin;
import me.border.bitcoin.utils.BitcoinAPI;
import me.border.bitcoin.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

import java.io.File;
import java.io.IOException;

public class InventoryListener implements Listener {

    private Bitcoin plugin;
    BitcoinAPI bc = BitcoinAPI.getInstance();

    public InventoryListener(Bitcoin plugin){
        this.plugin = plugin;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent e){
        if (e.getView().getTitle().equals(Utils.ucs("Vendor.GUI.title"))){
            Player p = (Player) e.getWhoClicked();
            e.setCancelled(true);
            if (e.getCurrentItem() == null || e.getCurrentItem().getType() == Material.AIR){
                return;
            }
            String uuid = p.getUniqueId().toString();
            File path = new File(plugin.getDataFolder() + File.separator + "/playerdata");
            File playerFile = new File(path, File.separator + uuid + ".yml");
            FileConfiguration data;
            if (!path.exists())
                path.mkdirs();
            if (!playerFile.exists()) {
                try {
                    playerFile.createNewFile();
                    data = YamlConfiguration.loadConfiguration(playerFile);
                    data.set("Username", p.getName());
                    data.set("Balance", 0);
                    bc.save(data, playerFile);
                } catch (IOException ex) {
                    Bukkit.getLogger().severe(ChatColor.RED + "Player file creation failed!");
                    ex.printStackTrace();
                }
            }
            data = YamlConfiguration.loadConfiguration(playerFile);

            long balance = data.getLong("Balance");
            int items = Utils.ci("Vendor.GUI.amountOfItems");
            for (int i = 0; i < items; i++){
                if (e.getSlot() == Utils.ci("Vendor.GUI." + i + ".slot")){
                    long cost = plugin.getConfig().getLong("Vendor.GUI." + i + ".cost");
                    if (balance < cost) {
                        p.sendMessage(Utils.ucs("notEnoughBalance"));
                        return;
                    }
                    Bukkit.dispatchCommand(Bukkit.getConsoleSender(), Utils.cs("Vendor.GUI." + i + ".commandToExecute").replaceAll("%player%", p.getName()));
                    long newbalance = balance - cost;
                    data.set("Balance", newbalance);
                    try {
                        bc.save(data, playerFile);
                    } catch (IOException ex){
                        ex.printStackTrace();
                    }
                    p.sendMessage(Utils.ucs("Vendor.purchase").replaceAll("%cost%", String.valueOf(cost)).replaceAll("%item%", Utils.ucs("Vendor.GUI." + i + ".displayname")));
                    p.sendMessage(Utils.chat("&7[&eBTC&7] &6New Balance:&a " + String.valueOf(newbalance)));
                    p.closeInventory();
                }
            }
        }
    }
}
