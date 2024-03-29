package top.ncserver.nchargeantiafk;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.player.*;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;

import javax.swing.event.EventListenerList;
import java.util.*;
import java.util.logging.Logger;

import static top.ncserver.nchargeantiafk.NchargeAntiAFK.kickedPlayers;

public class CheckingPlayer extends BukkitRunnable implements Listener {
    private final Player player;
    private final String command;
    private final List<LocationWithTime> locations = new LinkedList<>();
    private final List<Integer> locationsInt = new LinkedList<>();
    private int chatCount = 0;
    private int commandCount = 0;

    private int InteractCount = 0;
    private int maybeAFKTimes=0;

    private int verifyTimer=0;
    private int verifyTimerMax = 600;
    private int passTimer=0;
    private int passTimerMax=3000;
    private final Logger logger=NchargeAntiAFK.getPlugin(NchargeAntiAFK.class).getLogger();
    private boolean kickCheck = false;
    private int int1;
    private int int2;

    private BukkitRunnable bukkitRunnable;

    private final Plugin plugin = NchargeAntiAFK.getPlugin(NchargeAntiAFK.class);

    public CheckingPlayer(Player player,String command){
        this.player = player;
        this.command = command;
        this.passTimerMax=NchargeAntiAFK.config.getInt("passTime");
        this.verifyTimerMax=NchargeAntiAFK.config.getInt("verifyTime");
    }

    @Override
    public void run() {
        //kickTimer=new KickTimer(player,command);
        if (kickedPlayers.contains(player.getName())){
            kickCheck();
        }
        Bukkit.getPluginManager().registerEvents(this, NchargeAntiAFK.getPlugin(NchargeAntiAFK.class));
        player.sendMessage("§2[§bNchargeAntiAFK§2]§a本服务器禁止AFK,您的行为正在被监控");
        bukkitRunnable= new BukkitRunnable() {
            @Override
            public void run() {

                if (!kickCheck){
                    if (passTimer>=passTimerMax){
                       // logger.info(String.valueOf(false));
                        locationsInt.add(LocationToString(player.getLocation()));
                        locations.add(new LocationWithTime(player.getLocation(),System.currentTimeMillis()));
                    }else {
                        passTimer++;
                    }

                    //  kickCheck();
                }else {
                    verifyTimer++;
                    if (verifyTimer>=verifyTimerMax){
                        String name = player.getDisplayName();
                        new BukkitRunnable()
                        {
                            @Override
                            public void run()
                            {
                                if (command.equals("null")) {
                                    if (!kickedPlayers.contains(player.getName()))
                                        kickedPlayers.add(player.getName());
                                    player.kickPlayer("§2[§bNchargeAntiAFK§2]§4验证失败");
                                }
                                else player.performCommand(command.replace("%p", player.getDisplayName()));

                            }
                        }.runTask(NchargeAntiAFK.getPlugin(NchargeAntiAFK.class));
                        logger.info("§4"+name+"挂机验证未通过");
                    }
                }

                if (locations.size()>=3000){

                    int score =0;
                    Collections.sort(locationsInt);
                    List<Integer> diffs = new LinkedList<>();
                    List<Integer> diffsTimes = new LinkedList<>();
                    for (int i = 0; i < locationsInt.size(); i++) {
                        if (!diffs.contains(locationsInt.get(i))){
                            diffs.add(locationsInt.get(i));
                            diffsTimes.add(1);
                        }else {
                            diffsTimes.set(diffs.size()-1,(diffsTimes.get(diffs.size()-1))+1);
                        }
                    }
                    if (diffs.size()<10) {

                        score=score+2;
                    }else
                    if (diffs.size()<30) {
                        score++;
                    }else if (diffs.size()>50) score--;
                    for (int i = 0; i < diffsTimes.size(); i++) {
                        if (diffsTimes.get(i)>2000) {

                            score=score+2;
                        }else
                        if (diffsTimes.get(i)>1500) {
                            score++;
                        }
                    }
                    if (commandCount>=20)score++;
                    else  if (commandCount<=5&&commandCount!=0)score--;


                    if (chatCount<2&&chatCount!=0)score++;
                    else if (chatCount>=5)score--;

                    if (InteractCount<=20)score=score+2;
                    else if (InteractCount>50&&InteractCount<1000)score--;
                    else if (InteractCount>=1500)score++;
                    player.sendMessage("§2[§bNchargeAntiAFK§2]§a检测完成您的AFK指数为"+score);
                    if (score>=3){

                        kickCheck();
                        logger.info("§a玩家"+player.getDisplayName()+"AFK");
                    }else if (score == 2) {
                        maybeAFKTimes++;
                        player.sendTitle("禁止AFK提醒","§2[§bNchargeAntiAFK§2]§a");
                        logger.info("§a玩家"+player.getDisplayName()+"疑似AFK");
                    }else if (score<0){
                        if (maybeAFKTimes==1)
                            maybeAFKTimes--;
                    }
                    if (maybeAFKTimes>=2){
                        kickCheck();
                        maybeAFKTimes=0;
                        logger.info("§a玩家"+player.getDisplayName()+"多次疑似AFK");
                    }



                    ///判断结束
                    logger.info(player.getDisplayName()+"的AFK指数"+score);
                    logger.info(player.getDisplayName()+"的移动数据采样结果");
                    logger.info(Arrays.toString(diffsTimes.toArray()));
                    logger.info(Arrays.toString(diffs.toArray()));
                    logger.info(player.getDisplayName()+"聊天次数:"+chatCount);
                    logger.info(player.getDisplayName()+"执行命令次数:"+commandCount);
                    logger.info(player.getDisplayName()+"交互次数:"+InteractCount);
                    ///数据采样结果
                    locations.clear();
                    chatCount=0;
                    commandCount=0;
                    InteractCount=0;
                    locationsInt.clear();
                }
            }
        };
        bukkitRunnable.runTaskTimer(NchargeAntiAFK.getPlugin(NchargeAntiAFK.class),0,2);

        //注销事件
        //HandlerList.unregisterAll(this);

    }
    public int LocationToString(Location location){
        return Integer.parseInt(Math.abs((int) location.getX())+Math.abs((int)location.getY())+Math.abs((int)location.getZ())+"");
    }
    @EventHandler
    public void onDisableEvent(DisableEvent event) {
        logger.info(player.getDisplayName()+"的监控已移除");
        bukkitRunnable.cancel();
        this.cancel();
    }
    @EventHandler
    public void onVerifyEvent(verifyEvent event) {
        if (event.getPlayerName().equals(player.getDisplayName())){
            kickCheck();
            passTimer=passTimerMax;
        }
    }
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        if (player.getUniqueId().equals(event.getPlayer().getUniqueId())){
            logger.info("§a对"+event.getPlayer().getDisplayName()+"卸载NchargeAntiAFK成功");
            HandlerList.unregisterAll( this);
            bukkitRunnable.cancel();

            this.cancel();

        }
    }
    private int tryTimes = 0;
    @EventHandler
    public void onChat(AsyncPlayerChatEvent event)  {

        if (player.getUniqueId().equals(event.getPlayer().getUniqueId())){
            if (kickCheck){
                event.setCancelled(true);
                if (tryTimes>=2){
                    String name = player.getDisplayName();
                    new BukkitRunnable()
                    {
                        @Override
                        public void run()
                        {
                            if (command.equals("null")) {
                                if (!kickedPlayers.contains(player.getName()))
                                    kickedPlayers.add(player.getName());
                                player.kickPlayer("§2[§bNchargeAntiAFK§2]§4验证失败");
                            }
                            else {
                                player.performCommand(command.replace("%p", player.getDisplayName()));
                               // kickTimer.cancelTimer();
                                kickCheck = false;
                            }
                        }
                    }.runTask(NchargeAntiAFK.getPlugin(NchargeAntiAFK.class));

                    logger.info("§4"+name+"挂机验证多次错误未通过");
                }
                try {
                    if(Integer.parseInt(event.getMessage())==int1+int2) {
                        if (kickedPlayers.contains(player.getName())){
                            if (!kickedPlayers.remove(player.getName())) {
                                for (int i = 0; i < kickedPlayers.size(); i++) {
                                    if (kickedPlayers.get(i).equals(player.getName())){
                                        kickedPlayers.remove(i);
                                        break;
                                    }
                                }
                            }
                            logger.info("§a"+player.getDisplayName()+"移除二次监控");
                        }
                        player.sendMessage("§2[§bNchargeAntiAFK§2]§a验证通过");
                        logger.info("§a"+player.getDisplayName()+"验证通过");
                        new BukkitRunnable()
                        {
                            @Override
                            public void run()
                            {
                                kickCheck = false;
                                tryTimes=0;
                                passTimer=passTimerMax;
                                verifyTimer=0;
                            }
                        }.runTask(NchargeAntiAFK.getPlugin(NchargeAntiAFK.class));

                    }else {
                        player.sendMessage("§2[§bNchargeAntiAFK§2]§4输入结果有误");
                        tryTimes++;
                    }
                } catch (NumberFormatException e) {
                    player.sendMessage("§2[§bNchargeAntiAFK§2]§a输入结果有误");
                    tryTimes++;
                }
            }else chatCount++;

        }

    }
    @EventHandler
    public void onCommand(PlayerCommandPreprocessEvent event){
        if (player.getUniqueId().equals(event.getPlayer().getUniqueId()))
            commandCount++;
    }
    @EventHandler
    public void onInteractEvent(PlayerInteractEvent event){

        if (player.getUniqueId().equals(event.getPlayer().getUniqueId())) {
            InteractCount++;
            if (kickCheck){
                event.setCancelled(true);
                event.getPlayer().sendMessage("§2[§bNchargeAntiAFK§2]§4请完成AFK验证");

            }
        }
    }
    @EventHandler
    public void onPlayerRespawnEvent(PlayerRespawnEvent event){

        if (player.getUniqueId().equals(event.getPlayer().getUniqueId())) {
            if (kickCheck){
                player.sendTitle(verifyTimeTitle,verifyTimeSubtitle);
                player.sendMessage(verifyTimeMsg);
            }
        }
    }
    private String verifyTimeMsg="";
    private String verifyTimeTitle="";
    private String verifyTimeSubtitle="";
    public void kickCheck(){

        kickCheck=true;
         int1 =(int)(1+Math.random()*(10-1+1));
         int2 =(int)(1+Math.random()*(10-1+1));
        try {
            player.getOpenInventory().close();
        } catch (Exception e) {

        }
        logger.info("§a"+player.getDisplayName()+"的AFK验证:"+int1+"+"+int2+"=");
        verifyTimeMsg="§2[§bNchargeAntiAFK§2]§aAFK验证:请在聊天框输入"+int1+"+"+int2+"的正确答案";
        player.sendMessage(verifyTimeMsg);
        verifyTimeTitle="§a"+int1+"+"+int2+"=";
        verifyTimeSubtitle="§2[§bNchargeAntiAFK§2]§aAFK验证:请在聊天框输入正确答案";
        player.sendTitle(verifyTimeTitle,verifyTimeSubtitle);
    }
}
