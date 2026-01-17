package de.another.rpg.modules.combat;

import de.another.rpg.api.skill.Skill;

public class CombatSkill implements Skill {
    @Override
    public String getId() {
        return "combat";
    }

    @Override
    public String getDisplayName() {
        return "Combat";
    }
}

