package top.ncserver.nchargeantiafk;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public final class NchargeAntiAFK extends JavaPlugin  implements Listener {
    private final Logger logger=this.getLogger();
    private File configFile;
    private YamlConfiguration config;
    public static List<String> kickedPlayers=new ArrayList<String>();
    public static List<CheckingPlayer> checkingPlayers=new ArrayList<CheckingPlayer>();
    public static void copyFile(InputStream inputStream, File file) {
        try {
            FileOutputStream fileOutputStream = new FileOutputStream(file);
            byte[] arrayOfByte = new byte[63];
            int i;
            while ((i = inputStream.read(arrayOfByte)) > 0) {
                fileOutputStream.write(arrayOfByte, 0, i);
            }
            fileOutputStream.close();
            inputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Override
    public void onEnable() {
        this.config = new YamlConfiguration();
        this.configFile = new File(this.getDataFolder(), "config.yml");
        // Plugin startup logic
        if (!this.configFile.exists()) {
            this.configFile.getParentFile().mkdirs();
            copyFile(this.getResource("config.yml"), this.configFile);

            this.getLogger().info("File: 已生成 config.yml 文件");
        }
        this.config = YamlConfiguration.loadConfiguration(this.configFile);
        this.getLogger().info("当前AFK处理方案"+config.getString("command"));
        logger.info("§aNchargeAntiAFK加载完成");
        for (Player player : Bukkit.getServer().getOnlinePlayers()) {
            if (!player.hasPermission("nchargeantiafk.bypass")){
                CheckingPlayer player1 = new CheckingPlayer(player,config.getString("command"));
                player1.runTaskAsynchronously(this);
                logger.info("§a对当前在线的玩家"+player.getDisplayName()+"加载NchargeAntiAFK成功");
            }else logger.info("§a玩家"+player.getDisplayName()+"拥有免检权限");

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
        if (!playerJoinEvent.getPlayer().hasPermission("nchargeantiafk.bypass")) {
            CheckingPlayer player = new CheckingPlayer(playerJoinEvent.getPlayer(),config.getString("command"));
            player.runTaskAsynchronously(this);
            if (kickedPlayers.contains(playerJoinEvent.getPlayer().getName())){
                player.kickCheck();
            }
            logger.info("§a对" + playerJoinEvent.getPlayer().getDisplayName() + "加载NchargeAntiAFK成功");
        }else logger.info("§a玩家"+playerJoinEvent.getPlayer().getDisplayName()+"拥有免检权限");

    }
}
