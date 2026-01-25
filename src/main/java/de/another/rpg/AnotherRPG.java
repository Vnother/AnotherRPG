package de.another.rpg;

import java.io.File;

import javax.annotation.Nonnull;

import com.hypixel.hytale.server.core.plugin.JavaPlugin;
import com.hypixel.hytale.server.core.plugin.JavaPluginInit;

import de.another.rpg.api.component.SkillComponent;
import de.another.rpg.api.registry.SkillRegistry;
import de.another.rpg.config.AnotherRPGConfig;
import de.another.rpg.core.SkillManager;
import de.another.rpg.core.commands.RPGCommand;
import de.another.rpg.core.commands.XPCommand;
import de.another.rpg.core.leveling.ExponentialLevelingStrategy;
import de.another.rpg.core.logic.EffectManager;
import de.another.rpg.core.registry.SkillRegistryImpl;
import de.another.rpg.core.skilltree.NodeManager;
import de.another.rpg.core.skilltree.SkillTreeManager;
import de.another.rpg.data.JsonPlayerStorage;
import de.another.rpg.data.PlayerStorage;
import de.another.rpg.hytale.*;
import de.another.rpg.modules.combat.*;
import de.another.rpg.modules.magic.MagicSkill;
import de.another.rpg.modules.professions.*;
import de.another.rpg.modules.professions.mining.DoubleDropEffect;
import de.another.rpg.modules.professions.mining.ShatterStrikeEffect;

public class AnotherRPG extends JavaPlugin {

    private static AnotherRPG instance;
    private PlayerStorage playerStorage;

    // Core Systems
    private SkillManager skillManager;
    private SkillRegistry skillRegistry;
    private NodeManager nodeManager;
    private SkillTreeManager skillTreeManager;
    private EffectManager effectManager;

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
        this.getCommandRegistry().registerCommand(new RPGCommand());

        // Register Systems
        this.getEntityStoreRegistry().registerSystem(new BlockBreakEventSystem());
        this.getEntityStoreRegistry().registerSystem(new PlayerJoinSystem());
        this.getEntityStoreRegistry().registerSystem(new CombatEventSystem());
        this.getEntityStoreRegistry().registerSystem(new PreUseBlockEventSystem());
        //this.getEntityStoreRegistry().registerSystem(new PostUseBlockEventSystem());
        this.getEntityStoreRegistry().registerSystem(new DamageBlockEvent());
        this.getEntityStoreRegistry().registerSystem(new InteractivelyPickupItemEventSystem());
        this.getEntityStoreRegistry().registerSystem(new DropItemEventSystem());

        System.out.println("AnotherRPG Plugin Enabled!");
    }

    private void initializeSkillSystem() {
        // 1. Registry & Strategy
        this.skillRegistry = new SkillRegistryImpl();
        // Max level 99 as per OSRS standard
        var levelingStrategy = new ExponentialLevelingStrategy(99);

        // 2. Manager
        this.skillManager = new SkillManager(skillRegistry, levelingStrategy);
        this.nodeManager = new NodeManager();

        // 3. Register Modules
        skillRegistry.register(new MagicSkill());
        skillRegistry.register(new CombatSkill());
        skillRegistry.register(new ProsperitySkill());

        // 4. Initialize Node Manager
        File skillTreeFolder = new File("AnotherRPG/skilltrees");
        this.nodeManager.loadNodesFromDirectory(skillTreeFolder);

        // 5. Initialize SkillTreeManager
        this.skillTreeManager = new SkillTreeManager(nodeManager);

        // 6. Initialize Effect Manager
        this.effectManager = new EffectManager();
        this.effectManager.registerBlockBreakEffect(new DoubleDropEffect());
        this.effectManager.registerBlockBreakEffect(new ShatterStrikeEffect());

        System.out.println("Initialized SkillManager with " + skillRegistry.getAllSkills().size() + " skills.");
        System.out.println("Loaded skill nodes from " + skillTreeFolder.getAbsolutePath());
    }

    public SkillManager getSkillManager() {
        return skillManager;
    }

    public EffectManager getEffectManager() {
        return effectManager;
    }

    public NodeManager getNodeManager() {
        return nodeManager;
    }

    public SkillTreeManager getSkillTreeManager() {
        return skillTreeManager;
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
