"""compat.py -- Forge loader/version drift brain (lava-boats unified Forge 1.21.x tree).

Clusters (Forge 1.21 line; no 1.21.2, 1.21.9 gated beta; ceiling 1.21.11 via forge 61):
  legacy : 1.21, 1.21.1        pre-boat-refactor (custom subclasses -> converged to vanilla Boat + BoatDropMixin)
  eb6    : 1.21.3 - 1.21.5      classic EventBus (IEventBus, MinecraftForge.EVENT_BUS, @Mod.EventBusSubscriber client)
  eb7    : 1.21.6 - 1.21.11     EventBus 7.x (BusGroup, Event.getBus().addListener, constructor client register)

Two extra axes ride on top of the eb7 cluster (both self-derived from the NeoForge brain, which
already solved them, since Forge and NeoForge are BOTH mojmap-native at these versions):
  eb8    @1.21.10 (forge 60+)   Forge deprecated-for-removal the static Event.getBus(BusGroup) accessor
                                mid EventBus migration. The call still compiles + runs (1.21.10 jar boots),
                                and Forge has published no stable public replacement yet, so we keep it and
                                add a narrow documented @SuppressWarnings("removal") (D11 sanctioned form).
  rename @1.21.11               ResourceLocation->Identifier; vehicle.*->vehicle.boat.*; client model->model.object.boat
"""


import compat_core


def _vt(ver):
    return tuple(int(x) for x in ver.split("-")[0].split("."))


def is_legacy(ver):
    return _vt(ver) < (1, 21, 2)


def eb7(ver):
    return _vt(ver) >= (1, 21, 6)


def eb8(ver):
    # Forge 60+ (MC 1.21.10 and up): static Event.getBus(BusGroup) accessor deprecated for removal.
    return _vt(ver) >= (1, 21, 10)


def renamed(ver):
    # MC 1.21.11: mojmap ResourceLocation->Identifier, vehicle.*->vehicle.boat.*, client model pkg move.
    return _vt(ver) >= (1, 21, 11)


def id_type(ver):
    return "Identifier" if renamed(ver) else "ResourceLocation"


def boat_pkg(ver):
    return "net.minecraft.world.entity.vehicle.boat" if renamed(ver) \
        else "net.minecraft.world.entity.vehicle"


def boat_model_pkg(ver):
    return "net.minecraft.client.model.object.boat" if renamed(ver) \
        else "net.minecraft.client.model"


def cluster(ver):
    if is_legacy(ver):
        return "legacy"
    return "eb7" if eb7(ver) else "eb6"


def bus_type(ver):
    return "BusGroup" if eb7(ver) else "IEventBus"


def bus_import(ver):
    return "net.minecraftforge.eventbus.api.bus.BusGroup" if eb7(ver) \
        else "net.minecraftforge.eventbus.api.IEventBus"


def _emit(cog, lines):
    for ln in lines:
        cog.outl(ln)

# ---- ModEntities.register(busType) param ----
def emit_entities_register(cog, ver):
    cog.outl("public static void register({0} modBus) {{".format(bus_type(ver)))
    cog.outl("    ENTITIES.register(modBus);")
    cog.outl("}")


def emit_items_register_bus(cog, ver):
    cog.outl("public static void register({0} modBus) {{".format(bus_type(ver)))
    cog.outl("    ITEMS.register(modBus);")
    cog.outl("}")


# ---- entity registration body: byte-identical to NeoForge -> single source in compat_core (D14 dedup) ----
def emit_register_boat_body(cog, ver, kind):
    compat_core.emit_register_boat_body(cog, ver, kind)


# ---- item registration body ----
def emit_register_item_body(cog, ver):
    idt = id_type(ver)
    if is_legacy(ver):
        cog.outl("return ITEMS.register(name, () -> new LavaBoatItem(type, new Item.Properties().stacksTo(1).fireResistant()));")
    else:
        cog.outl("{0} id = {0}.fromNamespaceAndPath(LavaBoats.MOD_ID, name);".format(idt))
        cog.outl("ResourceKey<Item> key = ResourceKey.create(Registries.ITEM, id);")
        cog.outl("return ITEMS.register(name, () -> new BoatItem(type.get(), new Item.Properties().stacksTo(1).fireResistant().setId(key)));")


def boat_base_type(ver):
    # ModItems.register param supertype: ChestBoat extends Boat pre-1.21.2, AbstractBoat after
    return "Boat" if is_legacy(ver) else "AbstractBoat"

def recipes_unlock_arg(ver):
    # onPlayerJoin: player.awardRecipesByKey(<arg>). RECIPES is the unified List everywhere;
    # below 1.20.3 the vanilla method takes an ARRAY.
    if _vt(ver) < (1, 20, 3):
        return "LavaBoats.RECIPES.toArray(new ResourceLocation[0])"
    return "LavaBoats.RECIPES"


def unlock_extra_import(ver):
    return "import net.minecraft.resources.ResourceLocation;\n" if _vt(ver) < (1, 20, 3) else ""


def ctx_injected(ver):
    # Forge 49.2+ (MC 1.20.4) deprecates FMLJavaModLoadingContext.get() for removal (same 49.2
    # backport wave as the ResourceLocation factories); the mod constructor takes the context
    # as a parameter instead. 47-49.0 (1.20.1-1.20.3) keep .get().
    # EXCEPTION (boot-proven 2026-07-02): Forge 51.0.0 (the short-lived MC 1.21.0 line) predates
    # the injection backport wave -- NoSuchMethodException <init>() at CONSTRUCT. It also does not
    # deprecate .get(), so 1.21.0 stays on the classic form.
    if _vt(ver) == (1, 21):
        return False
    return _vt(ver) >= (1, 20, 4)


def ctor_arg(ver):
    return "FMLJavaModLoadingContext context" if ctx_injected(ver) else ""


def bus_expr(ver):
    return "context.getModEventBus()" if ctx_injected(ver) else "FMLJavaModLoadingContext.get().getModEventBus()"


# eb8 (forge 60+) narrow documented removal-suppression for the deprecated static getBus(BusGroup)
# accessor. One indented annotation line, or empty on eb7 (1.21.6-1.21.8, not yet deprecated).
def _removal_supp(ver):
    if eb8(ver):
        return ('    @SuppressWarnings("removal") // Forge 60+ deprecated the static Event.getBus(BusGroup) '
                'accessor mid EventBus migration; it still compiles + runs and Forge ships no stable '
                'replacement yet.\n')
    return ""

# ============ entrypoint + client: whole-class emission per cluster ============
_TABS = '''    private void onBuildTabs(BuildCreativeModeTabContentsEvent event) {
        if (event.getTabKey() == CreativeModeTabs.TOOLS_AND_UTILITIES) {
            ModItems.TAB_ITEMS.forEach(s -> event.accept(s.get()));
        }
    }'''


def emit_entrypoint_file(cog, ver):
    arg = recipes_unlock_arg(ver)
    if eb7(ver):
        body = '''import com.kishku7.lavaboats.LavaBoats;
import com.kishku7.lavaboats.ModEntities;
import com.kishku7.lavaboats.ModItems;
import com.kishku7.lavaboats.forge.client.LavaBoatsForgeClient;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.bus.BusGroup;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLEnvironment;

@Mod(LavaBoats.MOD_ID)
public class LavaBoatsForge {
%(supp)s    public LavaBoatsForge(FMLJavaModLoadingContext context) {
        BusGroup modBus = context.getModBusGroup();
        ModEntities.register(modBus);
        ModItems.register(modBus);
        BuildCreativeModeTabContentsEvent.getBus(modBus).addListener(this::onBuildTabs);
        PlayerEvent.PlayerLoggedInEvent.BUS.addListener(this::onPlayerJoin);
        if (FMLEnvironment.dist == Dist.CLIENT) {
            LavaBoatsForgeClient.register(modBus);
        }
    }
%(tabs)s
    private void onPlayerJoin(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            player.awardRecipesByKey(%(arg)s);
        }
    }
}''' % {"tabs": _TABS, "arg": arg, "supp": _removal_supp(ver)}
    else:
        body = '''import com.kishku7.lavaboats.LavaBoats;
import com.kishku7.lavaboats.ModEntities;
import com.kishku7.lavaboats.ModItems;

import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.CreativeModeTabs;
%(rlimp)simport net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(LavaBoats.MOD_ID)
public class LavaBoatsForge {
    public LavaBoatsForge(%(ctorarg)s) {
        IEventBus modBus = %(busexpr)s;
        ModEntities.register(modBus);
        ModItems.register(modBus);
        modBus.addListener(this::onBuildTabs);
        MinecraftForge.EVENT_BUS.addListener(this::onPlayerJoin);
    }
%(tabs)s
    private void onPlayerJoin(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) {
            player.awardRecipesByKey(%(arg)s);
        }
    }
}''' % {"tabs": _TABS, "arg": arg, "rlimp": unlock_extra_import(ver),
         "ctorarg": ctor_arg(ver), "busexpr": bus_expr(ver)}
    for ln in body.split("\n"):
        cog.outl(ln)


def emit_client_file(cog, ver):
    if is_legacy(ver):
        body = '''import com.kishku7.lavaboats.LavaBoats;
import com.kishku7.lavaboats.ModEntities;
import com.kishku7.lavaboats.client.LavaBoatRenderer;

import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = LavaBoats.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class LavaBoatsForgeClient {
    private LavaBoatsForgeClient() {}

    @SubscribeEvent
    public static void onRegisterRenderers(EntityRenderersEvent.RegisterRenderers event) {
%(render)s
    }
}''' % {"render": compat_core.RENDER_LEGACY_EVENT}
    elif eb7(ver):
        body = '''import com.kishku7.lavaboats.ModEntities;
import com.kishku7.lavaboats.client.LavaBoatLayers;

import %(modelpkg)s.BoatModel;
import net.minecraft.client.renderer.entity.BoatRenderer;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.bus.BusGroup;

public final class LavaBoatsForgeClient {
    private LavaBoatsForgeClient() {}

%(supp)s    public static void register(BusGroup modBus) {
        EntityRenderersEvent.RegisterLayerDefinitions.getBus(modBus).addListener(LavaBoatsForgeClient::onRegisterLayers);
        EntityRenderersEvent.RegisterRenderers.getBus(modBus).addListener(LavaBoatsForgeClient::onRegisterRenderers);
    }

    private static void onRegisterLayers(EntityRenderersEvent.RegisterLayerDefinitions event) {
%(layers)s
    }

    private static void onRegisterRenderers(EntityRenderersEvent.RegisterRenderers event) {
%(render)s
    }
}''' % {"modelpkg": boat_model_pkg(ver), "supp": _removal_supp(ver),
         "layers": compat_core.LAYERS_EVENT, "render": compat_core.RENDER_VANILLA_EVENT}
    else:
        body = '''import com.kishku7.lavaboats.LavaBoats;
import com.kishku7.lavaboats.ModEntities;
import com.kishku7.lavaboats.client.LavaBoatLayers;

import net.minecraft.client.model.BoatModel;
import net.minecraft.client.renderer.entity.BoatRenderer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.EntityRenderersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = LavaBoats.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public final class LavaBoatsForgeClient {
    private LavaBoatsForgeClient() {}

    @SubscribeEvent
    public static void onRegisterLayers(EntityRenderersEvent.RegisterLayerDefinitions event) {
%(layers)s
    }

    @SubscribeEvent
    public static void onRegisterRenderers(EntityRenderersEvent.RegisterRenderers event) {
%(render)s
    }
}''' % {"layers": compat_core.LAYERS_EVENT, "render": compat_core.RENDER_VANILLA_EVENT}
    for ln in body.split("\n"):
        cog.outl(ln)
