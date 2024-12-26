package dev.noah.privatetraining.commands;

import dev.noah.privatetraining.TrainingBox;
import dev.noah.privatetraining.TrainingBoxManager;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class TrainCommand implements CommandExecutor {


    private TrainingBoxManager trainingBoxManager;
    public TrainCommand(TrainingBoxManager trainingBoxManager) {
        this.trainingBoxManager = trainingBoxManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if(!(sender instanceof Player player)){
            sender.sendMessage("Only players can use this command!");
            return true;
        }


        TrainingBox trainingBox = trainingBoxManager.getAvailableTrainingBox();
        trainingBox.resetBox();

        player.teleport(trainingBox.getSpawnpoint());

        player.sendMessage("You have been teleported to a training box!");


        return true;

    }
}
