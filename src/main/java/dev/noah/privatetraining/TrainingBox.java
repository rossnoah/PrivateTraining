package dev.noah.privatetraining;

import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.BoundingBox;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;


public record TrainingBox(Location corner1, Location corner2) {

    public TrainingBox(Location corner1, Location corner2) {
        this.corner1 = corner1;
        this.corner2 = corner2;

        generateBox();

    }

    public Location getCenter() {
        return new Location(corner1.getWorld(), (corner1.getX() + corner2.getX()) / 2, (corner1.getY() + corner2.getY()) / 2, (corner1.getZ() + corner2.getZ()) / 2);
    }

    public Location getSpawnLocation() {
        //go to the center and then find a safe spot to spawn the player
        Location loc = getCenter();
        while (loc.getY() > Math.min(corner1.getY(), corner2.getY())) {
            if (loc.clone().add(0, -2, 0).getBlock().getType().isSolid()) {
                return loc;
            }
            loc.add(0, -1, 0);
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
                .allMatch(player -> player.hasMetadata("NPC"));
    }

    private void generateBox() {
        //fill a floor at the bottom of the box with grass_block
        int y = corner1().getBlockY();
        for (int x = corner1().getBlockX(); x <= corner2().getBlockX(); x++) {
            for (int z = corner1().getBlockZ(); z <= corner2().getBlockZ(); z++) {
                corner1().getWorld().getBlockAt(x, y, z).setType(Material.GRASS_BLOCK);
            }
        }

        //fill walls with glass
        for (int x = corner1().getBlockX(); x <= corner2().getBlockX(); x++) {
            for (int y1 = corner1().getBlockY() + 1; y1 <= corner1().getBlockY() + 10; y1++) {
                if (!(corner1().getWorld().getBlockAt(x, y1, corner1().getBlockZ()).getType() == Material.TINTED_GLASS))
                    corner1().getWorld().getBlockAt(x, y1, corner1().getBlockZ()).setType(Material.TINTED_GLASS);
                if (!(corner1().getWorld().getBlockAt(x, y1, corner2().getBlockZ()).getType() == Material.TINTED_GLASS))
                    corner1().getWorld().getBlockAt(x, y1, corner2().getBlockZ()).setType(Material.TINTED_GLASS);
            }
        }

        for (int z = corner1().getBlockZ(); z <= corner2().getBlockZ(); z++) {
            for (int y1 = corner1().getBlockY() + 1; y1 <= corner1().getBlockY() + 10; y1++) {

                if (!(corner1().getWorld().getBlockAt(corner1().getBlockX(), y1, z).getType() == Material.TINTED_GLASS))
                    corner1().getWorld().getBlockAt(corner1().getBlockX(), y1, z).setType(Material.TINTED_GLASS);
                if (!(corner1().getWorld().getBlockAt(corner2().getBlockX(), y1, z).getType() == Material.TINTED_GLASS))
                    corner1().getWorld().getBlockAt(corner2().getBlockX(), y1, z).setType(Material.TINTED_GLASS);
            }
        }
    }

    public void resetBox() {
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

        corner1.getWorld().getNearbyEntities(BoundingBox.of(corner1, corner2)).stream().filter(entity -> !(entity instanceof Player)).filter(player -> !player.hasMetadata("NPC")).forEach(Entity::remove);


    }


}
