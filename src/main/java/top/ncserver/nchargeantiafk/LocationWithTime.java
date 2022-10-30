package top.ncserver.nchargeantiafk;

import org.bukkit.Location;

public class LocationWithTime {
    private final Location location;
    private final long time;
    public LocationWithTime(Location location, long time) {
        this.location = location;
        this.time = time;
    }

    public Location getLocation() {
        return location;
    }
    public String getLocationString() {
        return (int) location.getX() +((int)location.getY())+((int)location.getZ())+"";
    }
    public long getTime() {
        return time;
    }
}
