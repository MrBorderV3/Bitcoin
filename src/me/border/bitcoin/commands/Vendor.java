package me.border.bitcoin.commands;

import me.border.bitcoin.Bitcoin;
import me.border.bitcoin.utils.BitcoinAPI;
import me.border.bitcoin.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class Vendor implements CommandExecutor {

    BitcoinAPI bc = BitcoinAPI.getInstance();
    private Bitcoin plugin;

    public Vendor(Bitcoin plugin){
        this.plugin = plugin;

        plugin.getCommand("vendor").setExecutor(this);
    }

    public boolean onCommand(final CommandSender sender,final Command cmd,final String label,final String[] args) {

        if (!(sender instanceof Player)){
            sender.sendMessage(Utils.ucs("notAPlayer"));
            return true;
        }

        Player p = (Player) sender;

        if (args.length == 0) {
            bc.createPlayer(p, plugin);
        } else {
            p.sendMessage(Utils.ucs("Vendor.incorrect-usage"));
        }

        openVendorGUI(p);
        p.sendMessage(Utils.ucs("Vendor.opening"));
        return false;
    }

    public void openVendorGUI(Player p){
        Inventory vendorGUI = Bukkit.createInventory(null, plugin.getConfig().getInt("Vendor.GUI.rows") * 9, Utils.ucs("Vendor.GUI.title"));
        int itemsCount = Utils.ci("Vendor.GUI.amountOfItems");
        ArrayList<Integer> slots = new ArrayList<Integer>();
        for (int i = 0; i < Utils.ci("Vendor.GUI.rows") *9; i++){
            slots.add(i);
        }
        ArrayList<ItemStack> itemList = new ArrayList<ItemStack>();
        for (int i = 0; i < itemsCount; i++){
            itemList.add(createItem(Utils.ucs("Vendor.GUI." + i + ".displayname"), Material.valueOf(Utils.cs("Vendor.GUI." + i + ".material")), Utils.csl("Vendor.GUI." + i + ".lore")));
        }
        for (int i = 0; i < itemsCount; i++){
            vendorGUI.setItem(Utils.ci("Vendor.GUI." + i + ".slot"), itemList.get(i));
            slots.remove(Utils.ci("Vendor.GUI." + i + ".slot"));
        }
        if (Utils.cb("Vendor.GUI.fillItem.enabled")){
            ItemStack fillItem = createItem(Utils.ucs("Vendor.GUI." + "fillItem" + ".displayname"), Material.valueOf(Utils.cs("Vendor.GUI." + "fillItem" + ".material")), Utils.csl("Vendor.GUI." + "fillItem" + ".lore"));
            for (int i = 0; i < slots.size(); i++){
                vendorGUI.setItem(slots.get(i), fillItem);
            }
        }
        p.openInventory(vendorGUI);
    }

    public ItemStack createItem(String disname, Material material, List<String> itemlore){
        ArrayList<String> lore = new ArrayList<String>();
        for (String output: itemlore){
            lore.add(Utils.chat(output));
        }
        ItemStack item = new ItemStack(material, 1);
        ItemMeta itemm = item.getItemMeta();
        itemm.setDisplayName(Utils.chat(disname));
        itemm.setLore(lore);
        item.setItemMeta(itemm);
        return item;
    }
}
