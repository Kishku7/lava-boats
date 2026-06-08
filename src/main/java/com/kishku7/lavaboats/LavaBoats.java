package com.kishku7.lavaboats;

import net.fabricmc.api.ModInitializer;

/**
 * Lava Boats - common entrypoint.
 *
 * Registers Crimson and Warped boats (plain + chest variants) that ride on lava
 * like water. The fireproofing and rider fire-safety run server-side for every
 * client; the actual lava buoyancy/steering is a client-side physics tweak
 * (see {@code mixin.client.AbstractBoatLavaMixin}).
 */
public class LavaBoats implements ModInitializer {
    public static final String MOD_ID = "lavaboats";

    @Override
    public void onInitialize() {
        // Order matters: entity types reference item drops via a lazy supplier,
        // items reference the (already-registered) entity types directly.
        ModEntities.register();
        ModItems.register();
    }
}
