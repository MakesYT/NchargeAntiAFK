package top.ncserver.nchargeantiafk;

import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.logging.Logger;

public class KickTimer extends BukkitRunnable {
    private final Player player;
    private final String command;
    private boolean finished = false;
    private final Logger logger=NchargeAntiAFK.getPlugin(NchargeAntiAFK.class).getLogger();
    public KickTimer(Player player,String command) {
        this.player = player;
        this.command=command;
    }
    private boolean passed = false;
    @Override
    public void run() {
        finished=false;
        try {
            Thread.sleep(60000);
            if (!passed){
                String name = player.getDisplayName();
                new BukkitRunnable()
                {
                    @Override
                    public void run()
                    {
                        if (command.equals("null")) {
                            NchargeAntiAFK.kickedPlayers.add(player.getName());
                            player.kickPlayer("§2[§bNchargeAntiAFK§2]§4验证失败");
                        }
                        else player.performCommand(command.replace("%p", player.getDisplayName()));

                    }
                }.runTask(NchargeAntiAFK.getPlugin(NchargeAntiAFK.class));
                finished=true;
                logger.info("§4"+name+"挂机验证未通过");

                this.cancel();
            }

        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
    public void cancelTimer() {
        finished=true;
        passed =true;
        try {
            this.cancel();
        } catch (IllegalStateException ignored) {

        }
    }

    public boolean isFinished() {
        return finished;
    }
}
