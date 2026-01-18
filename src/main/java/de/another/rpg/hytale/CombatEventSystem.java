package de.another.rpg.hytale;


import com.hypixel.hytale.component.*;
import com.hypixel.hytale.component.query.Query;
import com.hypixel.hytale.server.core.Message;
import com.hypixel.hytale.server.core.entity.UUIDComponent;
import com.hypixel.hytale.server.core.entity.entities.Player;
import com.hypixel.hytale.server.core.inventory.Inventory;
import com.hypixel.hytale.server.core.inventory.ItemStack;
import com.hypixel.hytale.server.core.modules.entity.damage.Damage;
import com.hypixel.hytale.server.core.modules.entity.damage.DamageEventSystem;
import com.hypixel.hytale.server.core.modules.entitystats.EntityStatMap;
import com.hypixel.hytale.server.core.modules.entitystats.EntityStatValue;
import com.hypixel.hytale.server.core.modules.entitystats.EntityStatsModule;
import com.hypixel.hytale.server.core.modules.entitystats.asset.DefaultEntityStatTypes;
import com.hypixel.hytale.server.core.universe.world.storage.EntityStore;
import com.hypixel.hytale.server.npc.entities.NPCEntity;
import de.another.rpg.AnotherRPG;
import de.another.rpg.core.SkillManager;
import org.checkerframework.checker.nullness.compatqual.NonNullDecl;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

public class CombatEventSystem extends DamageEventSystem {
    public CombatEventSystem() {
        super();
    }

    private final SkillManager skillManager = AnotherRPG.getInstance().getSkillManager();


    @NullableDecl
    @Override
    public Query<EntityStore> getQuery() {
        return Query.any();
    }


    @Override
    public void handle(int i, @NonNullDecl ArchetypeChunk<EntityStore> archetypeChunk, @NonNullDecl Store<EntityStore> store, @NonNullDecl CommandBuffer<EntityStore> commandBuffer, @NonNullDecl Damage damage) {
        try {
            // Check if source is an Entity (Attacker)
            if (!(damage.getSource() instanceof Damage.EntitySource)) {
                return;
            }

            Damage.EntitySource source = (Damage.EntitySource) damage.getSource();

            // 1. The Attacker (Who dealt the damage)
            Ref<EntityStore> attackerRef = source.getRef();

            // 2. The Victim (The entity receiving the damage/event)
            Ref<EntityStore> victimRef = archetypeChunk.getReferenceTo(i);

            // Get Components for Attacker
            Player attackerPlayer = store.getComponent(attackerRef, Player.getComponentType());
            UUIDComponent attackerUUID = store.getComponent(attackerRef, UUIDComponent.getComponentType());
            if (attackerPlayer == null) {
                return; // Attacker is not a player
            }

            ComponentType<EntityStore, EntityStatMap> statMapType =
                    EntityStatsModule.get().getEntityStatMapComponentType();
            // Get Components for Victim (Who was hit?)
            UUIDComponent victimUUID = store.getComponent(victimRef, UUIDComponent.getComponentType());
            NPCEntity victim = store.getComponent(victimRef, NPCEntity.getComponentType());
            if (victim == null && victimUUID == null) {
                return; // Victim is neither a Player nor an NPC
            }
            EntityStatMap stats = store.getComponent(victimRef, statMapType); // Null if not a player (e.g. a Mob)
            int healthIndex = DefaultEntityStatTypes.getHealth();
            EntityStatValue entityStatValue = stats.get(healthIndex);
            double health = entityStatValue.get(); // Current Health of the Victim
            if (health > 0) {
                return; // Victim is still alive, we only want to process kills
            }


            if (attackerUUID == null) {
                return;
            }

            Inventory inv = attackerPlayer.getInventory();
            if (inv == null) {
                return;
            }

            ItemStack itemInHand = inv.getItemInHand();
            if (itemInHand != null) {
                String itemId = itemInHand.getItemId();

                // Logic: Award XP based on weapon
                System.out.println("Player " + attackerPlayer.getDisplayName() + " attacked using item: " + itemId);
                System.out.println("Result: Hit a Mob! type: " + victim.getNPCTypeId());
                skillManager.addXpCombat(attackerUUID.getUuid(), itemInHand, entityStatValue.getMax());






                // Example: skillManager.addXpCombat(attackerUUID.getUuid(), itemInHand, damage);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}