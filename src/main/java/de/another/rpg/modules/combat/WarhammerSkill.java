package de.another.rpg.modules.combat;

import de.another.rpg.api.skill.Skill;

public class WarhammerSkill implements Skill {
    @Override
    public String getId() {
        return "warhammer";
    }

    @Override
    public String getDisplayName() {
        return "Warhammer";
    }
}
