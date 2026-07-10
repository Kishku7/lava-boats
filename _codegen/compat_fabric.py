"""compat.py -- loader/version drift brain for the Platform facade.

Given (loader, version) it knows the imports + bodies for each platform method. The generated
Platform.java is the ONLY place these drifting symbols are named. Version-axis conditionals here
replace what Stonecutter used to do -- Cog is the sole preprocessor.

Methods:
  isClient()  -- loader + version drift (Fabric env type / Forge|NeoForge dist; NeoForge 1.21.9 split)
  id(ns,path) -- version-only drift: ResourceLocation -> Identifier rename at MC 1.21.11
"""


import compat_core


def _vt(ver):
    return tuple(int(x) for x in ver.split("-")[0].split("."))


def is_26(ver):
    return _vt(ver) >= (26, 0, 0)

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
            "var id = " + compat_core.make_id(ver, "LavaBoats.MOD_ID", "name", "fabric") + ";",
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
            "var id = " + compat_core.make_id(ver, "LavaBoats.MOD_ID", "name", "fabric") + ";",
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
            "var id = " + compat_core.make_id(ver, "LavaBoats.MOD_ID", "name", "fabric") + ";",
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
            "var id = " + compat_core.make_id(ver, "LavaBoats.MOD_ID", "name", "fabric") + ";",
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
    cog.outl("    var id = " + compat_core.make_id(ver, "LavaBoats.MOD_ID", "name", "fabric") + ";")
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
        cog.outl("import net.fabricmc.fabric.api.client.rendering.v1." + _model_layer_registrar(ver) + ";")
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
        mlr = _model_layer_registrar(ver)
        for field, tex, chest in _BOATS:
            maker = "createChestBoatModel" if chest else "createBoatModel"
            cog.outl("{0}.registerModelLayer(LavaBoatLayers.{1}, BoatModel::{2});".format(mlr, field, maker))
        for field, tex, chest in _BOATS:
            cog.outl("{0}.register(ModEntities.{1}, ctx -> new BoatRenderer(ctx, LavaBoatLayers.{1}));".format(reg, field))

# ---- fabric entrypoint: recipe-book unlock on JOIN (array API < 1.20.3; List from 1.20.3) ----
def emit_join_unlock_imports(cog, ver):
    if _vt(ver) < (1, 20, 3):
        cog.outl("import net.minecraft.resources.ResourceLocation;")


def emit_join_unlock(cog, ver):
    if _vt(ver) < (1, 20, 3):
        arg = "LavaBoats.RECIPES.toArray(new ResourceLocation[0])"
    else:
        arg = "LavaBoats.RECIPES"
    player = "handler.player" if is_26(ver) else "handler.getPlayer()"
    cog.outl("ServerPlayConnectionEvents.JOIN.register((handler, sender, server) ->")
    cog.outl("        {0}.awardRecipesByKey({1}));".format(player, arg))

# ---- fabric model-layer registry renamed EntityModelLayerRegistry -> ModelLayerRegistry at MC 26 ----
def _model_layer_registrar(ver):
    return "ModelLayerRegistry" if is_26(ver) else "EntityModelLayerRegistry"


# ---- creative-tab insertion: ONE facade point (ModItems.init), version-branched. pre-26 uses
#      itemgroup.v1 ItemGroupEvents; MC 26 uses creativetab.v1 CreativeModeTabEvents (fapi renamed it). ----
def emit_moditems_tab_imports(cog, ver):
    cog.outl("import net.minecraft.world.item.CreativeModeTabs;")
    if is_26(ver):
        cog.outl("import net.fabricmc.fabric.api.creativetab.v1.CreativeModeTabEvents;")
    else:
        cog.outl("import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;")


def emit_moditems_tab(cog, ver):
    cog.outl("// Slot the boats into the Tools & Utilities tab alongside the vanilla boats.")
    if is_26(ver):
        cog.outl("CreativeModeTabEvents.modifyOutputEvent(CreativeModeTabs.TOOLS_AND_UTILITIES).register(output -> {")
        cog.outl("    output.accept(CRIMSON_BOAT);")
        cog.outl("    output.accept(CRIMSON_CHEST_BOAT);")
        cog.outl("    output.accept(WARPED_BOAT);")
        cog.outl("    output.accept(WARPED_CHEST_BOAT);")
        cog.outl("});")
    else:
        cog.outl("ItemGroupEvents.modifyEntriesEvent(CreativeModeTabs.TOOLS_AND_UTILITIES).register(entries ->")
        cog.outl("        TAB_ITEMS.forEach(entries::accept));")
