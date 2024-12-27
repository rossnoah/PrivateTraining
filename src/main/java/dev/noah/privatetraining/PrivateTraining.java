package dev.noah.privatetraining;

import dev.noah.privatetraining.commands.TrainCommand;
import org.bukkit.Bukkit;
import org.bukkit.GameRule;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashSet;
import java.util.Set;

public final class PrivateTraining extends JavaPlugin {

    public static Set<Material> trainingBlocks;

    private TrainingBoxManager trainingBoxManager;

    @Override
    public void onEnable() {
        long startTime = System.currentTimeMillis();
        // Plugin startup logic
        this.saveDefaultConfig();
        trainingBlocks = new HashSet<>();
        trainingBlocks.addAll(Set.of(Material.OBSIDIAN, Material.GLOWSTONE, Material.RESPAWN_ANCHOR, Material.ENDER_CHEST, Material.COBWEB, Material.OAK_PLANKS, Material.COBBLESTONE, Material.STONE, Material.SAND, Material.WATER, Material.LAVA, Material.CACTUS));
        for (Material m : Material.values()) {
            if (m.name().contains("SHULKER_BOX")) {
                trainingBlocks.add(m);
            }
        }


        World spawnWorld = Bukkit.getWorld(getConfig().getString("spawn-world", "world"));
        if (spawnWorld == null) {
            getLogger().severe("Spawn world not found! Please check your config.yml");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        World trainingWorld = Bukkit.getWorld(getConfig().getString("training-world", "training"));
        if (trainingWorld == null) {
            getLogger().severe("Training world not found! Please check your config.yml");
            getServer().getPluginManager().disablePlugin(this);
            return;
        } else {
            trainingWorld.setGameRule(GameRule.DO_IMMEDIATE_RESPAWN, true);
            trainingWorld.setGameRule(GameRule.RANDOM_TICK_SPEED, 0);
            trainingWorld.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);
            trainingWorld.setGameRule(GameRule.DO_WEATHER_CYCLE, false);
            trainingWorld.setGameRule(GameRule.DO_MOB_SPAWNING, false);
        }

        trainingBoxManager = new TrainingBoxManager(this, trainingWorld, spawnWorld);

        //Create 20 training boxes on startup as a buffer to avoid creating them during runtime
        //They are auto created if more are needed
        for (int i = 0; i < 20; i++) {
            trainingBoxManager.createTrainingBox();
        }


        getServer().getPluginManager().registerEvents(trainingBoxManager, this);
        getCommand("train").setExecutor(new TrainCommand(trainingBoxManager));


        long endTime = System.currentTimeMillis();
        getLogger().info("Plugin enabled in " + (endTime - startTime) + "ms");
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        trainingBoxManager.resetAllBoxes();
    }
}
