package cz.boosik.boosCooldown.Listeners;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerGameModeChangeEvent;

import util.boosChat;
import cz.boosik.boosCooldown.BoosConfigManager;
import cz.boosik.boosCooldown.BoosWarmUpManager;

/**
 * Poslucha� naslouchaj�c� ud�losti, kter� se spou�t� v okam�iku kdy hr�� zm�n�
 * hern� m�d. Pokud na p��kazech hr��e je aktivn� �asova� warmup, ve chv�li
 * spu�t�n� t�to ud�losti jsou v�echny jeho warmup �asova�e stornov�ny a hr��i
 * je odesl�na zpr�va, kter� ho o t�to skute�nosti informuje. Pokud hr��
 * disponuje opr�vn�n�m �booscooldowns.nocancel.gamemodechange�, jeho warmup
 * �asova�e stornov�ny nejsou.
 * 
 * @author Jakub Kol��
 * 
 */
public class BoosPlayerGameModeChangeListener implements Listener {
	/**
	 * Metoda zji��uje jestli je entita kter� spustila tuto ud�lost hr��. Pokud
	 * je entita hr��, hr�� nen� null a nedisponuje opr�vn�n�m
	 * booscooldowns.nocancel.gamemodechange a pokud tento hr�� disponuje aktivn�mi
	 * warmup �asova�i, pak je hr��i odesl�na zpr�va, kter� ho informuje o
	 * ukon�en� v�ech warmup �asova�� a n�sledn� tyto �asova�e ukon�uje pomoc�
	 * metody cancelWarmUps();.
	 * 
	 * @param event
	 *            ud�lost PlayerGameModeChangeEvent
	 */
	@EventHandler(priority = EventPriority.NORMAL)
	private void onPlayerGameModeChange(PlayerGameModeChangeEvent event) {
		if (event.isCancelled())
			return;

		Entity entity = event.getPlayer();
		if (entity != null && entity instanceof Player) {
			Player player = (Player) entity;
			if (player != null
					&& !player
							.hasPermission("booscooldowns.nocancel.gamemodechange")) {
				if (BoosWarmUpManager.hasWarmUps(player)) {
					boosChat.sendMessageToPlayer(player, BoosConfigManager
							.getCancelWarmupByGameModeChangeMessage());
					BoosWarmUpManager.cancelWarmUps(player);
				}

			}
		}
	}
}
