package top.ncserver.nchargeantiafk;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
public class verifyEvent extends Event  {
    private static final HandlerList handlers = new HandlerList();
    private String playerName;
    public verifyEvent(String name) {
        playerName=name;
    }

    public String getPlayerName() {
        return playerName;
    }

    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
