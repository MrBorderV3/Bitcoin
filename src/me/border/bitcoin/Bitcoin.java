package me.border.bitcoin;

import me.border.bitcoin.commands.CMDBitcoin;
import me.border.bitcoin.commands.Vendor;
import me.border.bitcoin.listeners.PlayerDeathListener;
import me.border.bitcoin.listeners.InventoryListener;
import me.border.bitcoin.listeners.MiningListener;
import me.border.bitcoin.utils.Utils;
import org.bukkit.plugin.java.JavaPlugin;

public class Bitcoin extends JavaPlugin {

    @Override
    public void onEnable(){
        saveDefaultConfig();
        getConfig().options().copyDefaults(true);
        new CMDBitcoin(this);
        new Vendor(this);
        new Utils(this);
        getServer().getPluginManager().registerEvents(new InventoryListener(this), this);
        if (Utils.cb("Missions.Mining.enabled"))
            getServer().getPluginManager().registerEvents(new MiningListener(this), this);
        if (Utils.cb("Missions.PlayerKills.enabled"))
            getServer().getPluginManager().registerEvents(new PlayerDeathListener(this), this);
    }
}
