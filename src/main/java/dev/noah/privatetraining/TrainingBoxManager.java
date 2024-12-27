package dev.noah.privatetraining;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.HashMap;

public class TrainingBoxManager implements Listener {

    private final ArrayList<TrainingBox> trainingBoxes;
    private final HashMap<Player, TrainingBox> respawnMap;
    private final World trainingWorld;
    private final World spawnWorld;
    Plugin plugin;

    public TrainingBoxManager(Plugin plugin, World trainingWorld, World spawnWorld) {
        this.plugin = plugin;
        this.trainingWorld = trainingWorld;
        this.spawnWorld = spawnWorld;
        trainingBoxes = new ArrayList<>();
        respawnMap = new HashMap<>();

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
            trainingBox = new TrainingBox(trainingWorld.getSpawnLocation().clone().add(-20, 10, -20), trainingWorld.getSpawnLocation().clone().add(20, 30, 20));
        } else {
            int gridSpacing = 256; // Spacing between boxes in the grid
            int gridSize = 5; // Number of boxes per row in the grid

            int boxIndex = trainingBoxes.size();
            int row = boxIndex / gridSize;
            int col = boxIndex % gridSize;

            // Calculate the new positions based on the grid
            Location lastCorner1 = trainingBoxes.get(0).corner1(); // Use the first box as a reference
            Location lastCorner2 = trainingBoxes.get(0).corner2();

            Location newCorner1 = lastCorner1.clone().add(col * gridSpacing, 0, row * gridSpacing);
            Location newCorner2 = lastCorner2.clone().add(col * gridSpacing, 0, row * gridSpacing);

            trainingBox = new TrainingBox(newCorner1, newCorner2);
        }

        trainingBoxes.add(trainingBox);


        trainingBox.resetBox();


        return trainingBox;
    }

    public void resetAllBoxes() {
        for (TrainingBox trainingBox : trainingBoxes) {
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
                respawnMap.put(player, trainingBox);
                return;
            }
        }
    }

    @EventHandler
    public void playerSpawnEvent(PlayerRespawnEvent event) {
        if (respawnMap.containsKey(event.getPlayer())) {
            event.setRespawnLocation(respawnMap.get(event.getPlayer()).getSpawnLocation());
            respawnMap.remove(event.getPlayer());
        }
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        for (TrainingBox trainingBox : trainingBoxes) {
            if (trainingBox.isInBox(player.getLocation())) {
                player.teleport(spawnWorld.getSpawnLocation());
            }
        }
    }

    @EventHandler
    public void onBlockPlace(BlockPlaceEvent event) {

        if (event.getBlock().getWorld() != trainingWorld) {
            return;
        }

        for (TrainingBox trainingBox : trainingBoxes) {
            if (trainingBox.isInBox(event.getBlock().getLocation())) {
                if (!PrivateTraining.trainingBlocks.contains(event.getBlock().getType())) {
                    event.setCancelled(true);
                    return;
                }
                return;
            }
        }
    }


}
