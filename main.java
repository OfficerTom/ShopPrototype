package p.officertom.shop;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import p.officertom.shop.Data.DataManager;

public class main extends JavaPlugin {

    private DataManager dataManager;
    private CommandManager commandManager;

    private String[] commands = {"sreload", "ssave"};

    public void onEnable() {
        registerManagers();
        registerCommands();

        updateRunnable();
    }

    public void onDisable() {
        dataManager.saveData();
    }

    private void registerManagers() {
        dataManager = new DataManager(this);
        commandManager = new CommandManager(this);
    }

    private void registerCommands() {
        for (String thisCommand : commands)
            getCommand(thisCommand).setExecutor(commandManager);
    }

    public DataManager getDataManager() {
        return dataManager;
    }

    public void updateRunnable() {
        new BukkitRunnable() {

            @Override
            public void run() {
                dataManager.tryUpdateInventories();
            }
        }.runTaskTimer(this, 30000, 360000);
    }

}