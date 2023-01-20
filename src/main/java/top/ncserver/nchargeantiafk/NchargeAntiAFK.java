package top.ncserver.nchargeantiafk;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public final class NchargeAntiAFK extends JavaPlugin  implements Listener {
    private final Logger logger=this.getLogger();
    private File configFile;
    public static FileConfiguration config;
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
    private static void addConfig(String str,File file) throws IOException {
        FileWriter fstream = new FileWriter(file, true);

        BufferedWriter out = new BufferedWriter(fstream);

        out.write(str);

        out.newLine();

        //close buffer writer

        out.close();
    }
    @Override
    public void onEnable() {
        Metrics metrics = new Metrics(this, 17410);
        Bukkit.getPluginCommand("nchargeantiafk").setExecutor(this);
        this.configFile = new File(this.getDataFolder(), "config.yml");
        // Plugin startup logic
        if (!this.configFile.exists()) {
            this.configFile.getParentFile().mkdirs();
            copyFile(this.getResource("config.yml"), this.configFile);

            this.getLogger().info("File: 已生成 config.yml 文件");
        }
        config = YamlConfiguration.loadConfiguration(this.configFile);
        if (!config.contains("verifyTime")){
            logger.info("添加verifyTime配置");
            try {
                addConfig("verifyTime : 600",configFile);
                config = YamlConfiguration.loadConfiguration(this.configFile);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

        }
        if (!config.contains("passTime")){
            try {
                addConfig("passTime : 3000",configFile);
                config = YamlConfiguration.loadConfiguration(this.configFile);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        this.getLogger().info("当前AFK处理方案"+config.getString("command")+
                ",验证时间:"+(config.getInt("verifyTime")/10)+"秒"+
                ",免检时间:"+(config.getInt("passTime")/10)+"秒");
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
        Bukkit.getServer().getPluginManager().callEvent(new DisableEvent());
        logger.info("§aNchargeAntiAFK已卸载");
        HandlerList.unregisterAll((Plugin) this);
        // Plugin shutdown logic
    }

    @Override
    public boolean onCommand(CommandSender commandSender, org.bukkit.command.Command command, String s, String[] strings) {
        if(!commandSender.hasPermission("nchargeantiafk.command")) {
            commandSender.sendMessage("§c您没有这个命令的权限");
            return true;
        }
        if(strings.length == 0) {
            commandSender.sendMessage("§c你需要指定玩家");
            return true;
        }
        Bukkit.getServer().getPluginManager().callEvent(new verifyEvent(strings[0]));
        return true;
    }
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent playerJoinEvent){
        if (!playerJoinEvent.getPlayer().hasPermission("nchargeantiafk.bypass")) {
            CheckingPlayer player = new CheckingPlayer(playerJoinEvent.getPlayer(),config.getString("command"));
            player.runTaskAsynchronously(this);

            logger.info("§a对" + playerJoinEvent.getPlayer().getDisplayName() + "加载NchargeAntiAFK成功");
        }else logger.info("§a玩家"+playerJoinEvent.getPlayer().getDisplayName()+"拥有免检权限");

    }
}
