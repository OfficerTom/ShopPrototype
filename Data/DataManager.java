package p.officertom.shop.Data;

import java.io.File;
import java.util.HashMap;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.inventory.ItemStack;
import p.officertom.shop.ShopInstance;
import p.officertom.shop.main;

public class DataManager {
    private HashMap<String, ShopInstance> shopInstances = new HashMap<>();
    private HashMap<String, ProductNode> products = new HashMap<>();
    private main plugin;
    private File shopsFile, productsFile;
    private FileConfiguration shopsConfig, productsConfig;

    private long currentTime; // prevent multiple memory allocations

    public DataManager(main plugin) {
        this.plugin = plugin;
        shopsFile = new File(plugin.getDataFolder(), "shops.yml");
        productsFile = new File(plugin.getDataFolder(), "products.yml");
        shopsConfig = new YamlConfiguration();
        productsConfig = new YamlConfiguration();

        if (!shopsFile.exists()) {
            shopsFile.getParentFile().mkdirs();
            plugin.saveResource("shops.yml", false);
        }

        if (!productsFile.exists()) {
            productsFile.getParentFile().mkdirs();
            plugin.saveResource("products.yml", false);
        }

        try {
            shopsConfig.load(shopsFile);
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            productsConfig.load(productsFile);
        } catch (Exception e) {
            e.printStackTrace();
        }

        loadData();

    }

    public void reloadData() {
        shopsConfig = YamlConfiguration.loadConfiguration(shopsFile);
        productsConfig = YamlConfiguration.loadConfiguration(productsFile);
        loadData();
    }

    public void saveData() {
        ConfigurationSection thisSection;

        for (String thisShopName : shopInstances.keySet()) {
            thisSection = shopsConfig.getConfigurationSection("shops." + thisShopName);
            thisSection.set("next-delivery", shopInstances.get(thisShopName).getNextDelivery());

            thisSection = shopsConfig.getConfigurationSection("shops." + thisShopName + ".products");

            ShopInstance thisShopInstance = shopInstances.get(thisShopName);
            String thisProductName;
            ProductNode thisProductNode;

            for (LocalProductNode localProductNode : thisShopInstance.getInventory()) {
                thisProductName = localProductNode.getConfigName();
                shopsConfig.set("shops." + thisShopName + ".products." + thisProductName + ".balance", localProductNode.getBalance());
                shopsConfig.set("shops." + thisShopName + ".products." + thisProductName + ".minimum", localProductNode.getMinimum());
                shopsConfig.set("shops." + thisShopName + ".products." + thisProductName + ".sold", localProductNode.getSold());
                shopsConfig.set("shops." + thisShopName + ".products." + thisProductName + ".on-order", localProductNode.getOnOrder());
            }
        }
    }

    @SuppressWarnings("deprecation")
    private void loadData() {
        products.clear();
        shopInstances.clear();

        if (!productsConfig.getConfigurationSection("products").equals(null)) {
            ConfigurationSection thisSection;
            for (String thisProductName : productsConfig.getConfigurationSection("products").getKeys(false)) {
                thisSection = productsConfig.getConfigurationSection("products." + thisProductName);
                ProductNode thisProductNode = new LocalProductNode();
                thisProductNode.setConfigName(thisSection.getString("name"));
                thisProductNode.setLore(thisSection.getString("lore"));

                thisProductNode.setItemStack(
                        new ItemStack(
                                Material.getMaterial(thisSection.getInt("itemID")),
                                1,
                                (short) thisSection.getInt("damage")
                        )
                );

                thisProductNode.setCost(thisSection.getDouble("cost"));
                thisProductNode.setPackSize(thisSection.getInt("pack"));

                products.put(thisProductName, thisProductNode);
            }
        }

        if (!shopsConfig.getConfigurationSection("shops").equals(null)) {
            for (String thisShopName : shopsConfig.getConfigurationSection("shops").getKeys(false)) {
                ConfigurationSection thisSection = shopsConfig.getConfigurationSection("shops." + thisShopName);
                ShopInstance thisShopInstance = new ShopInstance();
                thisShopInstance.setName(thisSection.getString("name"));
                thisShopInstance.setProfitMargin(1.0 + (thisSection.getDouble("profit") / 100.0));
                thisShopInstance.setTimes(
                        thisSection.getInt("delivery-delay"),
                        thisSection.getLong("next-delivery"));

                thisSection = thisSection.getConfigurationSection("products");
                for (String thisProductName : thisSection.getKeys(false)) {
                    thisShopInstance.registerItem(
                            products.get(thisProductName),
                            thisSection.getInt(thisProductName + ".balance"),
                            thisSection.getInt(thisProductName + ".minimum"),
                            thisSection.getInt(thisProductName + ".sold"),
                            thisSection.getInt(thisProductName + ".on-order")
                    );
                }
            }
        }

    }

    public void tryUpdateInventories() {
        if (shopInstances.size() > 0) {
            currentTime = System.currentTimeMillis();

            for (ShopInstance thisShopInstance : shopInstances.values()) {
                thisShopInstance.tryUpdate(currentTime);
            }
        }
    }
}
