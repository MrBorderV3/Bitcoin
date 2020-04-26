package me.border.bitcoin.utils;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;

public class BitcoinAPI {
    public static BitcoinAPI instance = new BitcoinAPI();

    public static BitcoinAPI getInstance() {
        return instance;
    }

    public FileConfiguration setupPlayer(Player p){
        String uuid = p.getUniqueId().toString();
        File path = new File("plugins/StaffTools" + File.separator + "/playerdata");
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
                save(data, playerFile);
            } catch (IOException e) {
                Bukkit.getLogger().severe(ChatColor.RED + "Player file creation failed!");
                e.printStackTrace();
            }
        }
        data = YamlConfiguration.loadConfiguration(playerFile);

        return data;
    }

    public void createPlayer(Player p, Plugin plugin){
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
                save(data, playerFile);
            } catch (IOException e) {
                Bukkit.getLogger().severe(ChatColor.RED + "Player file creation failed!");
                e.printStackTrace();
            }
        }
    }

    public void createPlayer(OfflinePlayer t, Plugin plugin){
        String uuid = t.getUniqueId().toString();
        File path = new File(plugin.getDataFolder() + File.separator + "/playerdata");
        File playerFile = new File(path, File.separator + uuid + ".yml");
        FileConfiguration data;
        if (!path.exists())
            path.mkdirs();
        if (!playerFile.exists() && !t.isOnline()) {
            return;
        }
        if (!playerFile.exists()) {
            try {
                playerFile.createNewFile();
                data = YamlConfiguration.loadConfiguration(playerFile);
                data.set("Username", t.getName());
                data.set("Balance", 0);
                save(data, playerFile);
            } catch (IOException e) {
                Bukkit.getLogger().severe(ChatColor.RED + "Player file creation failed!");
                e.printStackTrace();
            }
        }
    }

    public FileConfiguration setupPlayer(OfflinePlayer t, Plugin plugin){
        String uuid = t.getUniqueId().toString();
        File path = new File(plugin.getDataFolder() + File.separator + "/playerdata");
        File playerFile = new File(path, File.separator + uuid + ".yml");
        FileConfiguration data;
        if (!path.exists())
            path.mkdirs();
        if (!playerFile.exists() && !t.isOnline()) {
            return null;
        }
        if (!playerFile.exists()) {
            try {
                playerFile.createNewFile();
                data = YamlConfiguration.loadConfiguration(playerFile);
                data.set("Username", t.getName());
                data.set("Balance", 0);
                save(data, playerFile);
            } catch (IOException e) {
                Bukkit.getLogger().severe(ChatColor.RED + "Player file creation failed!");
                e.printStackTrace();
            }
        }
        data = YamlConfiguration.loadConfiguration(playerFile);

        return data;
    }

    public long getBitcoin(Player p, Plugin plugin) {
        FileConfiguration data = setupPlayer(p, plugin);
        long balance = data.getLong("Balance");
        return balance;
    }

    public long getBitcoin(OfflinePlayer t, Plugin plugin) {
        FileConfiguration data = setupPlayer(t, plugin);
        if (data == null){
            return 0;
        }
        long balance = data.getLong("Balance");
        return balance;
    }

    public void takeBitcoin(OfflinePlayer t, Plugin plugin, long takeamount) {
        String tuuid = t.getUniqueId().toString();
        File path = new File(plugin.getDataFolder() + File.separator + "/playerdata");
        File targetFile = new File(path, File.separator + tuuid + ".yml");
        FileConfiguration data = setupPlayer(t, plugin);
        if (data == null){
            return;
        }
        long tbalance = data.getLong("Balance");
        try {
            long newtbalance = tbalance - takeamount;
            data.set("Balance", newtbalance);
            save(data, targetFile);
        } catch (IOException e) {
            Bukkit.getLogger().severe(ChatColor.RED + "Payment failed!");
            e.printStackTrace();
            return;
        }
        return;
    }

    public void giveBitcoin(OfflinePlayer t, Plugin plugin, long giveamount) {
        String tuuid = t.getUniqueId().toString();
        File path = new File(plugin.getDataFolder() + File.separator + "/playerdata");
        File targetFile = new File(path, File.separator + tuuid + ".yml");
        FileConfiguration data = setupPlayer(t, plugin);
        if (data == null){
            return;
        }
        long tbalance = data.getLong("Balance");
        try {
            long newtbalance = tbalance + giveamount;
            data.set("Balance", newtbalance);
            save(data, targetFile);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        return;
    }

    public void payBitcoin(Player p, OfflinePlayer t, Plugin plugin, long payamount) {
        String puuid = p.getUniqueId().toString();
        String tuuid = t.getUniqueId().toString();
        File path = new File(plugin.getDataFolder() + File.separator + "/playerdata");
        File playerFile = new File(path, File.separator + puuid + ".yml");
        File targetFile = new File(path, File.separator + tuuid + ".yml");
        FileConfiguration tdata = setupPlayer(t, plugin);
        FileConfiguration pdata = setupPlayer(p, plugin);
        if (tdata == null){
            return;
        }
        long pbalance = getBitcoin(p, plugin);
        long tbalance = getBitcoin(t, plugin);
        try {
            long newtbalance = tbalance + payamount;
            tdata.set("Balance", newtbalance);
            save(tdata, targetFile);
            long newpbalance = pbalance - payamount;
            pdata.set("Balance", newpbalance);
            save(pdata, playerFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void save(FileConfiguration data,File file) throws IOException {
        data.save(file);
    }
}
