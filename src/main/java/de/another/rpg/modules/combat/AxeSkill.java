package de.another.rpg.modules.combat;

import de.another.rpg.api.skill.Skill;

public class AxeSkill implements Skill {
    @Override
    public String getId() {
        return "axe";
    }

    @Override
    public String getDisplayName() {
        return "Axe Fighting";
    }
}
