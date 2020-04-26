package me.border.bitcoin.listeners;

import me.border.bitcoin.Bitcoin;
import me.border.bitcoin.utils.BitcoinAPI;
import me.border.bitcoin.utils.Utils;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

import java.util.List;

public class PlayerDeathListener implements Listener {

    private Bitcoin plugin;
    BitcoinAPI bc = BitcoinAPI.getInstance();

    public PlayerDeathListener(Bitcoin plugin){
        this.plugin = plugin;
    }

    @EventHandler
    public void onDeath(PlayerDeathEvent e){
        Player dead;
        Player killer;
        try {
            dead = e.getEntity().getPlayer();
            killer = e.getEntity().getKiller().getPlayer();
        } catch (NullPointerException ex){
            return;
        }
        if (dead == killer){
            return;
        }
        List<Double> chances = plugin.getConfig().getDoubleList("Missions.PlayerKills.chances");
        List<Long> rewards = plugin.getConfig().getLongList("Missions.PlayerKills.rewards");
        Double rand = random();
        for (int i = 0; i < chances.size(); i++){
            if (rand.equals(chances.get(i))){
                long reward = rewards.get(i);
                bc.giveBitcoin(killer, plugin, reward);
                killer.sendMessage(Utils.ucs("Missions.PlayerKills.received").replaceAll("%amount%", String.valueOf(reward)).replaceAll("%target%", dead.getName()));
                return;
            }
        }
    }

    public double random(){
        double ran = Math.random();
        double cumulativeProbability = 0.0;
        List<Double> deathchances = plugin.getConfig().getDoubleList("Missions.PlayerKills.chances");
        for (Double chances : deathchances) {
            cumulativeProbability += chances/100;
            if (ran <= cumulativeProbability) {
                return chances;
            }
        }
        return 0.0;
    }
}
