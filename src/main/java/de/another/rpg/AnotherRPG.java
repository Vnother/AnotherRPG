package de.another.rpg;

import java.io.File;
import java.nio.file.Path;

import javax.annotation.Nonnull;

import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;

import de.another.rpg.api.component.SkillComponent;
import de.another.rpg.api.registry.SkillRegistry;
import de.another.rpg.config.AnotherRPGConfig;
import de.another.rpg.core.SkillManager;
import de.another.rpg.core.commands.XPCommand;
import de.another.rpg.core.leveling.ExponentialLevelingStrategy;
import de.another.rpg.core.registry.SkillRegistryImpl;
import de.another.rpg.data.JsonPlayerStorage;
import de.another.rpg.data.PlayerStorage;
import de.another.rpg.hytale.BlockBreakEventSystem;
import de.another.rpg.hytale.CombatEventSystem;
import de.another.rpg.hytale.PlayerJoinSystem;
import de.another.rpg.modules.combat.CombatSkill;
import de.another.rpg.modules.farming.FarmingSkill;
import de.another.rpg.modules.mining.MiningSkill;
import de.another.rpg.modules.woodcutting.WoodcuttingSkill;

public class AnotherRPG extends JavaPlugin {

    private static AnotherRPG instance;
    private PlayerStorage playerStorage;

    // Core Systems
    private SkillManager skillManager;
    private SkillRegistry skillRegistry;

    // Config
    // private Config<AnotherRPGConfig> configWrapper;
    private AnotherRPGConfig config;

    public AnotherRPG(@Nonnull JavaPluginInit init) {
        super(init);
        this.config = new AnotherRPGConfig();
        instance = this;
    }

    public static AnotherRPG getInstance() {
        return instance;
    }

    @Override
    protected void setup() {
        super.setup();

        var folder = new File("AnotherRPG");
        if (!folder.exists()) folder.mkdirs();
        
        // Link Component Types
        SkillComponent.setComponentType(
            this.getEntityStoreRegistry().registerComponent(SkillComponent.class, SkillComponent::new)
        );

        // Initialize Config (if needed)
        this.config.syncLoad();

        // Initialize Skill System
        initializeSkillSystem();

        // Register Persistence
        File dataFolder = new File("player_data"); // or get from config/environment
        this.playerStorage = new JsonPlayerStorage(dataFolder);

        // Register Commands
        this.getCommandRegistry().registerCommand(new XPCommand());

        // Register Systems
        this.getEntityStoreRegistry().registerSystem(new BlockBreakEventSystem());
        this.getEntityStoreRegistry().registerSystem(new PlayerJoinSystem());
        this.getEntityStoreRegistry().registerSystem(new CombatEventSystem());

        System.out.println("AnotherRPG Plugin Enabled!");
    }

    private void initializeSkillSystem() {
        // 1. Registry & Strategy
        this.skillRegistry = new SkillRegistryImpl();
        // Max level 99 as per OSRS standard
        var levelingStrategy = new ExponentialLevelingStrategy(99);

        // 2. Manager
        this.skillManager = new SkillManager(skillRegistry, levelingStrategy);

        // 3. Register Modules
        skillRegistry.register(new MiningSkill());
        skillRegistry.register(new WoodcuttingSkill());
        skillRegistry.register(new CombatSkill());
        skillRegistry.register(new FarmingSkill());

        System.out.println("Initialized SkillManager with " + skillRegistry.getAllSkills().size() + " skills.");
    }

    public SkillManager getSkillManager() {
        return skillManager;
    }

    public SkillRegistry getSkillRegistry() {
        return skillRegistry;
    }

    public PlayerStorage getPlayerStorage() {
        return playerStorage;
    }

    public AnotherRPGConfig getConfig() {
        return config;
    }
}
