package de.another.rpg.modules.combat;

import de.another.rpg.api.skill.Skill;

public class SwordSkill implements Skill {
    @Override
    public String getId() {
        return "sword";
    }

    @Override
    public String getDisplayName() {
        return "Sword";
    }
}
