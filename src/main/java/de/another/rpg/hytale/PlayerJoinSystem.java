package de.another.rpg.hytale;

import com.hypixel.hytale.component.*;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.RefSystem;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.event.events.player.AddPlayerToWorldEvent;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import de.another.rpg.AnotherRPG;
import de.another.rpg.api.component.SkillComponent;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

import java.util.UUID;

public class PlayerJoinSystem extends RefSystem<EntityStore> {

    public static final ComponentType<EntityStore, SkillComponent> SKILL_COMPONENT_TYPE = SkillComponent.getComponentType();

    public PlayerJoinSystem() {
        super();
    }

    @Override
    public void onEntityAdded(@NonNullDecl Ref<EntityStore> ref, @NonNullDecl AddReason addReason, @NonNullDecl Store<EntityStore> store, @NonNullDecl CommandBuffer<EntityStore> commandBuffer) {
        Ref<EntityStore> playerRef = ref;
        Player player = store.getComponent(playerRef, Player.getComponentType());

        if (player == null) return;

        @SuppressWarnings("removal") UUID uuid = player.getUuid();

        // Load data from storage
        SkillComponent skillData = AnotherRPG.getInstance().getPlayerStorage().loadPlayer(uuid);

        // Update SkillManager (Core Logic)
        AnotherRPG.getInstance().getSkillManager().loadPlayerData(uuid, skillData);

        // Attach Component to Player Entity
        commandBuffer.addComponent(playerRef, SKILL_COMPONENT_TYPE, skillData);

        System.out.println("Loaded and attached SkillComponent for player: " + uuid);
    }

    @Override
    public void onEntityRemove(@NonNullDecl Ref<EntityStore> ref, @NonNullDecl RemoveReason removeReason, @NonNullDecl Store<EntityStore> store, @NonNullDecl CommandBuffer<EntityStore> commandBuffer) {

    }


    @NullableDecl
    @Override
    public Query<EntityStore> getQuery() {
        return Query.any();
    }
}
