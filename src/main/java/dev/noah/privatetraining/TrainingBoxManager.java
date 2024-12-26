package dev.noah.privatetraining;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;

public class TrainingBoxManager implements Listener {

    Plugin plugin;
    private final ArrayList<TrainingBox> trainingBoxes;
    private World trainingWorld;

    public TrainingBoxManager(Plugin plugin, World trainingWorld) {
        this.plugin = plugin;
        this.trainingWorld = trainingWorld;
        trainingBoxes = new ArrayList<>();

    }

    public TrainingBox getAvailableTrainingBox() {
        for (TrainingBox trainingBox : trainingBoxes) { //TODO: Implement sorting of boxes that are not empty to the end to optimize the next lookup
            if (trainingBox.isEmpty()) {
                return trainingBox;
            }
        }


        return createTrainingBox();
    }

    public TrainingBox createTrainingBox() {
        TrainingBox trainingBox;

        if (trainingBoxes.isEmpty()) {
            // Create the first training box at the spawn location.
            trainingBox = new TrainingBox(
                    trainingWorld.getSpawnLocation().clone().add(-20, 10, -20),
                    trainingWorld.getSpawnLocation().clone().add(20, 30, 20)
            );
        } else {
            int gridSpacing = 256; // Spacing between boxes in the grid
            int gridSize = 5; // Number of boxes per row in the grid

            int boxIndex = trainingBoxes.size();
            int row = boxIndex / gridSize;
            int col = boxIndex % gridSize;

            // Calculate the new positions based on the grid
            Location lastCorner1 = trainingBoxes.get(0).getCorner1(); // Use the first box as a reference
            Location lastCorner2 = trainingBoxes.get(0).getCorner2();

            Location newCorner1 = lastCorner1.clone().add(col * gridSpacing, 0, row * gridSpacing);
            Location newCorner2 = lastCorner2.clone().add(col * gridSpacing, 0, row * gridSpacing);

            trainingBox = new TrainingBox(newCorner1, newCorner2);
        }

        trainingBoxes.add(trainingBox);

        //fill a floor at the bottom of the box with grass_block
        int y = trainingBox.getCorner1().getBlockY();
        for (int x = trainingBox.getCorner1().getBlockX(); x <= trainingBox.getCorner2().getBlockX(); x++) {
            for (int z = trainingBox.getCorner1().getBlockZ(); z <= trainingBox.getCorner2().getBlockZ(); z++) {
                trainingBox.getCorner1().getWorld().getBlockAt(x, y, z).setType(Material.GRASS_BLOCK);
            }
        }

        //fill walls with glass
        for (int x = trainingBox.getCorner1().getBlockX(); x <= trainingBox.getCorner2().getBlockX(); x++) {
            for (int y1 = trainingBox.getCorner1().getBlockY() + 1; y1 <= trainingBox.getCorner1().getBlockY() + 10; y1++) {
                if(!(trainingBox.getCorner1().getWorld().getBlockAt(x, y1, trainingBox.getCorner1().getBlockZ()).getType()==Material.TINTED_GLASS))
                trainingBox.getCorner1().getWorld().getBlockAt(x, y1, trainingBox.getCorner1().getBlockZ()).setType(Material.TINTED_GLASS);
                if(!(trainingBox.getCorner1().getWorld().getBlockAt(x, y1, trainingBox.getCorner2().getBlockZ()).getType()==Material.TINTED_GLASS))
                trainingBox.getCorner1().getWorld().getBlockAt(x, y1, trainingBox.getCorner2().getBlockZ()).setType(Material.TINTED_GLASS);
            }
        }

        for (int z = trainingBox.getCorner1().getBlockZ(); z <= trainingBox.getCorner2().getBlockZ(); z++) {
            for (int y1 = trainingBox.getCorner1().getBlockY() + 1; y1 <= trainingBox.getCorner1().getBlockY() + 10; y1++) {

                if(!(trainingBox.getCorner1().getWorld().getBlockAt(trainingBox.getCorner1().getBlockX(), y1, z).getType()==Material.TINTED_GLASS))
                trainingBox.getCorner1().getWorld().getBlockAt(trainingBox.getCorner1().getBlockX(), y1, z).setType(Material.TINTED_GLASS);
                if(!(trainingBox.getCorner1().getWorld().getBlockAt(trainingBox.getCorner2().getBlockX(), y1, z).getType()==Material.TINTED_GLASS))
                trainingBox.getCorner1().getWorld().getBlockAt(trainingBox.getCorner2().getBlockX(), y1, z).setType(Material.TINTED_GLASS);
            }
        }

        trainingBox.resetBox();


        return trainingBox;
    }

    public void resetAllBoxes(){
        for(TrainingBox trainingBox : trainingBoxes){
            trainingBox.resetBox();
        }
    }


    @EventHandler
    public void onDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        if (player.hasMetadata("NPC")) {
            return;
        }

        for (TrainingBox trainingBox : trainingBoxes) {
            if (trainingBox.isInBox(player.getLocation())) {
                player.spigot().respawn();
                player.teleport(trainingBox.getSpawnpoint());
                return;
            }
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {

        if(event.getBlock().getWorld()!=trainingWorld){
            return;
        }

        for (TrainingBox trainingBox : trainingBoxes) {
            if (trainingBox.isInBox(event.getBlock().getLocation())) {
                if(!PrivateTraining.trainingBlocks.contains(event.getBlock().getType())){
                    event.setCancelled(true);
                    return;
                }
                return;
            }
        }
    }



}
