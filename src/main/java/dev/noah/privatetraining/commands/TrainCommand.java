package dev.noah.privatetraining.commands;

import dev.noah.privatetraining.TrainingBox;
import dev.noah.privatetraining.TrainingBoxManager;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TrainCommand implements CommandExecutor {

    private static final long COOLDOWN_MILLIS = 3000L;
    private final Map<UUID, Long> cooldowns;
    private final TrainingBoxManager trainingBoxManager;

    public TrainCommand(TrainingBoxManager trainingBoxManager) {
        this.trainingBoxManager = trainingBoxManager;
        cooldowns = new HashMap<>();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (!(sender instanceof Player player)) {
            sender.sendMessage("Only players can use this command!");
            return true;
        }

        long currentTime = System.currentTimeMillis();
        long lastUsed = cooldowns.getOrDefault(player.getUniqueId(), 0L);

        if (currentTime - lastUsed < COOLDOWN_MILLIS) {
            long secondsLeft = (COOLDOWN_MILLIS - (currentTime - lastUsed)) / 1000;
            player.sendMessage(ChatColor.RED + "You must wait " + secondsLeft + " more seconds before using this command again.");
            return true;
        }
        cooldowns.put(player.getUniqueId(), currentTime);


        TrainingBox trainingBox = trainingBoxManager.getAvailableTrainingBox();
        trainingBox.resetBox();

        player.teleport(trainingBox.getSpawnLocation());

        player.sendMessage(ChatColor.GREEN + "You have been teleported to a private training box!");

        return true;

    }
}
