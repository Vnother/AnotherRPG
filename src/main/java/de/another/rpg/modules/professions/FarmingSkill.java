package de.another.rpg.modules.professions;

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

    @Override
    public String displayItemName() {
        return "Tool_Hoe_Iron";
    }
}
