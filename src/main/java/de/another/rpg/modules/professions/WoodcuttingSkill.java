package de.another.rpg.modules.professions;

import de.another.rpg.api.skill.Skill;

public class WoodcuttingSkill implements Skill {
    @Override
    public String getId() {
        return "woodcutting";
    }

    @Override
    public String getDisplayName() {
        return "Woodcutting";
    }

    @Override
    public String displayItemName() {
        return "Tool_Hatchet_Iron";
    }
}

