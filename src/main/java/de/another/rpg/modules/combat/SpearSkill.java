package de.another.rpg.modules.combat;

import de.another.rpg.api.skill.Skill;

public class SpearSkill implements Skill {
    @Override
    public String getId() {
        return "spear";
    }

    @Override
    public String getDisplayName() {
        return "Spear";
    }
}
