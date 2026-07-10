"""compat_core.py -- loader-AGNOSTIC, version-keyed drift helpers for the SHARED Java files.

Used by the files in _codegen/cog_sources/shared/ (LavaBoats, LavaBoatLayers, AbstractBoatLavaMixin)
which are ONE copy cog-materialized into every pre-26 cell's gen/ tree. Loader-SPECIFIC
registration/entrypoint/client live in compat_fabric / compat_forge / compat_neoforge. The only
loader input here is the client annotation (@Environment) which Fabric needs and Forge/NeoForge
do not.

Version axes (unified branch, MC 1.20.1 - 1.21.11; 26 cells never run cog):
  rl_factory @1.21   : new ResourceLocation(ns,path) ctor  ->  ResourceLocation.fromNamespaceAndPath
  legacy boat <1.21.2: vanilla Boat target (converged BoatDropMixin era), array-era recipe API
  renamed @1.21.11   : Identifier rename + vehicle.boat package move
"""


def _vt(ver):
    return tuple(int(x) for x in ver.split("-")[0].split("."))


def is_legacy(ver):
    return _vt(ver) < (1, 21, 2)


def renamed(ver):
    return _vt(ver) >= (1, 21, 11)


def has_rl_factory(ver, loader=None):
    # ResourceLocation.fromNamespaceAndPath exists from 1.21 in vanilla. FORGE backported the
    # factories at 1.20.4 (49.2+) AND deprecated-for-removal the two-arg ctor there, so forge
    # targets switch a line earlier. NeoForge/Fabric 1.20.x keep the (undeprecated) ctor.
    if loader == "forge" and _vt(ver) >= (1, 20, 4):
        return True
    return _vt(ver) >= (1, 21)


def boat_pkg(ver):
    return "net.minecraft.world.entity.vehicle.boat" if renamed(ver) else "net.minecraft.world.entity.vehicle"


def id_type(ver):
    return "Identifier" if renamed(ver) else "ResourceLocation"


def boat_base_type(ver):
    return "Boat" if is_legacy(ver) else "AbstractBoat"


def make_id(ver, ns_expr, path_expr, loader=None):
    """Java expression constructing an id from (namespace, path) expressions, era+loader-correct."""
    if has_rl_factory(ver, loader):
        return "{0}.fromNamespaceAndPath({1}, {2})".format(id_type(ver), ns_expr, path_expr)
    return "new ResourceLocation({0}, {1})".format(ns_expr, path_expr)


# ---- Fabric-only client annotation (@Environment); Forge/NeoForge gate client via the entrypoint ----
def emit_environment(cog, loader):
    if loader == "fabric":
        cog.outl("@Environment(EnvType.CLIENT)")


def emit_environment_imports(cog, loader):
    if loader == "fabric":
        cog.outl("import net.fabricmc.api.EnvType;")
        cog.outl("import net.fabricmc.api.Environment;")


# ---- LavaBoats.RECIPES (unified List form; id built era-correct, no Platform dependency) ----
def emit_recipes_imports(cog, loader, ver):
    cog.outl("import java.util.List;")
    if is_legacy(ver):
        cog.outl("import net.minecraft.resources.ResourceLocation;")
    else:
        cog.outl("import net.minecraft.core.registries.Registries;")
        cog.outl("import net.minecraft.resources.ResourceKey;")
        cog.outl("import net.minecraft.resources." + id_type(ver) + ";")
        cog.outl("import net.minecraft.world.item.crafting.Recipe;")


def emit_recipes_block(cog, loader, ver):
    if is_legacy(ver):
        cog.outl("public static final List<ResourceLocation> RECIPES = List.of(")
        cog.outl('        recipeKey("crimson_boat"),')
        cog.outl('        recipeKey("warped_boat"),')
        cog.outl('        recipeKey("crimson_chest_boat"),')
        cog.outl('        recipeKey("warped_chest_boat"));')
        cog.outl("")
        cog.outl("private static ResourceLocation recipeKey(String name) {")
        cog.outl("    return " + make_id(ver, "MOD_ID", "name", loader) + ";")
        cog.outl("}")
    else:
        cog.outl("public static final List<ResourceKey<Recipe<?>>> RECIPES = List.of(")
        cog.outl('        recipeKey("crimson_boat"),')
        cog.outl('        recipeKey("warped_boat"),')
        cog.outl('        recipeKey("crimson_chest_boat"),')
        cog.outl('        recipeKey("warped_chest_boat"));')
        cog.outl("")
        cog.outl("private static ResourceKey<Recipe<?>> recipeKey(String name) {")
        cog.outl("    return ResourceKey.create(Registries.RECIPE, " + make_id(ver, "MOD_ID", "name", loader) + ");")
        cog.outl("}")


# ---- LavaBoatLayers id (era-correct construction) ----
def emit_layer_id_import(cog, loader, ver):
    cog.outl("import net.minecraft.resources." + id_type(ver) + ";")


def emit_layer_body(cog, loader, ver):
    cog.outl("return new ModelLayerLocation(" + make_id(ver, "LavaBoats.MOD_ID", "path", loader) + ", \"main\");")


# ---- legacy renderer: renderToBuffer overload changed at 1.21 (rgba floats -> packed-ARGB int) ----
def emit_render_to_buffer(cog, ver):
    if _vt(ver) >= (1, 21):
        cog.outl("// 1.21+: renderToBuffer takes a single packed-ARGB int (0xFFFFFFFF = opaque white).")
        cog.outl("this.model.renderToBuffer(poseStack, vc, packedLight, OverlayTexture.NO_OVERLAY, 0xFFFFFFFF);")
    else:
        cog.outl("this.model.renderToBuffer(poseStack, vc, packedLight, OverlayTexture.NO_OVERLAY, 1.0F, 1.0F, 1.0F, 1.0F);")

# ---- id-type import for any file whose generated code constructs ids directly ----
def emit_id_type_import(cog, loader, ver):
    cog.outl("import net.minecraft.resources." + id_type(ver) + ";")


# ---- shared client fragments: Forge and NeoForge emit IDENTICAL event-API lines ----
LAYERS_EVENT = """        event.registerLayerDefinition(LavaBoatLayers.CRIMSON_BOAT, BoatModel::createBoatModel);
        event.registerLayerDefinition(LavaBoatLayers.WARPED_BOAT, BoatModel::createBoatModel);
        event.registerLayerDefinition(LavaBoatLayers.CRIMSON_CHEST_BOAT, BoatModel::createChestBoatModel);
        event.registerLayerDefinition(LavaBoatLayers.WARPED_CHEST_BOAT, BoatModel::createChestBoatModel);"""

RENDER_VANILLA_EVENT = """        event.registerEntityRenderer(ModEntities.CRIMSON_BOAT.get(), ctx -> new BoatRenderer(ctx, LavaBoatLayers.CRIMSON_BOAT));
        event.registerEntityRenderer(ModEntities.WARPED_BOAT.get(), ctx -> new BoatRenderer(ctx, LavaBoatLayers.WARPED_BOAT));
        event.registerEntityRenderer(ModEntities.CRIMSON_CHEST_BOAT.get(), ctx -> new BoatRenderer(ctx, LavaBoatLayers.CRIMSON_CHEST_BOAT));
        event.registerEntityRenderer(ModEntities.WARPED_CHEST_BOAT.get(), ctx -> new BoatRenderer(ctx, LavaBoatLayers.WARPED_CHEST_BOAT));"""

RENDER_LEGACY_EVENT = """        event.registerEntityRenderer(ModEntities.CRIMSON_BOAT.get(), ctx -> new LavaBoatRenderer(ctx, LavaBoatRenderer.CRIMSON, false));
        event.registerEntityRenderer(ModEntities.WARPED_BOAT.get(), ctx -> new LavaBoatRenderer(ctx, LavaBoatRenderer.WARPED, false));
        event.registerEntityRenderer(ModEntities.CRIMSON_CHEST_BOAT.get(), ctx -> new LavaBoatRenderer(ctx, LavaBoatRenderer.CRIMSON_CHEST, true));
        event.registerEntityRenderer(ModEntities.WARPED_CHEST_BOAT.get(), ctx -> new LavaBoatRenderer(ctx, LavaBoatRenderer.WARPED_CHEST, true));"""


# ---- unified boat-fluid mixin (Forge + NeoForge share the canBoatInFluid redirect; the
# canBoatInFluid method is each loader's own patch, hence remap=false). Fabric uses the
# separate FluidState.is redirect (BoatWaterFabricMixin). ----
def emit_boat_fluid_mixin(cog, loader, ver):
    cls = "BoatFluidForgeMixin" if loader == "forge" else "BoatFluidNeoForgeMixin"
    label = "Forge" if loader == "forge" else "NeoForge"
    bt = boat_base_type(ver)
    pk = boat_pkg(ver)
    body = """import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import com.kishku7.lavaboats.ModEntities;

import net.minecraft.tags.FluidTags;
import %(pkgdot)s.%(bt)s;
import net.minecraft.world.level.material.FluidState;

/** %(label)s boat buoyancy: redirect canBoatInFluid so lava counts for our boats. */
@Mixin(%(bt)s.class)
public abstract class %(cls)s {

    @Redirect(
            method = {"checkInWater", "isUnderwater", "getWaterLevelAbove"},
            at = @At(value = "INVOKE",
                    target = "L%(pkgslash)s/%(bt)s;canBoatInFluid(Lnet/minecraft/world/level/material/FluidState;)Z",
                    remap = false)
    )
    private boolean lavaboats$lavaCountsForBoat(%(bt)s self, FluidState state) {
        if (self.canBoatInFluid(state)) {
            return true;
        }
        return ModEntities.isLavaBoat(self.getType()) && state.is(FluidTags.LAVA);
    }
}""" % {"cls": cls, "label": label, "bt": bt, "pkgdot": pk, "pkgslash": pk.replace(".", "/")}
    for ln in body.split("\n"):
        cog.outl(ln)


# ---- entity/item bodies IDENTICAL across Forge + NeoForge (both hold via RegistryObject/DeferredHolder,
# so they read the value with .get()). Single source here; both loader brains delegate emit_register_boat_body
# and the cog_sources emit the three method bodies directly via these. NOTE: Fabric is intentionally NOT a
# consumer -- its twins use plain static-field access (no .get()) and a different register-body signature. ----
def emit_register_boat_body(cog, ver, kind):
    typ = "Boat" if kind == "boat" else "ChestBoat"
    idt = id_type(ver)
    ctor = "new {0}(t, level)" if is_legacy(ver) else "new {0}(t, level, dropItem)"
    ctor = ctor.format(typ)
    cog.outl("return ENTITIES.register(name, () -> EntityType.Builder")
    cog.outl("        .<{0}>of((t, level) -> {1}, MobCategory.MISC)".format(typ, ctor))
    if not is_legacy(ver):
        cog.outl("        .noLootTable()")
    cog.outl("        .sized(1.375F, 0.5625F)")
    if not is_legacy(ver):
        cog.outl("        .eyeHeight(0.5625F)")
    cog.outl("        .clientTrackingRange(10)")
    cog.outl("        .fireImmune()")
    if is_legacy(ver):
        cog.outl("        .build(name));")
    else:
        cog.outl("        .build(ResourceKey.create(Registries.ENTITY_TYPE, {0}.fromNamespaceAndPath(LavaBoats.MOD_ID, name))));".format(idt))


def emit_is_lava_boat(cog):
    cog.outl("public static boolean isLavaBoat(EntityType<?> type) {")
    cog.outl("    return type == CRIMSON_BOAT.get() || type == WARPED_BOAT.get()")
    cog.outl("            || type == CRIMSON_CHEST_BOAT.get() || type == WARPED_CHEST_BOAT.get();")
    cog.outl("}")


def emit_drop_item_for(cog):
    cog.outl("public static Item dropItemFor(EntityType<?> type) {")
    cog.outl("    if (type == CRIMSON_BOAT.get()) return ModItems.CRIMSON_BOAT.get();")
    cog.outl("    if (type == WARPED_BOAT.get()) return ModItems.WARPED_BOAT.get();")
    cog.outl("    if (type == CRIMSON_CHEST_BOAT.get()) return ModItems.CRIMSON_CHEST_BOAT.get();")
    cog.outl("    if (type == WARPED_CHEST_BOAT.get()) return ModItems.WARPED_CHEST_BOAT.get();")
    cog.outl("    return null;")
    cog.outl("}")


def emit_is_lava_boat_item(cog):
    cog.outl("public static boolean isLavaBoatItem(ItemStack stack) {")
    cog.outl("    if (stack.isEmpty()) {")
    cog.outl("        return false;")
    cog.outl("    }")
    cog.outl("    Item i = stack.getItem();")
    cog.outl("    return i == CRIMSON_BOAT.get() || i == CRIMSON_CHEST_BOAT.get()")
    cog.outl("            || i == WARPED_BOAT.get() || i == WARPED_CHEST_BOAT.get();")
    cog.outl("}")
