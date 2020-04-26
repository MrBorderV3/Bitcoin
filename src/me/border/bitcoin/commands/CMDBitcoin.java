package me.border.bitcoin.commands;

import me.border.bitcoin.Bitcoin;
import me.border.bitcoin.utils.BitcoinAPI;
import me.border.bitcoin.utils.Utils;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.File;

public class CMDBitcoin implements CommandExecutor {

    private Bitcoin plugin;
    BitcoinAPI bc = BitcoinAPI.getInstance();

    public CMDBitcoin(Bitcoin plugin){
        this.plugin = plugin;

        plugin.getCommand("bitcoin").setExecutor(this);
    }

    public boolean onCommand(final CommandSender sender, final Command cmd, final String label, final String[] args){


        // /btc showing the balance of the player executing it
        if (args.length == 0) {
            if (!(sender instanceof Player)) {
                sender.sendMessage(Utils.ucs("notAPlayer"));
                return true;
            }
            Player p = (Player) sender;
            long balance = bc.getBitcoin(p, plugin);
            p.sendMessage(Utils.chat("&7[&eBTC&7]&6 Balance:&a " + String.valueOf(balance)));
            return true;
        }

        // /btc <Player> showing the balance of the player given in the first argument
        if (args.length == 1){
            OfflinePlayer t = Bukkit.getOfflinePlayer(args[0]);
            long balance = bc.getBitcoin(t, plugin);
            sender.sendMessage(Utils.chat("&7[&eBTC&7] &a%target%'s&6 Balance:&a " + String.valueOf(balance)).replaceAll("%target%", args[0]));
            return true;
        }

        // /btc take <player> <amount> take x amount of bitcoin from player
        if (args.length == 3 && args[0].equalsIgnoreCase("take")) {
            OfflinePlayer t = Bukkit.getOfflinePlayer(args[1]);
            String tuuid = t.getUniqueId().toString();
            File path = new File(plugin.getDataFolder() + File.separator + "/playerdata");
            File targetFile = new File(path, File.separator + tuuid + ".yml");
            if (!path.exists())
                path.mkdirs();
            if (!targetFile.exists() && !t.isOnline()) {
                sender.sendMessage(Utils.ucs("targetNonExistent").replaceAll("%target%", args[1]));
                return true;
            }
            String tName = args[1];
            try {
                long takeamount = Long.parseLong(args[2]);
                bc.takeBitcoin(t, plugin, takeamount);
                long balance = bc.getBitcoin(t, plugin);

                if (t.isOnline()){
                    t.getPlayer().sendMessage(Utils.ucs("Take.bitcoinTaken").replaceAll("%amount%", String.valueOf(takeamount)));
                    t.getPlayer().sendMessage(Utils.chat("&7[&eBTC&7] &6New Balance:&a "+ String.valueOf(balance)));
                }
                sender.sendMessage(Utils.ucs("Take.bitcoinTook").replaceAll("%amount%", args[2]).replaceAll("%target%", tName));
                sender.sendMessage(Utils.chat("&7[&eBTC&7] &a%target%'s &6New Balance:&a " + String.valueOf(balance)).replaceAll("%target%", tName));
                return true;
            } catch (NumberFormatException e){
                sender.sendMessage(Utils.ucs("Take.incorrect-usage"));
                return true;
            }
        }

        // /btc give <player> <amount> give x amount of btc to player
        if (args.length == 3 && args[0].equalsIgnoreCase("give")){
            OfflinePlayer t = Bukkit.getOfflinePlayer(args[1]);
            String tuuid = t.getUniqueId().toString();
            File path = new File(plugin.getDataFolder() + File.separator + "/playerdata");
            File targetFile = new File(path, File.separator + tuuid + ".yml");
            if (!path.exists())
                path.mkdirs();
            if (!targetFile.exists() && !t.isOnline()) {
                sender.sendMessage(Utils.ucs("targetNonExistent").replaceAll("%target%", args[1]));
                return true;
            }
            try {
                long giveamount = Long.parseLong(args[2]);
                bc.giveBitcoin(t, plugin, giveamount);
                long balance = bc.getBitcoin(t, plugin);
                if (t.isOnline()){
                    t.getPlayer().sendMessage(Utils.ucs("Give.bitcoinReceived").replaceAll("%amount%", String.valueOf(giveamount)));
                    t.getPlayer().sendMessage(Utils.chat("&7[&eBTC&7] &6New Balance:&a "+ String.valueOf(balance)));
                }
                sender.sendMessage(Utils.ucs("Give.bitcoinSent").replaceAll("%amount%", args[2]).replaceAll("%target%", args[1]));
                sender.sendMessage(Utils.chat("&7[&eBTC&7] &a%target%'s &6New Balance:&a " + String.valueOf(balance)).replaceAll("%target%", args[1]));
            } catch (NumberFormatException e){
                sender.sendMessage(Utils.ucs("Give.incorrect-usage"));
                return true;
            }
            return true;
        }

        // /btc pay <player> <amount> pay x amount of btc to player from the player's executing it balance
        if (args.length == 3 && args[0].equalsIgnoreCase("pay")){
            if (!(sender instanceof Player)) {
                sender.sendMessage(Utils.ucs("notAPlayer"));
                return true;
            }
            Player p = (Player) sender;
            OfflinePlayer t = Bukkit.getOfflinePlayer(args[1]);
            String tuuid = t.getUniqueId().toString();
            File path = new File(plugin.getDataFolder() + File.separator + "/playerdata");
            File targetFile = new File(path, File.separator + tuuid + ".yml");
            if (t == p) {
                p.sendMessage(Utils.ucs("Pay.cantPayYourself"));
                return true;
            }
            if (!path.exists())
                path.mkdirs();
            if (!t.isOnline()) {
                if (!targetFile.exists()) {
                    p.sendMessage(Utils.ucs("targetNonExistent").replaceAll("%target%", args[1]));
                    return true;
                }
            }
                String pName = p.getName();
                String tName = args[1];
                try {
                    long pbalance = bc.getBitcoin(p, plugin);
                    long payamount = Long.parseLong(args[2]);
                    if (pbalance < payamount){
                        p.sendMessage(Utils.ucs("notEnoughBalance"));
                        return true;
                    }
                    bc.payBitcoin(p, t, plugin, payamount);
                    long tbalance = bc.getBitcoin(t, plugin);
                    pbalance = bc.getBitcoin(p, plugin);
                    if (t.isOnline()) {
                        t.getPlayer().sendMessage(Utils.ucs("Pay.paymentReceived").replaceAll("%by%", pName).replaceAll("%amount%", args[2]));
                        t.getPlayer().sendMessage(Utils.chat("&7[&eBTC&7] &6New Balance:&a " + tbalance));
                    }
                    p.sendMessage(Utils.ucs("Pay.paymentSent").replaceAll("%target%", tName).replaceAll("%amount%", args[2]));
                    p.sendMessage(Utils.chat("&7[&eBTC&7] &6New Balance:&a " + pbalance));
                } catch (NumberFormatException e) {
                    p.sendMessage(Utils.ucs("Pay.incorrect-usage"));
                    return true;
                }
                return true;
        } else {
            sender.sendMessage(Utils.ucs("illegalArguments"));
        }
        return false;
    }
}
