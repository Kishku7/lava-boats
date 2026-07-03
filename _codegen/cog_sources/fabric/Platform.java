package com.kishku7.lavaboats;

//[[[cog
// import sys; sys.path.insert(0, codegen); import compat_fabric as compat
// compat.emit_imports(cog, loader, ver)
//]]]
import net.fabricmc.api.EnvType;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.resources.Identifier;
//[[[end]]]

/**
 * Generated platform facade (Cog/compat.py). All loader/version drift for the wrapped operations
 * lives ONLY here. Business code calls Platform.* and imports nothing loader/version-specific.
 * Generate: cog -r -D loader=<x> -D ver=<a.b.c> Platform.java   Do not hand-edit cog regions.
 */
public final class Platform {
    private Platform() {}

    public static boolean isClient() {
        //[[[cog
        // compat.emit_is_client(cog, loader, ver)
        //]]]
        return FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT;
        //[[[end]]]
    }

    //[[[cog
    // compat.emit_id_method(cog, loader, ver)
    //]]]
    public static Identifier id(String namespace, String path) {
        return Identifier.fromNamespaceAndPath(namespace, path);
    }
    //[[[end]]]
}