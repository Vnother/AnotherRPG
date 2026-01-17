package de.another.rpg.modules.combat;

import de.another.rpg.api.skill.Skill;

public class DaggerSkill implements Skill {
    @Override
    public String getId() {
        return "dagger";
    }

    @Override
    public String getDisplayName() {
        return "Dagger";
    }
}
