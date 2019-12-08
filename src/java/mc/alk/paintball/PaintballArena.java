package mc.alk.paintball;

import mc.alk.arena.objects.arenas.Arena;
import mc.alk.arena.objects.events.ArenaEventHandler;

import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.projectiles.ProjectileSource;

import java.util.Objects;

public class PaintballArena extends Arena {

	static boolean friendlyFire;
	static int damage = 20;

	/**
	 * This is how you create customized events.  You specify a method as a {@link ArenaEventHandler}
	 * and give it at least one bukkit event as the first argument.  In this case its EntityDamageEvent
	 * These events will ONLY be called when a match is ongoing
	 * If the event returns a player (in this case it does) then the event only gets called when
	 * 1) match is ongoing
	 * 2) player is still alive in the match
	 *
	 * @param event: Which bukkit event are we listening to
	 */
	@ArenaEventHandler(suppressCastWarnings = true)
	public void onEntityDamage(EntityDamageByEntityEvent event) {
		if (!(event.getEntity() instanceof Player) || event.getDamager().getType() != EntityType.SNOWBALL)
			return;

		ProjectileSource source = ((Projectile) event.getDamager()).getShooter();
		if (!(source instanceof Player))
			return;

		Player hit = (Player) event.getEntity();
		Player shooter = (Player) source;

		if (!friendlyFire)
			if (Objects.equals(getTeam(hit), getTeam(shooter)))
				return;

		event.setDamage(damage);
	}
}
