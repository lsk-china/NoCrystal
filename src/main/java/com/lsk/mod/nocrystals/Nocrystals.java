package com.lsk.mod.nocrystals;

import com.mojang.brigadier.Command;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.command.v1.CommandRegistrationCallback;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerEntityEvents;
import net.minecraft.entity.decoration.EndCrystalEntity;
import net.minecraft.server.command.CommandManager;
import net.minecraft.text.LiteralText;
import net.minecraft.world.World;
import org.apache.logging.log4j.core.jmx.Server;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Nocrystals implements ModInitializer {
    private static final Logger log = LoggerFactory.getLogger(Nocrystals.class);
    private static volatile boolean enabled = true;
    @Override
    public void onInitialize() {

        CommandRegistrationCallback.EVENT.register((dispatcher, dedicated) -> {
            dispatcher.register(CommandManager.literal("crystals")
                    .then(CommandManager.
                            literal("enable").
                            executes(context -> {
                                Nocrystals.enabled = true;
                                return Command.SINGLE_SUCCESS;
                            }))
                    .then(CommandManager.
                            literal("disable").
                            executes(context -> {
                                Nocrystals.enabled = false;
                                log.info("disable");
                                return Command.SINGLE_SUCCESS;
                            }))
                    .then(CommandManager.
                            literal("state").
                            executes(context -> {
                                context.getSource().sendFeedback(new LiteralText("Ban crystals has been " + (Nocrystals.enabled ? "enabled" : "disabled")), false);
                                return Command.SINGLE_SUCCESS;
                            }))
                    .executes(context -> {
                context.getSource().sendFeedback(new LiteralText("Usage: /crystals enable | disable | state"), false);
                return Command.SINGLE_SUCCESS;
            }));
        });

        ServerEntityEvents.ENTITY_LOAD.register((entity, world) -> {
            if (!world.getRegistryKey().getValue().equals(World.END.getValue()) && entity.getClass() == EndCrystalEntity.class && Nocrystals.enabled) {
                log.info("Killing entity " + entity.getClass().getSimpleName() + " at " + entity.getPos().x + ", " + entity.getPos().y + ", " + entity.getPos().z);
                entity.kill();
            }
        });
    }
}
