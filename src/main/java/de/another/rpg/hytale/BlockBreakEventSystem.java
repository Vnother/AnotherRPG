package de.another.rpg.hytale;

import com.hypixel.hytale.component.ArchetypeChunk;
import com.hypixel.hytale.component.CommandBuffer;
import com.hypixel.hytale.component.Ref;
import com.hypixel.hytale.component.Store;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.component.system.EntityEventSystem;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.event.events.ecs.BreakBlockEvent;
import com.hypixel.hytale.server.core.universe.PlayerRef;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import de.another.rpg.AnotherRPG;
import de.another.rpg.api.component.SkillComponent;
import de.another.rpg.api.logic.BlockBreakEffect;
import de.another.rpg.core.SkillManager;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

import java.util.UUID;

public class BlockBreakEventSystem extends EntityEventSystem<EntityStore, BreakBlockEvent> {

    private final SkillManager skillManager = AnotherRPG.getInstance().getSkillManager();

    public BlockBreakEventSystem() {
        super(BreakBlockEvent.class);
    }


    @Override
    public void handle(int i
            , @NonNullDecl ArchetypeChunk<EntityStore> archetypeChunk,
                       @NonNullDecl Store<EntityStore> store,
                       @NonNullDecl CommandBuffer<EntityStore> commandBuffer,
                       @NonNullDecl BreakBlockEvent breakBlockEvent) {

        String blockId = breakBlockEvent.getBlockType().getId();
        Ref<EntityStore> entityStoreRef = archetypeChunk.getReferenceTo(i);
        Player player = store.getComponent(entityStoreRef, Player.getComponentType());
        if (player == null) {
            return;
        }

        @SuppressWarnings("removal") UUID playerId = player.getUuid();

        // 1. Get Skill Component (Stats)
        SkillComponent skillData = store.getComponent(entityStoreRef, SkillComponent.getComponentType());

        // 2. Delegate to Registered Effects (Logic in separate classes now)
        if (skillData != null) {
             for (BlockBreakEffect effect : AnotherRPG.getInstance().getEffectManager().getBlockBreakEffects()) {
                 effect.onBreak(breakBlockEvent, player, skillData, skillManager);
             }
        }

        skillManager.awardXpForBlock(playerId, blockId);


        // Add message to player (debugmode)
        player.sendMessage(Message.raw("You broke block: " + blockId));

    }

    public void AlternateTreeFellingMethod(BreakBlockEvent breakBlockEvent) {
        // Get all connected logs and break them
        if (breakBlockEvent.getItemInHand().getItemId().contains("Tool_Hatchet")) {



        } else {
            return;
        }
    }

    @NullableDecl
    @Override
    public Query<EntityStore> getQuery() {
        return PlayerRef.getComponentType();
    }


}
