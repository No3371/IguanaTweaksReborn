package insane96mcp.iguanatweaksreborn.event;

import insane96mcp.iguanatweaksreborn.IguanaTweaksReborn;
import insane96mcp.iguanatweaksreborn.modules.SleepRespawnModule;
import net.minecraftforge.event.entity.player.PlayerSleepInBedEvent;
import net.minecraftforge.event.world.SleepFinishedTimeEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = IguanaTweaksReborn.MOD_ID)
public class Sleep {

	@SubscribeEvent
	public static void wakeUp(SleepFinishedTimeEvent event) {
		SleepRespawnModule.wakeUpHungerAndEffects(event);
	}

	@SubscribeEvent
	public static void onSleep(PlayerSleepInBedEvent event) {
		SleepRespawnModule.tooHungryToSleep(event);
		SleepRespawnModule.disableSleeping(event);
	}
}
