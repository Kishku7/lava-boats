"""compat.py -- loader/version drift brain for the Platform facade.

Given (loader, version) it knows the imports + bodies for each platform method. The generated
Platform.java is the ONLY place these drifting symbols are named. Version-axis conditionals here
replace what Stonecutter used to do -- Cog is the sole preprocessor.

Methods:
  isClient()  -- loader + version drift (Fabric env type / Forge|NeoForge dist; NeoForge 1.21.9 split)
  id(ns,path) -- version-only drift: ResourceLocation -> Identifier rename at MC 1.21.11
"""


def _vt(ver):
    return tuple(int(x) for x in ver.split("-")[0].split("."))


def _is_client(loader, ver):
    v = _vt(ver)
    if loader == "fabric":
        return (["net.fabricmc.api.EnvType", "net.fabricmc.loader.api.FabricLoader"],
                "return FabricLoader.getInstance().getEnvironmentType() == EnvType.CLIENT;")
    if loader == "forge":
        return (["net.minecraftforge.api.distmarker.Dist", "net.minecraftforge.fml.loading.FMLEnvironment"],
                "return FMLEnvironment.dist == Dist.CLIENT;")
    if loader == "neoforge":
        if v >= (1, 21, 9):
            return (["net.neoforged.api.distmarker.Dist", "net.neoforged.fml.loading.FMLLoader"],
                    "return FMLLoader.getCurrent().getDist() == Dist.CLIENT;")
        return (["net.neoforged.api.distmarker.Dist", "net.neoforged.fml.loading.FMLEnvironment"],
                "return FMLEnvironment.dist == Dist.CLIENT;")
    raise ValueError("unknown loader: " + loader)


def _id_type(ver):
    return "Identifier" if _vt(ver) >= (1, 21, 11) else "ResourceLocation"


def _id_import(ver):
    return "net.minecraft.resources." + _id_type(ver)


def emit_imports(cog, loader, ver):
    imps = set(_is_client(loader, ver)[0])
    imps.add(_id_import(ver))
    for imp in sorted(imps):
        cog.outl("import {0};".format(imp))


def emit_is_client(cog, loader, ver):
    cog.outl(_is_client(loader, ver)[1])


def has_rl_factory(ver):
    # ResourceLocation.fromNamespaceAndPath exists from 1.21; 1.20.x uses the public ctor.
    return _vt(ver) >= (1, 21)


def emit_id_method(cog, loader, ver):
    t = _id_type(ver)
    cog.outl("public static {0} id(String namespace, String path) {{".format(t))
    if has_rl_factory(ver):
        cog.outl("    return {0}.fromNamespaceAndPath(namespace, path);".format(t))
    else:
        cog.outl("    return new ResourceLocation(namespace, path);")
    cog.outl("}")


# ---- boat package move at MC 1.21.11 (vehicle.* -> vehicle.boat.*) ----
# Used by a one-line cog conditional on each affected import; simple names are unchanged.
def boat_pkg(ver):
    return "net.minecraft.world.entity.vehicle.boat" if _vt(ver) >= (1, 21, 11) \
        else "net.minecraft.world.entity.vehicle"

# client BoatModel package move at MC 1.21.11 (model -> model.object.boat)
def boat_model_pkg(ver):
    return "net.minecraft.client.model.object.boat" if _vt(ver) >= (1, 21, 11) \
        else "net.minecraft.client.model"

# ---- era split: pre-1.21.2 boat refactor (legacy) vs 1.21.2+ (modern) ----
def is_legacy(ver):
    return _vt(ver) < (1, 21, 2)


def boat_base_type(ver):
    # buoyancy-mixin target: vanilla Boat pre-1.21.2; AbstractBoat from 1.21.2
    return "Boat" if is_legacy(ver) else "AbstractBoat"


def _emit(cog, lines):
    for ln in lines:
        cog.outl(ln)


def emit_register_boat_body(cog, loader, ver):
    if is_legacy(ver):
        _emit(cog, [
            "var id = Platform.id(LavaBoats.MOD_ID, name);",
            "EntityType<Boat> type = EntityType.Builder",
            "        .<Boat>of((t, level) -> new Boat(t, level), MobCategory.MISC)",
            "        .sized(1.375F, 0.5625F)",
            "        .clientTrackingRange(10)",
            "        .fireImmune()",
            "        .build(name);",
            "return Registry.register(BuiltInRegistries.ENTITY_TYPE, id, type);",
        ])
    else:
        _emit(cog, [
            "var id = Platform.id(LavaBoats.MOD_ID, name);",
            "EntityType<Boat> type = EntityType.Builder",
            "        .<Boat>of((t, level) -> new Boat(t, level, dropItem), MobCategory.MISC)",
            "        .noLootTable()",
            "        .sized(1.375F, 0.5625F)",
            "        .eyeHeight(0.5625F)",
            "        .clientTrackingRange(10)",
            "        .fireImmune()",
            "        .build(ResourceKey.create(Registries.ENTITY_TYPE, id));",
            "return Registry.register(BuiltInRegistries.ENTITY_TYPE, id, type);",
        ])


def emit_register_chest_boat_body(cog, loader, ver):
    if is_legacy(ver):
        _emit(cog, [
            "var id = Platform.id(LavaBoats.MOD_ID, name);",
            "EntityType<ChestBoat> type = EntityType.Builder",
            "        .<ChestBoat>of((t, level) -> new ChestBoat(t, level), MobCategory.MISC)",
            "        .sized(1.375F, 0.5625F)",
            "        .clientTrackingRange(10)",
            "        .fireImmune()",
            "        .build(name);",
            "return Registry.register(BuiltInRegistries.ENTITY_TYPE, id, type);",
        ])
    else:
        _emit(cog, [
            "var id = Platform.id(LavaBoats.MOD_ID, name);",
            "EntityType<ChestBoat> type = EntityType.Builder",
            "        .<ChestBoat>of((t, level) -> new ChestBoat(t, level, dropItem), MobCategory.MISC)",
            "        .noLootTable()",
            "        .sized(1.375F, 0.5625F)",
            "        .eyeHeight(0.5625F)",
            "        .clientTrackingRange(10)",
            "        .fireImmune()",
            "        .build(ResourceKey.create(Registries.ENTITY_TYPE, id));",
            "return Registry.register(BuiltInRegistries.ENTITY_TYPE, id, type);",
        ])


def emit_register_item_method(cog, loader, ver):
    base = boat_base_type(ver)
    cog.outl("private static Item register(String name, Supplier<? extends EntityType<? extends {0}>> type) {{".format(base))
    cog.outl("    var id = Platform.id(LavaBoats.MOD_ID, name);")
    if is_legacy(ver):
        cog.outl("    Item item = new LavaBoatItem(type, new Item.Properties().stacksTo(1).fireResistant());")
    else:
        cog.outl("    ResourceKey<Item> key = ResourceKey.create(Registries.ITEM, id);")
        cog.outl("    BoatItem item = new BoatItem(type.get(), new Item.Properties().stacksTo(1).fireResistant().setId(key));")
    cog.outl("    return Registry.register(BuiltInRegistries.ITEM, id, item);")
    cog.outl("}")

# ---- client init: legacy custom renderer vs modern vanilla renderer + model layers ----
def uses_vanilla_renderers(ver):
    # fapi deprecated EntityRendererRegistry at 1.21.9; replacement = vanilla
    # EntityRenderers.register, made public by Fabric Transitive Access Wideners (v1).
    return _vt(ver) >= (1, 21, 9)


def _renderer_registrar(ver):
    return "EntityRenderers" if uses_vanilla_renderers(ver) else "EntityRendererRegistry"


def emit_client_imports(cog, loader, ver):
    cog.outl("import com.kishku7.lavaboats.ModEntities;")
    cog.outl("import net.fabricmc.api.ClientModInitializer;")
    if uses_vanilla_renderers(ver):
        cog.outl("import net.minecraft.client.renderer.entity.EntityRenderers;")
    else:
        cog.outl("import net.fabricmc.fabric.api.client.rendering.v1.EntityRendererRegistry;")
    if is_legacy(ver):
        cog.outl("import com.kishku7.lavaboats.client.LavaBoatRenderer;")
    else:
        cog.outl("import com.kishku7.lavaboats.client.LavaBoatLayers;")
        cog.outl("import net.fabricmc.fabric.api.client.rendering.v1.EntityModelLayerRegistry;")
        cog.outl("import net.minecraft.client.renderer.entity.BoatRenderer;")
        cog.outl("import " + boat_model_pkg(ver) + ".BoatModel;")


_BOATS = [("CRIMSON_BOAT", "CRIMSON", False), ("WARPED_BOAT", "WARPED", False),
          ("CRIMSON_CHEST_BOAT", "CRIMSON_CHEST", True), ("WARPED_CHEST_BOAT", "WARPED_CHEST", True)]


def emit_client_body(cog, loader, ver):
    reg = _renderer_registrar(ver)
    if is_legacy(ver):
        for field, tex, chest in _BOATS:
            cog.outl("{0}.register(ModEntities.{1},".format(reg, field))
            cog.outl("        ctx -> new LavaBoatRenderer(ctx, LavaBoatRenderer.{0}, {1}));".format(tex, "true" if chest else "false"))
    else:
        for field, tex, chest in _BOATS:
            maker = "createChestBoatModel" if chest else "createBoatModel"
            cog.outl("EntityModelLayerRegistry.registerModelLayer(LavaBoatLayers.{0}, BoatModel::{1});".format(field, maker))
        for field, tex, chest in _BOATS:
            cog.outl("{0}.register(ModEntities.{1}, ctx -> new BoatRenderer(ctx, LavaBoatLayers.{1}));".format(reg, field))

# ---- recipe-unlock API: List<ResourceLocation> (legacy) vs List<ResourceKey<Recipe<?>>> (modern) ----
def emit_recipes_imports(cog, loader, ver):
    if is_legacy(ver):
        cog.outl("import net.minecraft.resources.ResourceLocation;")
    else:
        cog.outl("import net.minecraft.core.registries.Registries;")
        cog.outl("import net.minecraft.resources.ResourceKey;")
        cog.outl("import net.minecraft.world.item.crafting.Recipe;")


def emit_recipes_block(cog, loader, ver):
    if is_legacy(ver):
        cog.outl("public static final List<ResourceLocation> RECIPES = List.of(")
    else:
        cog.outl("public static final List<ResourceKey<Recipe<?>>> RECIPES = List.of(")
    cog.outl('        recipeKey("crimson_boat"),')
    cog.outl('        recipeKey("warped_boat"),')
    cog.outl('        recipeKey("crimson_chest_boat"),')
    cog.outl('        recipeKey("warped_chest_boat"));')
    cog.outl("")
    if is_legacy(ver):
        cog.outl("private static ResourceLocation recipeKey(String name) {")
        cog.outl("    return Platform.id(MOD_ID, name);")
        cog.outl("}")
    else:
        cog.outl("private static ResourceKey<Recipe<?>> recipeKey(String name) {")
        cog.outl("    return ResourceKey.create(Registries.RECIPE, Platform.id(MOD_ID, name));")
        cog.outl("}")

# ---- fabric entrypoint: recipe-book unlock on JOIN (array API < 1.20.3; List from 1.20.3) ----
def emit_join_unlock_imports(cog, ver):
    if _vt(ver) < (1, 20, 3):
        cog.outl("import net.minecraft.resources.ResourceLocation;")


def emit_join_unlock(cog, ver):
    if _vt(ver) < (1, 20, 3):
        arg = "LavaBoats.RECIPES.toArray(new ResourceLocation[0])"
    else:
        arg = "LavaBoats.RECIPES"
    cog.outl("ServerPlayConnectionEvents.JOIN.register((handler, sender, server) ->")
    cog.outl("        handler.getPlayer().awardRecipesByKey({0}));".format(arg))