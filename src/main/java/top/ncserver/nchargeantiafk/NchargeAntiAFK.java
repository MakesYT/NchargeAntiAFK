package top.ncserver.nchargeantiafk;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public final class NchargeAntiAFK extends JavaPlugin  implements Listener {
    private final Logger logger=this.getLogger();

    @Override
    public void onEnable() {
        // Plugin startup logic
        logger.info("§aNchargeAntiAFK加载完成");
        for (Player player : Bukkit.getServer().getOnlinePlayers()) {
            CheckingPlayer player1 = new CheckingPlayer(player);
            player1.runTaskAsynchronously(this);

            logger.info("§a对当前在线的玩家"+player.getDisplayName()+"加载NchargeAntiAFK成功");
        }

        Bukkit.getPluginManager().registerEvents(this, this);
    }

    @Override
    public void onDisable() {

        logger.info("§aNchargeAntiAFK已卸载");
        // Plugin shutdown logic
    }
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent playerJoinEvent){
        CheckingPlayer player = new CheckingPlayer(playerJoinEvent.getPlayer());
        player.runTaskAsynchronously(this);

        logger.info("§a对"+playerJoinEvent.getPlayer().getDisplayName()+"加载NchargeAntiAFK成功");
    }
}
