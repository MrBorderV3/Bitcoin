package me.border.bitcoin.listeners;

import me.border.bitcoin.Bitcoin;
import me.border.bitcoin.utils.BitcoinAPI;
import me.border.bitcoin.utils.Utils;
import org.bukkit.Material;
import org.bukkit.craftbukkit.libs.it.unimi.dsi.fastutil.Hash;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MiningListener implements Listener {

    HashMap<String, Double> blocks = new HashMap<String, Double>();
    HashMap<String, String> rewards = new HashMap<String, String>();
    BitcoinAPI bc = BitcoinAPI.getInstance();
    private Bitcoin plugin;

    public MiningListener(Bitcoin plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onMine(BlockBreakEvent e) {
        Player p = e.getPlayer();
        List<String> bitcoinblocks = Utils.csl("Missions.Mining.blocks");
        List<Double> bitcoinblockchances = plugin.getConfig().getDoubleList("Missions.Mining.chances");
        ArrayList<Material> materials = new ArrayList<Material>();
        for (int i = 0; i < bitcoinblocks.size(); i++) {
            blocks.put(bitcoinblocks.get(i), bitcoinblockchances.get(i));
            rewards.put(bitcoinblocks.get(i), bitcoinblocks.get(i));
            materials.add(Material.valueOf(bitcoinblocks.get(i)));
        }
        if (materials.contains(e.getBlock().getType())) {
            Double rand = random();
            if (rand.equals(blocks.get(e.getBlock().getType().toString()))) {
                int reward = Utils.ci("Missions.Mining." + rewards.get(e.getBlock().getType().toString()) + ".reward");
                if (reward == 0){
                    bc.giveBitcoin(p, plugin, Utils.ci("Missions.Mining.reward"));
                    p.sendMessage(Utils.ucs("Missions.Mining.received").replaceAll("%amount%", String.valueOf(Utils.ci("Missions.Mining.reward"))));
                    return;
                }
                bc.giveBitcoin(p, plugin, reward);
                p.sendMessage(Utils.ucs("Missions.Mining.received").replaceAll("%amount%", String.valueOf(reward)));
            }
        }
    }


    public double random(){
        double ran = Math.random();
        double cumulativeProbability = 0.0;
        List<Double> bitcoinblockchances = plugin.getConfig().getDoubleList("Missions.Mining.chances");
        for (Double chances : bitcoinblockchances) {
            cumulativeProbability += chances/100;
            if (ran <= cumulativeProbability) {
                return chances;
            }
        }
        return 0.0;
    }

}
