package top.ncserver.nchargeantiafk;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class DisableEvent extends Event {
    private static final HandlerList handlers = new HandlerList();

    public DisableEvent() {

    }
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
