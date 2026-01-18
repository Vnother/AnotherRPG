package de.another.rpg.api.component;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.checkerframework.checker.nullness.compatqual.NullableDecl;

import com.hypixel.hytale.component.Component;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

public class SkillComponent implements Component<EntityStore> {

    private Map<String, Double> skillData;
    private Set<String> unlockedNodes;
    private int combatPoints;
    private int professionPoints;

    // Runtime cache for active stats (not serialized)
    private transient Map<String, Double> cachedStats;

    public SkillComponent() {
        this.skillData = new HashMap<>();
        this.unlockedNodes = new HashSet<>();
        this.cachedStats = new HashMap<>();
        this.combatPoints = 0;
        this.professionPoints = 0;
    }

    public Map<String, Double> getSkillData() {
        return skillData;
    }

    public void setSkillData(Map<String, Double> skillData) {
        this.skillData = skillData;
    }
    
    public double getXp(String skillId) {
        return skillData.getOrDefault(skillId, 0.0);
    }
    
    public void setXp(String skillId, double xp) {
        skillData.put(skillId, xp);
    }

    public Set<String> getUnlockedNodes() {
        if (unlockedNodes == null) unlockedNodes = new HashSet<>();
        return unlockedNodes;
    }

    public void addUnlockedNode(String nodeId) {
        if (unlockedNodes == null) unlockedNodes = new HashSet<>();
        unlockedNodes.add(nodeId);
    }

    public boolean hasNodeUnlocked(String nodeId) {
        return unlockedNodes != null && unlockedNodes.contains(nodeId);
    }

    public int getCombatPoints() {
        return combatPoints;
    }

    public void setCombatPoints(int combatPoints) {
        this.combatPoints = combatPoints;
    }

    public void addCombatPoints(int amount) {
        this.combatPoints += amount;
    }

    public int getProfessionPoints() {
        return professionPoints;
    }

    public void setProfessionPoints(int professionPoints) {
        this.professionPoints = professionPoints;
    }

    public void addProfessionPoints(int amount) {
        this.professionPoints += amount;
    }

    public Map<String, Double> getCachedStats() {
        if (cachedStats == null) cachedStats = new HashMap<>();
        return cachedStats;
    }

    public void setCachedStats(Map<String, Double> stats) {
        this.cachedStats = stats;
    }

    public double getStat(String statName) {
        if (cachedStats == null) return 0.0;
        return cachedStats.getOrDefault(statName, 0.0);
    }

    private static ComponentType<EntityStore, SkillComponent> componentType;

    public static void setComponentType(ComponentType<EntityStore, SkillComponent> type) {
        componentType = type;
    }

    //Get Component Type
    public static ComponentType<EntityStore, SkillComponent> getComponentType() {
        return componentType;
    }

    @NullableDecl
    @Override
    public SkillComponent clone() {
        try {
            SkillComponent clone = (SkillComponent) super.clone();
            clone.skillData = new HashMap<>(this.skillData);
            clone.unlockedNodes = new HashSet<>(this.unlockedNodes);
            clone.combatPoints = this.combatPoints;
            clone.professionPoints = this.professionPoints;
            // cachedStats are transient, so new map is fine, but if we want to preserve state:
            clone.cachedStats = new HashMap<>(this.cachedStats);
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }
}
