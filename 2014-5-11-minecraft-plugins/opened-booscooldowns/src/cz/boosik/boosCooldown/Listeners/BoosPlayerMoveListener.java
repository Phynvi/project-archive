package cz.boosik.boosCooldown.Listeners;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import util.boosChat;
import cz.boosik.boosCooldown.BoosConfigManager;
import cz.boosik.boosCooldown.BoosWarmUpManager;

/**
 * Poslucha� naslouchaj�c� ud�losti, kter� se spou�t� v okam�iku kdy se hr��
 * pohybuje. Pokud na p��kazech hr��e je aktivn� �asova� warmup, ve chv�li
 * spu�t�n� t�to ud�losti jsou v�echny jeho warmup �asova�e stornov�ny a hr��i
 * je odesl�na zpr�va, kter� ho o t�to skute�nosti informuje. P�i pohybech hr��e
 * hra tuto ud�lost spou�t� 20 kr�t ka�dou vte�inu. To m��e b�t na velk�ch
 * serverech velmi neoptim�ln� a proto se funkce spou�t� jen jednou za vte�inu,
 * pomoc� jednoduch�ho cyklu. Pokud hr�� disponuje opr�vn�n�m
 * �booscooldowns.nocancel.move�, jeho warmup �asova�e stornov�ny nejsou.
 * 
 * @author Jakub Kol��
 * 
 */
public class BoosPlayerMoveListener implements Listener {
	/**
	 * Metoda zji��uj�c�, jestli hr�� zm�nil pozico ve sv�t�, nebo p��padn� sv�t
	 * od doby kdy pou�il p��kaz a� do doby vol�n� t�to metody.
	 * 
	 * @param player
	 *            hr�� kter� se pohybuje
	 * @return true pokud hr�� zm�nil svou pozici, nebo pokud p�e�el do jin�ho
	 *         sv�ta; jinak vrac� false
	 */
	private static boolean hasMoved(Player player) {
		String curworld = player.getWorld().getName();
		String cmdworld = BoosWarmUpManager.getPlayerworld().get(player);
		Location curloc = player.getLocation();
		Location cmdloc = BoosWarmUpManager.getPlayerloc().get(player);
		if (!curworld.equals(cmdworld)) {
			return true;
		} else if (cmdloc.distanceSquared(curloc) > 2) {
			return true;
		}

		return false;
	}

	private int tempTimer = 0;

	/**
	 * Pokud hr�� nen� null a nedisponuje opr�vn�n�m
	 * booscooldowns.nocancel.move a pokud tento hr�� disponuje aktivn�mi
	 * warmup �asova�i, pak je hr��i odesl�na zpr�va, kter� ho informuje o
	 * ukon�en� v�ech warmup �asova�� a n�sledn� tyto �asova�e ukon�uje pomoc�
	 * metody cancelWarmUps();. Metoda obsahuje jednoduch� �asova�, kter�
	 * zaji��uje, �e funkce nebudou prov�d�ny p�i ka�d�m vol�n� t�to metody (20x
	 * za vte�inu).
	 * 
	 * @param event
	 *            ud�lost PlayerMoveEvent
	 */
	@EventHandler(priority = EventPriority.NORMAL)
	private void onPlayerMove(PlayerMoveEvent event) {
		if (event.isCancelled()) {
			return;
		}
		if (tempTimer < 20) {
			tempTimer = tempTimer + 1;
			return;
		} else {
			Player player = event.getPlayer();
			if (player != null
					&& !player.hasPermission("booscooldowns.nocancel.move")) {
				if (BoosWarmUpManager.hasWarmUps(player) && hasMoved(player)) {
					BoosWarmUpManager.clearLocWorld(player);
					boosChat.sendMessageToPlayer(player,
							BoosConfigManager.getWarmUpCancelledByMoveMessage());
					BoosWarmUpManager.cancelWarmUps(player);
				}
			}
			tempTimer = 0;
		}
	}
}