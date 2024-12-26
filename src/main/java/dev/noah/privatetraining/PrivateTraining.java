package dev.noah.privatetraining;

import dev.noah.privatetraining.commands.TrainCommand;
import org.bukkit.Bukkit;
import org.bukkit.GameRule;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Set;

public final class PrivateTraining extends JavaPlugin {

    public static Set<Material> trainingBlocks = Set.of(Material.OBSIDIAN, Material.GLOWSTONE, Material.RESPAWN_ANCHOR, Material.ENDER_CHEST, Material.COBWEB, Material.OAK_PLANKS, Material.COBBLESTONE, Material.STONE, Material.SAND, Material.WATER, Material.LAVA, Material.CACTUS);


    public static World trainingWorld;
    @Override
    public void onEnable() {
        long startTime = System.currentTimeMillis();
        // Plugin startup logic
        this.saveDefaultConfig();

        World world = Bukkit.getWorld(getConfig().getString("training-world"));
        if (world == null) {
            getLogger().severe("Training world not found! Please check your config.yml");
            getServer().getPluginManager().disablePlugin(this);
            return;
        }else {
            trainingWorld = world;
            trainingWorld.setGameRule(GameRule.DO_IMMEDIATE_RESPAWN, true);
        }

        TrainingBoxManager trainingBoxManager = new TrainingBoxManager(this);

        //Create 20 training boxes on startup as a buffer to avoid creating them during runtime
        //They are auto created if more are needed
        for(int i =0;i<20;i++){
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
    }
}
