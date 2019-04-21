package cz.boosik.boosCooldown.Listeners;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

import util.boosChat;
import cz.boosik.boosCooldown.BoosConfigManager;
import cz.boosik.boosCooldown.BoosWarmUpManager;

/**
 * Poslucha� naslouchaj�c� ud�losti, kter� se spou�t� v okam�iku kdy hr��
 * prov�d� interakce s hern�mi bloky. Pokud na p��kazech hr��e je aktivn�
 * �asova� warmup a blok se kter�m hr�� interaguje je kontejner, pak tato t��da
 * pot� ukon�uje tuto ud�lost a blokuje hr��i p��stup do kontejner�. Pokud hr��
 * disponuje opr�vn�n�m �booscooldowns.dontblock.interact�, jeho p��stup do
 * kontejner� nen� blokov�n.
 * 
 * @author Jakub Kol��
 * 
 */
public class BoosPlayerInteractListener implements Listener {
	/**
	 * Metoda zji��uje jestli je entita kter� spustila tuto ud�lost hr��. Pokud
	 * je entita hr��, hr�� nen� null a nedisponuje opr�vn�n�m
	 * booscooldowns.dontblock.interact a pokud tento hr�� disponuje aktivn�mi
	 * warmup �asova�i a blok se kter�m interaguje je kontejner, pak je hr��i
	 * odesl�na zpr�va, kter� ho informuje o tom, �e do kontejneru nen� mo�n�
	 * p�istupovat a tato ud�lost je n�sledn� ukon�ena. To zablokuje hr��i
	 * p��stup do kontejneru.
	 * 
	 * @param event
	 *            ud�lost PlayerInteractEvent
	 */
	@EventHandler(priority = EventPriority.NORMAL)
	private void onPlayerInteract(PlayerInteractEvent event) {
		if (event.isCancelled())
			return;

		Entity entity = event.getPlayer();
		if (entity != null && entity instanceof Player) {
			Player player = (Player) entity;
			if (player != null
					&& !player
							.hasPermission("booscooldowns.dontblock.interact")) {
				if (BoosWarmUpManager.hasWarmUps(player)) {
					if (event.getClickedBlock().getType().name()
							.equals("CHEST")
							|| event.getClickedBlock().getType().name()
									.equals("FURNACE")
							|| event.getClickedBlock().getType().name()
									.equals("BURNING_FURNACE")
							|| event.getClickedBlock().getType().name()
									.equals("WORKBENCH")
							|| event.getClickedBlock().getType().name()
									.equals("DISPENSER")
							|| event.getClickedBlock().getType().name()
									.equals("JUKEBOX")
							|| event.getClickedBlock().getType().name()
									.equals("LOCKED_CHEST")
							|| event.getClickedBlock().getType().name()
									.equals("ENCHANTMENT_TABLE")
							|| event.getClickedBlock().getType().name()
									.equals("BREWING_STAND")
							|| event.getClickedBlock().getType().name()
									.equals("CAULDRON")
							|| event.getClickedBlock().getType().name()
									.equals("STORAGE_MINECART")
							|| event.getClickedBlock().getType().name()
									.equals("TRAPPED_CHEST")
							|| event.getClickedBlock().getType().name()
									.equals("DROPPER")
							|| event.getClickedBlock().getType().name()
									.equals("HOPPER")) {
						event.setCancelled(true);
						boosChat.sendMessageToPlayer(player,
								BoosConfigManager.getInteractBlockedMessage());
					}
				}

			}
		}
	}
}
