package de.another.rpg.modules.combat;

import de.another.rpg.api.skill.Skill;

public class ArcherySkill implements Skill {
    @Override
    public String getId() {
        return "archery";
    }

    @Override
    public String getDisplayName() {
        return "Archery";
    }
}
