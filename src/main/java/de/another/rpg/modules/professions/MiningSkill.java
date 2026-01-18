package de.another.rpg.modules.professions;

import de.another.rpg.api.skill.Skill;

public class MiningSkill implements Skill {
    @Override
    public String getId() {
        return "mining";
    }

    @Override
    public String getDisplayName() {
        return "Mining";
    }
    @Override
    public String displayItemName() {
        return "Tool_Pickaxe_Iron";
    }
}

