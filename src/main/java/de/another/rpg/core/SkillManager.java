package de.another.rpg.core;

import com.hypixel.hytale.codec.schema.metadata.ui.UIEditor;
import com.hypixel.hytale.protocol.ItemWithAllMetadata;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.Universe;
import com.hypixel.hytale.server.core.util.EventTitleUtil;
import com.hypixel.hytale.server.core.util.NotificationUtil;
import de.another.rpg.AnotherRPG;
import de.another.rpg.api.component.SkillComponent;
import de.another.rpg.api.event.SkillEventListener;
import de.another.rpg.api.event.SkillLevelUpEvent;
import de.another.rpg.api.leveling.LevelingStrategy;
import de.another.rpg.api.registry.SkillRegistry;
import de.another.rpg.config.AnotherRPGConfig;
import de.another.rpg.util.Tools;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Core component handling player skills and XP.
 * Uses Dependency Injection for dependencies.
 */
public class SkillManager {

    private final SkillRegistry skillRegistry;
    private final LevelingStrategy levelingStrategy;
    private final List<SkillEventListener> listeners = new CopyOnWriteArrayList<>();

    // In-Memory Cache: PlayerUID -> SkillComponent
    // In a real app, this would be backed by the Data Layer.
    private final Map<UUID, SkillComponent> playerSkillData = new ConcurrentHashMap<>();

    // In-Memory map for Block Rewards: BlockID -> (SkillID, XP)
    // Map<BlockID, Pair<SkillID, Double>>
    private final Map<String, XpReward> blockRewards = new HashMap<>();

    public SkillManager(SkillRegistry skillRegistry, LevelingStrategy levelingStrategy) {
        this.skillRegistry = skillRegistry;
        this.levelingStrategy = levelingStrategy;

        // Initialize default configuration
        loadConfig();
    }


    private void loadConfig() {
        AnotherRPGConfig.Config config = AnotherRPGConfig.get();
        if (config != null) {
            config.getBlockRewards().forEach((block, reward) -> {
                registerBlockReward(block, reward.getSkill(), reward.getXp());
            });
            System.out.println("Loaded " + blockRewards.size() + " block rewards from config.");
        } else {
            System.out.println("Config not loaded or null!");
        }
    }

    public void registerBlockReward(String blockId, String skillId, double xp) {
        blockRewards.put(blockId, new XpReward(skillId, xp));
    }

    /**
     * Checks if a block reward exists and adds XP to the player.
     *
     * @param playerId UUID of the player.
     * @param blockId  ID of the block.
     */
    public void awardXpForBlock(UUID playerId, String blockId) {
        XpReward reward = null;
        for (Map.Entry<String, XpReward> entry : blockRewards.entrySet()) {
            if (blockId.contains(entry.getKey())) {
                reward = entry.getValue();
                break;
            }
        }
        if (reward == null) {
            return;
        }
        if (reward.skillId.equals("farming")) {
            // Farming XP is only when block is in final stage
            if (!blockId.contains("StageFinal")) {
                return;
            }
        }


        addXp(playerId, reward.skillId(), reward.amount());
    }

    public void addXpCombat(UUID playerId, ItemStack itemStack, double maxHealth) {


        // Simple XP calculation: based on victim's max health
        double baseXp = 1.0; // Example:
        baseXp += maxHealth * 0.25;
        addXp(playerId, "combat", baseXp);

    }

    private record XpReward(String skillId, double amount) {
    }

    public void registerListener(SkillEventListener listener) {
        listeners.add(listener);
    }

    /**
     * Adds XP to a player's skill.
     *
     * @param playerId The player's UUID.
     * @param skillId  The ID of the skill.
     * @param amount   The amount of XP to add.
     */
    public void addXp(UUID playerId, String skillId, double amount) {
        if (skillRegistry.getSkill(skillId).isEmpty()) {
            // Log warning or throw generic error
            System.err.println("Skill not found: " + skillId);
            return;
        }

        playerSkillData.putIfAbsent(playerId, new SkillComponent());
        SkillComponent component = playerSkillData.get(playerId);

        double currentXp = component.getXp(skillId);
        double newXp = currentXp + amount;
        component.setXp(skillId, newXp);

        sendNotification(
                playerId,
                "You gained " + amount + " XP in " + Tools.capitalizeFirstChar(skillId) + ".",
                "Total XP: " + newXp,
                skillRegistry.getSkill(skillId).get().displayItemName());

        // Check for level up
        int oldLevel = levelingStrategy.getLevelFromXp((long) currentXp);
        int newLevel = levelingStrategy.getLevelFromXp((long) newXp);

        if (newLevel > oldLevel) {
            handleLevelUp(playerId, skillId, newLevel);
        }
        AnotherRPG.getInstance().getPlayerStorage().savePlayer(playerId, component);

    }

    public void sendNotification(UUID playerId, String text1, String text2, String displayItemId) {


        var playerRef = Universe.get().getPlayer(playerId);
        var packetHandler = playerRef.getPacketHandler();
        var primaryMessage = Message.raw(text1).color("#00FF00");
        var secondaryMessage = Message.raw(text2).color("#228B22");
        var icon = new ItemStack(displayItemId, 1).toPacket();
        NotificationUtil.sendNotification(
                packetHandler,
                primaryMessage
                );

    }

    public int getLevel(UUID playerId, String skillId) {
        double xp = getXp(playerId, skillId);
        return levelingStrategy.getLevelFromXp((long) xp);
    }

    public double getXp(UUID playerId, String skillId) {
        SkillComponent component = playerSkillData.get(playerId);
        if (component == null) return 0.0;
        return component.getXp(skillId);
    }

    public void loadPlayerData(UUID playerId, SkillComponent data) {
        playerSkillData.put(playerId, data);
    }

    public SkillComponent getPlayerData(UUID playerId) {
        return playerSkillData.getOrDefault(playerId, new SkillComponent());
    }

    private void handleLevelUp(UUID playerId, String skillId, int newLevel) {
        // Fire Event via EventBus (Observer Pattern)
        // For now, simple console out as placeholder
        System.out.println("Player " + playerId + " leveled up " + skillId + " to " + newLevel + "!");

        //Get Player from UUID
        PlayerRef player = Universe.get().getPlayer(playerId);
        if (player == null) {
            return;
        }
        EventTitleUtil.showEventTitleToPlayer(
                player,
                Message.raw(Tools.capitalizeFirstChar(skillId) + " Level Up!"),
                Message.raw("You reached level " + newLevel + "!"),
                true);

        SkillLevelUpEvent event = new SkillLevelUpEvent(playerId, skillId, newLevel, newLevel - 1);
        for (SkillEventListener listener : listeners) {
            try {
                listener.onLevelUp(event);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


}
