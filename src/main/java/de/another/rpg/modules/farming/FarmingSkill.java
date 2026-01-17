package de.another.rpg.modules.farming;

import de.another.rpg.api.skill.Skill;

public class FarmingSkill implements Skill {
    @Override
    public String getId() {
        return "farming";
    }

    @Override
    public String getDisplayName() {
        return "Farming";
    }
}
