package me.odinclient

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import me.odinclient.dungeonmap.features.Dungeon
import me.odinclient.dungeonmap.features.MapRender
import me.odinclient.dungeonmap.features.Window
import me.odinclient.features.impl.dungeon.*
import me.odinclient.features.impl.floor7.AutoEdrag
import me.odinclient.features.impl.floor7.DioriteFucker
import me.odinclient.features.impl.floor7.RelicAura
import me.odinclient.features.impl.floor7.RelicPlacer
import me.odinclient.features.impl.floor7.p3.Arrows
import me.odinclient.features.impl.floor7.p3.CancelWrongTerms
import me.odinclient.features.impl.floor7.p3.HoverTerms
import me.odinclient.features.impl.floor7.p3.Levers
import me.odinclient.features.impl.render.ArrowTrajectory
import me.odinclient.features.impl.render.Ghosts
import me.odinclient.features.impl.render.NoCarpet
import me.odinclient.features.impl.render.NoDebuff
import me.odinclient.features.impl.skyblock.*
import me.odinmain.OdinMain
import me.odinmain.OdinMain.mc
import me.odinmain.commands.impl.*
import me.odinmain.config.Config
import me.odinmain.config.MiscConfig
import me.odinmain.config.WaypointConfig
import me.odinmain.events.EventDispatcher
import me.odinmain.features.Module
import me.odinmain.features.ModuleManager
import me.odinmain.features.impl.dungeon.KeyESP
import me.odinmain.features.impl.floor7.p3.ArrowAlign
import me.odinmain.features.impl.floor7.p3.SimonSays
import me.odinmain.features.impl.render.ClickGUIModule
import me.odinmain.features.impl.render.WaypointManager
import me.odinmain.ui.clickgui.ClickGUI
import me.odinmain.utils.ServerUtils
import me.odinmain.utils.clock.Executor
import me.odinmain.utils.render.world.RenderUtils
import me.odinmain.utils.skyblock.ChatUtils
import me.odinmain.utils.skyblock.LocationUtils
import me.odinmain.utils.skyblock.PlayerUtils
import me.odinmain.utils.skyblock.dungeon.DungeonUtils
import net.minecraft.command.ICommand
import net.minecraftforge.client.ClientCommandHandler
import net.minecraftforge.common.MinecraftForge
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.common.Mod.EventHandler
import net.minecraftforge.fml.common.event.FMLInitializationEvent
import net.minecraftforge.fml.common.event.FMLLoadCompleteEvent
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent
import net.minecraftforge.fml.common.gameevent.TickEvent
import java.io.File
import kotlin.coroutines.EmptyCoroutineContext

@Suppress("UNUSED_PARAMETER")
@Mod(
    modid = ModCore.MOD_ID,
    name = ModCore.NAME,
    version = ModCore.VERSION,
    clientSideOnly = true
)
class ModCore {
    @EventHandler
    fun init(event: FMLInitializationEvent) {

        Window.init()

        listOf(
            LocationUtils,
            ChatUtils,
            ServerUtils,
            PlayerUtils,
            RenderUtils,
            DungeonUtils,

            Dungeon,
            MapRender,

            EventDispatcher,

            Executor,
            ModuleManager,
            WaypointManager,
            this
        ).forEach {
            MinecraftForge.EVENT_BUS.register(it)
        }

        for (command in commandList) {
            ClientCommandHandler.instance.registerCommand(command as ICommand?)
        }
    }

    private val modules: ArrayList<Module> = arrayListOf(
        AutoGFS,
        AutoIceFill,
        AutoLeap,
        AutoMask,
        AutoSell,
        AutoShield,
        AutoUlt,
        AutoWish,
        CancelInteract,
        CloseChest,
        GhostPick,
        KeyESP,
        MapModule,
        SecretHitboxes,
        SecretTriggerbot,
        SuperBoom,
        SwapStonk,
        ThornStun,

        ArrowAlign,
        Arrows,
        CancelWrongTerms,
        HoverTerms,
        Levers,
        SimonSays,

        AutoEdrag,
        DioriteFucker,
        RelicAura,
        RelicPlacer,

        ArrowTrajectory,
        Ghosts,
        NoCarpet,
        NoDebuff,

        CookieClicker,
        EnchantingExperiments,
        FarmingHitboxes,
        LimboLeave,
        NoBlock,
        TermAC,
        Triggerbot
    )

    @EventHandler
    fun postInit(event: FMLPostInitializationEvent) = scope.launch(Dispatchers.IO) {

        val config = File(mc.mcDataDir, "config/odin")
        if (!config.exists()) {
            config.mkdirs()
        }

        launch {
            MiscConfig.loadConfig()
        }
        launch {
            WaypointConfig.loadConfig()
        }
    }

    @EventHandler
    fun loadComplete(event: FMLLoadCompleteEvent) = runBlocking {
        runBlocking {
            launch {
                Config.loadConfig()

                ClickGUIModule.firstTimeOnVersion = ClickGUIModule.lastSeenVersion != VERSION
                ClickGUIModule.lastSeenVersion = VERSION
            }
        }
        ClickGUI.init()
    }

    @SubscribeEvent
    fun onTick(event: TickEvent.ClientTickEvent) {
        if (event.phase != TickEvent.Phase.START) return

        if (Window.isVisible != Window.shouldShow) Window.isVisible = Window.shouldShow

        if (display != null) {
            mc.displayGuiScreen(display)
            display = null
        }
    }

    companion object {
        const val MOD_ID = "OdinClient"
        const val NAME = "OdinClient"
        const val VERSION = OdinMain.VERSION
        const val PREFIX = "§3Odin§bClient §8»§r"



        //var window = Window

        var display: GuiScreen? = null

        val scope = CoroutineScope(EmptyCoroutineContext)

        val commandList = arrayOf(
            mainCommand,
            termSimCommand,
            autoSellCommand,
            blacklistCommand,
            espCommand,
            WaypointCommand,
        )
    }
}