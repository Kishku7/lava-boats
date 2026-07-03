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
    if is_legacy(ver):
        cog.outl("import net.minecraft.resources.ResourceLocation;")
    else:
        cog.outl("import net.minecraft.core.registries.Registries;")
        cog.outl("import net.minecraft.resources.ResourceKey;")
        cog.outl("import net.minecraft.resources." + id_type(ver) + ";")
        cog.outl("import net.minecraft.world.item.crafting.Recipe;")


def emit_recipes_block(cog, loader, ver):
    if is_legacy(ver):
        cog.outl("public static final java.util.List<ResourceLocation> RECIPES = java.util.List.of(")
        cog.outl('        recipeKey("crimson_boat"),')
        cog.outl('        recipeKey("warped_boat"),')
        cog.outl('        recipeKey("crimson_chest_boat"),')
        cog.outl('        recipeKey("warped_chest_boat"));')
        cog.outl("")
        cog.outl("private static ResourceLocation recipeKey(String name) {")
        cog.outl("    return " + make_id(ver, "MOD_ID", "name", loader) + ";")
        cog.outl("}")
    else:
        cog.outl("public static final java.util.List<ResourceKey<Recipe<?>>> RECIPES = java.util.List.of(")
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