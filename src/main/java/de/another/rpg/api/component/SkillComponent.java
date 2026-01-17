package de.another.rpg.api.component;

import java.util.HashMap;
import java.util.Map;

import org.checkerframework.checker.nullness.compatqual.NullableDecl;

import com.hypixel.hytale.component.Component;
import com.hypixel.hytale.component.ComponentType;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;

public class SkillComponent implements Component<EntityStore> {

    private Map<String, Double> skillData;

    public SkillComponent() {
        this.skillData = new HashMap<>();
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
            return clone;
        } catch (CloneNotSupportedException e) {
            throw new RuntimeException(e);
        }
    }
}
