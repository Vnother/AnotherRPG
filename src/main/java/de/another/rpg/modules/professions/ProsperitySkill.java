package de.another.rpg.modules.professions;

import de.another.rpg.api.skill.Skill;

public class ProsperitySkill implements Skill {
    @Override
    public String getId() {
        return "prosperity";
    }

    @Override
    public String getDisplayName() {
        return "Prosperity";
    }

    @Override
    public String displayItemName() {
        return "Tool_Hatchet_Iron";
    }
}
