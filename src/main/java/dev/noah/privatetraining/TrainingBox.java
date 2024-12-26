package dev.noah.privatetraining;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.util.BoundingBox;

import java.util.Set;

public class TrainingBox {

    private final Location corner1;
    private final Location corner2;

    public TrainingBox(Location corner1, Location corner2) {
        this.corner1 = corner1;
        this.corner2 = corner2;
    }

    public Location getCorner1() {
        return corner1;
    }

    public Location getCorner2() {
        return corner2;
    }

    public Location getCenter() {
        return new Location(corner1.getWorld(), (corner1.getX() + corner2.getX()) / 2, (corner1.getY() + corner2.getY()) / 2, (corner1.getZ() + corner2.getZ()) / 2);
    }

    public Location getSpawnpoint(){
        //go to the center and then find a safe spot to spawn the player
        Location loc = getCenter();
        while(loc.getY()>Math.min(corner1.getY(),corner2.getY())){
            if(loc.clone().add(0,-2,0).getBlock().getType().isSolid()){
                return loc;
            }
            loc.add(0,-1,0);
        }
        return loc;
    }

    public boolean isInBox(Location location) {
        return location.getWorld().equals(corner1.getWorld()) && BoundingBox.of(corner1, corner2).contains(location.toVector());
    }


    public boolean isEmpty() {
        return corner1.getWorld().getNearbyEntities(BoundingBox.of(corner1, corner2)).stream()
                // Only consider Player entities
                .filter(entity -> entity instanceof Player)
                // If there's any player who lacks the "NPC" metadata -> real player -> return false
                .noneMatch(player -> !player.hasMetadata("NPC"));
    }

    public void resetBox() {
        corner1.getWorld().getNearbyEntities(BoundingBox.of(corner1, corner2)).stream().filter(entity -> !(entity instanceof Player)).filter(player -> !player.hasMetadata("NPC")).forEach(entity -> entity.remove());

        for (int x = corner1.getBlockX(); x <= corner2.getBlockX(); x++) {
            for (int y = corner1.getBlockY(); y <= corner2.getBlockY(); y++) {
                for (int z = corner1.getBlockZ(); z <= corner2.getBlockZ(); z++) {
                    Location loc = new Location(corner1.getWorld(), x, y, z);
                    if (PrivateTraining.trainingBlocks.contains(loc.getBlock().getType())) {
                        loc.getBlock().setType(Material.AIR);
                    }
                }
            }
        }

    }


}
