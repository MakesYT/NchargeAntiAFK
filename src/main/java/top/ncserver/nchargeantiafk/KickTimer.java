package top.ncserver.nchargeantiafk;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.logging.Logger;

public class KickTimer extends BukkitRunnable {
    private final Player player;

    private final Logger logger=NchargeAntiAFK.getPlugin(NchargeAntiAFK.class).getLogger();
    public KickTimer(Player player) {
        this.player = player;

    }
    private boolean passed = false;
    @Override
    public void run() {
        try {
            Thread.sleep(60000);
            if (!passed){
                String name = player.getDisplayName();
                new BukkitRunnable()
                {
                    @Override
                    public void run()
                    {
                        player.kickPlayer("§2[§bNchargeAntiAFK§2]§4验证失败");
                    }
                }.runTask(NchargeAntiAFK.getPlugin(NchargeAntiAFK.class));

                logger.info("§4"+name+"因挂机验证未通过被提出");

                this.cancel();
            }

        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
    public void cancelTimer() {
        passed =true;
        this.cancel();
    }
}
