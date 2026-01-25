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

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;

public class CombatEventSystem extends DamageEventSystem {
    public CombatEventSystem() {
        super();
    }

    private final SkillManager skillManager = AnotherRPG.getInstance().getSkillManager();
    private static final ConcurrentHashMap<Class<?>, DamageDisplayAccessors> DISPLAY_CACHE = new ConcurrentHashMap<>();


    @NullableDecl
    @Override
    public Query<EntityStore> getQuery() {
        return Query.any();
    }

    @NullableDecl
    @Override
    public SystemGroup<EntityStore> getGroup() {
        try {
            Class<?> mod = Class.forName("com.hypixel.hytale.server.core.modules.entity.damage.DamageModule");
            Object inst = mod.getMethod("get").invoke(null);
            return (SystemGroup<EntityStore>) mod.getMethod("getFilterDamageGroup").invoke(inst);
        } catch (Throwable e) {
            e.printStackTrace();
            return null;
        }
    }


    @Override
    public void handle(int i, @NonNullDecl ArchetypeChunk<EntityStore> archetypeChunk, @NonNullDecl Store<EntityStore> store, @NonNullDecl CommandBuffer<EntityStore> commandBuffer, @NonNullDecl Damage damage) {
        try {
            // Check if source is an Entity (Attacker)
            if (!(damage.getSource() instanceof Damage.EntitySource source)) {
                return;
            }

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

            // Skill Logic: Increase Damage by 1% per Combat Level
            if (attackerUUID != null) {
                int combatLevel = skillManager.getLevel(attackerUUID.getUuid(), "combat");
                float multiplier = 1.0f + (combatLevel * 0.01f);

                float currentAmount = damage.getAmount();
                float totalAmount = (float) Math.ceil(currentAmount * multiplier);

                damage.setAmount(totalAmount);
                applyDisplayAmount(damage, currentAmount, totalAmount);

                System.out.println("Applied Combat Level Damage Bonus: Level " + combatLevel + " -> Multiplier: " + multiplier);
                System.out.println("New Damage Amount: " + totalAmount + " (Original Damage: " + currentAmount + ")");
            }


            ComponentType<EntityStore, EntityStatMap> statMapType =
                    EntityStatsModule.get().getEntityStatMapComponentType();
            // Get Components for Victim (Who was hit?)
            UUIDComponent victimUUID = store.getComponent(victimRef, UUIDComponent.getComponentType());
            NPCEntity victim = store.getComponent(victimRef, NPCEntity.getComponentType());
            if (victim == null && victimUUID == null) {
                return; // Victim is neither a Player nor an NPC
            }
            EntityStatMap stats = store.getComponent(victimRef, statMapType);

            if (stats == null) return;

            int healthIndex = DefaultEntityStatTypes.getHealth();
            EntityStatValue entityStatValue = stats.get(healthIndex);

            if (entityStatValue == null) return;

            double health = entityStatValue.get(); // Current Health of the Victim
            // Check if this hit will kill the victim
            if (health - damage.getAmount() > 0) {
                return; // Victim is still alive after this hit
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
                if (victim != null) {
                     System.out.println("Result: Hit a Mob! type: " + victim.getNPCTypeId());
                }
                skillManager.addXpCombat(attackerUUID.getUuid(), itemInHand, entityStatValue.getMax());

                // Example: skillManager.addXpCombat(attackerUUID.getUuid(), itemInHand, damage);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    private void applyDisplayAmount(Damage damage, float currentAmount, float totalAmount) {
        DamageDisplayAccessors acc = DISPLAY_CACHE.computeIfAbsent(damage.getClass(), CombatEventSystem::resolveDisplayAccessors);
        if (acc.putMeta != null) {
            try {
                acc.putMeta.invoke(damage, "DisplayAmount", totalAmount);
                acc.putMeta.invoke(damage, "Amount", totalAmount);
            } catch (Throwable ignored) {
            }
        }

        if (acc.setInitialAmount != null) {
            try {
                acc.setInitialAmount.invoke(damage, totalAmount);
                return;
            } catch (Throwable ignored) {
            }
        }

        if (acc.initialAmountField != null) {
            try {
                acc.initialAmountField.setFloat(damage, totalAmount);
                return;
            } catch (Throwable ignored) {
            }
        }

        Field cached = acc.matchedAmountField.get();
        if (cached != null) {
            try {
                if (cached.getType() == Float.TYPE) {
                    cached.setFloat(damage, totalAmount);
                } else {
                    cached.set(damage, totalAmount);
                }

                return;
            } catch (Throwable ignored) {
                acc.matchedAmountField.compareAndSet(cached, null);
            }
        }

        for (Class<?> c = damage.getClass(); c != null && c != Object.class; c = c.getSuperclass()) {
            Field[] var7 = c.getDeclaredFields();
            for (Field f : var7) {
                if (Float.TYPE == f.getType() || Float.class == f.getType()) {
                    try {
                        f.setAccessible(true);
                        float v = f.getType() == Float.TYPE ? f.getFloat(damage) : ((Number) f.get(damage)).floatValue();
                        if (Math.abs(v - currentAmount) < 0.01F) {
                            if (f.getType() == Float.TYPE) {
                                f.setFloat(damage, totalAmount);
                            } else {
                                f.set(damage, totalAmount);
                            }

                            acc.matchedAmountField.compareAndSet(null, f);
                            return;
                        }
                    } catch (Throwable ignored) {
                    }
                }
            }
        }

    }

    @NonNullDecl
    private static DamageDisplayAccessors resolveDisplayAccessors(@NonNullDecl Class<?> damageClass) {
        Method putMeta = null;

        try {
            putMeta = damageClass.getMethod("putMeta", Object.class, Object.class);
        } catch (Throwable ignored) {
        }

        Method setInitialAmount = null;

        try {
            setInitialAmount = damageClass.getMethod("setInitialAmount", Float.TYPE);
        } catch (Throwable ignored) {
        }

        Field initialAmountField = null;
        Class<?> c = damageClass;

        while (c != null) {
            try {
                Field f = c.getDeclaredField("initialAmount");
                f.setAccessible(true);
                initialAmountField = f;
                break;
            } catch (NoSuchFieldException ignored) {
                c = c.getSuperclass();
            } catch (Throwable ignored) {
                break;
            }
        }

        return new DamageDisplayAccessors(putMeta, setInitialAmount, initialAmountField);
    }

    private static final class DamageDisplayAccessors {
        @NullableDecl
        final Method putMeta;
        @NullableDecl
        final Method setInitialAmount;
        @NullableDecl
        final Field initialAmountField;
        final AtomicReference<Field> matchedAmountField = new AtomicReference<>(null);

        DamageDisplayAccessors(@NullableDecl Method putMeta, @NullableDecl Method setInitialAmount, @NullableDecl Field initialAmountField) {
            this.putMeta = putMeta;
            this.setInitialAmount = setInitialAmount;
            this.initialAmountField = initialAmountField;
        }
    }
}

