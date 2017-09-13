package p.officertom.shop;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class CommandManager implements CommandExecutor {
    private main plugin;

    public CommandManager(main plugin) {
        this.plugin = plugin;
    }

    public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args) {
        if (cmd.getName().equalsIgnoreCase("sreload")) {
            if (sender.isOp()) {
                plugin.getDataManager().reloadData();
            }
            return true;
        } else if (cmd.getName().equalsIgnoreCase("ssave")) {
            if (sender.isOp()) {
                plugin.getDataManager().saveData();
            }
            return true;
        }

        return false;
    }
}
