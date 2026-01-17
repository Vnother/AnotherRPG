package de.another.rpg.config;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.hypixel.hytale.server.core.util.io.BlockingDiskFile;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class AnotherRPGConfig extends BlockingDiskFile {
    private static AnotherRPGConfig instance;

    public AnotherRPGConfig() {
        super(Path.of("AnotherRPG/AnotherRPGConfig.json"));
        instance = this;
    }

    public static Config get() {
        return instance != null ? instance.config : null;
    }

    public Config config;

    @Override
    protected void read(BufferedReader bufferedReader) throws IOException {
        JsonParser.parseReader(bufferedReader).getAsJsonArray().forEach((entry) -> {
            JsonObject object = entry.getAsJsonObject();
            try {
                config = new Config();
                config.setDebugMode(object.get("debugMode").getAsBoolean());
                config.setXpMultiplier(object.get("xpMultiplier").getAsDouble());
                if (object.has("blockRewards")) {
                    JsonObject rewardsObj = object.getAsJsonObject("blockRewards");
                    rewardsObj.entrySet().forEach(rewardEntry -> {
                        String blockId = rewardEntry.getKey();
                        JsonObject rewardData = rewardEntry.getValue().getAsJsonObject();
                        String skill = rewardData.get("skill").getAsString();
                        double xp = rewardData.get("xp").getAsDouble();
                        config.registerBlockReward(blockId, skill, xp);
                    });
                }
                System.out.println("Config read: " + config.toString());
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

    }

    @Override
    protected void write(BufferedWriter bufferedWriter) throws IOException {
        try {
            JsonArray jsonArray = new JsonArray();
            JsonObject object = new JsonObject();
            object.addProperty("debugMode", config.isDebugMode());
            object.addProperty("xpMultiplier", config.getXpMultiplier());
            JsonObject rewardsObj = new JsonObject();
            config.getBlockRewards().forEach((blockId, reward) -> {
                JsonObject rewardData = new JsonObject();
                rewardData.addProperty("skill", reward.getSkill());
                rewardData.addProperty("xp", reward.getXp());
                rewardsObj.add(blockId, rewardData);
            });
            object.add("blockRewards", rewardsObj);
            jsonArray.add(object);
            bufferedWriter.write(jsonArray.toString());
            System.out.println("Config written: " + config.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void create(BufferedWriter bufferedWriter) throws IOException {
        config = new Config();
        config.setDebugMode(false);
        config.setXpMultiplier(1.0);

        // Mining
        config.registerBlockReward("Stone", "mining", 5.0);
        config.registerBlockReward("Ore_Copper", "mining", 15.0);
        config.registerBlockReward("Ore_Iron", "mining", 10.0);
        config.registerBlockReward("Ore_Thorium", "mining", 25.0);
        config.registerBlockReward("Ore_Cobalt", "mining", 50.0);
        config.registerBlockReward("Ore_Mithril", "mining", 50.0);
        config.registerBlockReward("Ore_Onyxium", "mining", 50.0);
        config.registerBlockReward("Ore_Adamantite", "mining", 50.0);

        // Woodcutting
        config.registerBlockReward("Trunk", "woodcutting", 10.0);

        // Farming *Plant_Crop_Wheat_Block_State_Definitions_StageFinal
        config.registerBlockReward("Plant_Crop", "farming", 8.0);

        write(bufferedWriter);
    }

    public static class Config {
        private boolean debugMode;
        private double xpMultiplier;
        private Map<String, BlockReward> blockRewards = new HashMap<>();

        @Override
        public String toString() {
            return "Config{" +
                    "debugMode=" + debugMode +
                    ", xpMultiplier=" + xpMultiplier +
                    ", blockRewards=" + blockRewards +
                    '}';
        }

        public boolean isDebugMode() {
            return debugMode;
        }

        public void setDebugMode(boolean debugMode) {
            this.debugMode = debugMode;
        }

        public double getXpMultiplier() {
            return xpMultiplier;
        }

        public void setXpMultiplier(double xpMultiplier) {
            this.xpMultiplier = xpMultiplier;
        }

        public Map<String, BlockReward> getBlockRewards() {
            return blockRewards;
        }

        public void registerBlockReward(String blockId, String skill, double xp) {
            blockRewards.put(blockId, new BlockReward(skill, xp));
        }
    }

    public static class BlockReward {
        private final String skill;
        private final double xp;

        public BlockReward(String skill, double xp) {
            this.skill = skill;
            this.xp = xp;
        }

        public String getSkill() {
            return skill;
        }

        public double getXp() {
            return xp;
        }

        @Override
        public String toString() {
            return "BlockReward{skill='" + skill + "', xp=" + xp + "}";
        }
    }
}
