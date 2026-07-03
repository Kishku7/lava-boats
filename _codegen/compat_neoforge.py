"""compat.py -- NeoForge loader/version drift brain (lava-boats unified NeoForge 1.21.x tree).

Full range 1.21-1.21.11 (MDG). Three drift axes, each already solved on Fabric/Forge:
  legacy {1.21,1.21.1}    pre-boat-refactor (custom subclasses -> vanilla Boat + BoatDropMixin)
  dist split @1.21.9      FMLEnvironment.dist  ->  FMLLoader.getCurrent().getDist()
  rename @1.21.11         ResourceLocation->Identifier; vehicle.*->vehicle.boat.*; client model->model.object.boat
DeferredRegister/DeferredHolder native. Entrypoint + client are uniform (always IEventBus ctor + register).
"""


import compat_core


def _vt(ver):
    return tuple(int(x) for x in ver.split("-")[0].split("."))


def is_legacy(ver):
    return _vt(ver) < (1, 21, 2)


def dist_modern(ver):
    return _vt(ver) >= (1, 21, 9)


def renamed(ver):
    return _vt(ver) >= (1, 21, 11)


def boat_pkg(ver):
    return "net.minecraft.world.entity.vehicle.boat" if renamed(ver) else "net.minecraft.world.entity.vehicle"


def boat_model_pkg(ver):
    return "net.minecraft.client.model.object.boat" if renamed(ver) else "net.minecraft.client.model"


def id_type(ver):
    return "Identifier" if renamed(ver) else "ResourceLocation"


def boat_base_type(ver):
    return "Boat" if is_legacy(ver) else "AbstractBoat"


# ---- ModEntities/ModItems register(IEventBus) ----
def emit_entities_register(cog, ver):
    cog.outl("public static void register(IEventBus modBus) {")
    cog.outl("    ENTITIES.register(modBus);")
    cog.outl("}")


def emit_items_register_bus(cog, ver):
    cog.outl("public static void register(IEventBus modBus) {")
    cog.outl("    ITEMS.register(modBus);")
    cog.outl("}")


# ---- entity registration bodies (DeferredHolder, converged vanilla Boat) ----
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


# ---- item registration ----
def emit_register_item_method(cog, ver):
    base = boat_base_type(ver)
    idt = id_type(ver)
    cog.outl("private static DeferredHolder<Item, Item> register(String name, Supplier<? extends EntityType<? extends {0}>> type) {{".format(base))
    if is_legacy(ver):
        cog.outl("    return ITEMS.register(name, () -> new LavaBoatItem(type, new Item.Properties().stacksTo(1).fireResistant()));")
    else:
        cog.outl("    {0} id = {0}.fromNamespaceAndPath(LavaBoats.MOD_ID, name);".format(idt))
        cog.outl("    ResourceKey<Item> key = ResourceKey.create(Registries.ITEM, id);")
        cog.outl("    return ITEMS.register(name, () -> new BoatItem(type.get(), new Item.Properties().stacksTo(1).fireResistant().setId(key)));")
    cog.outl("}")

def recipes_unlock_arg(ver):
    # RECIPES is the unified List everywhere; below 1.20.3 the vanilla method takes an ARRAY
    # (deobf-verified: Player.awardRecipesByKey(ResourceLocation[]) on 1.20.1 AND 1.20.2).
    if _vt(ver) < (1, 20, 3):
        return "LavaBoats.RECIPES.toArray(new ResourceLocation[0])"
    return "LavaBoats.RECIPES"


def unlock_extra_import(ver):
    return "import net.minecraft.resources.ResourceLocation;\n" if _vt(ver) < (1, 20, 3) else ""


# ---- entrypoint (uniform: IEventBus ctor + register + dist gate) ----
def _dist_gate(ver):
    return "FMLLoader.getCurrent().getDist() == Dist.CLIENT" if dist_modern(ver) \
        else "FMLEnvironment.dist == Dist.CLIENT"


def _dist_import(ver):
    return "net.neoforged.fml.loading.FMLLoader" if dist_modern(ver) \
        else "net.neoforged.fml.loading.FMLEnvironment"


def emit_entrypoint_file(cog, ver):
    body = '''import com.kishku7.lavaboats.LavaBoats;
import com.kishku7.lavaboats.ModEntities;
import com.kishku7.lavaboats.ModItems;
import com.kishku7.lavaboats.neoforge.client.LavaBoatsNeoForgeClient;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.CreativeModeTabs;
%(rlimp)simport net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import %(distimp)s;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;

@Mod(LavaBoats.MOD_ID)
public class LavaBoatsNeoForge {
    public LavaBoatsNeoForge(IEventBus modBus) {
        ModEntities.register(modBus);
        ModItems.register(modBus);

        modBus.addListener(this::onBuildTabs);
        NeoForge.EVENT_BUS.addListener(this::onPlayerJoin);

        if (%(gate)s) {
            LavaBoatsNeoForgeClient.register(modBus);
        }
    }

    private void onBuildTabs(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.TOOLS_AND_UTILITIES) {
            ModItems.TAB_ITEMS.forEach(s -> event.accept(s.get()));
        }
    }

    private void onPlayerJoin(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            player.awardRecipesByKey(%(arg)s);
        }
    }
}''' % {"distimp": _dist_import(ver), "gate": _dist_gate(ver), "arg": recipes_unlock_arg(ver),
         "rlimp": unlock_extra_import(ver)}
    for ln in body.split("\n"):
        cog.outl(ln)


# ---- client (uniform mechanism; legacy custom renderer vs modern layers; BoatModel pkg @1.21.11) ----


def emit_client_file(cog, ver):
    if is_legacy(ver):
        body = '''import com.kishku7.lavaboats.ModEntities;
import com.kishku7.lavaboats.client.LavaBoatRenderer;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

public final class LavaBoatsNeoForgeClient {
    private LavaBoatsNeoForgeClient() {}

    public static void register(IEventBus modBus) {
        modBus.addListener(LavaBoatsNeoForgeClient::onRegisterRenderers);
    }

    private static void onRegisterRenderers(EntityRenderersEvent.RegisterRenderers event) {
%(rend)s
    }
}''' % {"rend": compat_core.RENDER_LEGACY_EVENT}
    else:
        body = '''import com.kishku7.lavaboats.ModEntities;
import com.kishku7.lavaboats.client.LavaBoatLayers;

import %(modelpkg)s.BoatModel;
import net.minecraft.client.renderer.entity.BoatRenderer;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

public final class LavaBoatsNeoForgeClient {
    private LavaBoatsNeoForgeClient() {}

    public static void register(IEventBus modBus) {
        modBus.addListener(LavaBoatsNeoForgeClient::onRegisterLayers);
        modBus.addListener(LavaBoatsNeoForgeClient::onRegisterRenderers);
    }

    private static void onRegisterLayers(EntityRenderersEvent.RegisterLayerDefinitions event) {
%(layers)s
    }

    private static void onRegisterRenderers(EntityRenderersEvent.RegisterRenderers event) {
%(rend)s
    }
}''' % {"modelpkg": boat_model_pkg(ver), "layers": compat_core.LAYERS_EVENT, "rend": compat_core.RENDER_VANILLA_EVENT}
    for ln in body.split("\n"):
        cog.outl(ln)


