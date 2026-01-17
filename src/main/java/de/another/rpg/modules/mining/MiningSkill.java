package de.another.rpg.modules.mining;

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
}

