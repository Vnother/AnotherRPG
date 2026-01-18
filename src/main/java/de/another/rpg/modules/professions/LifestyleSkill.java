package de.another.rpg.modules.professions;

import de.another.rpg.api.skill.Skill;

public class LifestyleSkill implements Skill {
    @Override
    public String getId() {
        return "lifestyle";
    }

    @Override
    public String getDisplayName() {
        return "Lifestyle";
    }

    @Override
    public String displayItemName() {
        return "Tool_Pickaxe_Iron";
    }
}
