package net.cursedvalley.classes;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import java.util.function.BiConsumer;

/** Buyu mermisi: gercek bir varlik degil, ilerleyen bir nokta + parcacik izi. */
public final class Spell {

    /**
     * @param pierce true ise ilk hedefte durmaz (buz mizragi, alev dalgasi)
     * @param onHit  isabet eden canliya uygulanacak etki
     */
    public static void bolt(Plugin plugin, Player caster, Particle particle,
                            double range, double step, boolean pierce,
                            BiConsumer<Player, LivingEntity> onHit) {

        Location loc = caster.getEyeLocation().clone();
        Vector dir = loc.getDirection().normalize().multiply(step);

        new BukkitRunnable() {
            double travelled = 0;

            @Override public void run() {
                for (int i = 0; i < 2; i++) {                 // tick basina iki adim
                    loc.add(dir);
                    travelled += step;

                    loc.getWorld().spawnParticle(particle, loc, 6, 0.12, 0.12, 0.12, 0.01);

                    if (loc.getBlock().getType().isSolid() || travelled >= range) {
                        cancel();
                        return;
                    }
                    for (LivingEntity e : loc.getWorld().getNearbyLivingEntities(loc, 1.1)) {
                        if (e.equals(caster)) continue;
                        if (e instanceof Player pl && Parties.sameParty(caster, pl)) continue;
                        onHit.accept(caster, e);
                        if (!pierce) { cancel(); return; }
                    }
                }
            }
        }.runTaskTimer(plugin, 0L, 1L);
    }

    /** Buyu hasari: zirhi yok sayar (buyu hasari zirhla erimemeli). */
    public static void magicDamage(Player caster, LivingEntity target, double amount) {
        double hp = target.getHealth() - amount;
        target.damage(0.01, caster);                       // vurus animasyonu + hedefleme
        target.setHealth(Math.max(0, Math.min(hp, target.getAttribute(
                org.bukkit.attribute.Attribute.MAX_HEALTH).getValue())));
    }

    /** r=10 alansal etki. */
    public static void nova(Plugin plugin, Player caster, Particle particle, double radius,
                            BiConsumer<Player, LivingEntity> onHit) {
        Location c = caster.getLocation();
        c.getWorld().spawnParticle(particle, c.clone().add(0, 1, 0), 220, radius / 2, 1.2, radius / 2, 0.02);
        for (LivingEntity e : c.getWorld().getNearbyLivingEntities(c, radius, radius / 2, radius)) {
            if (e.equals(caster)) continue;
            if (e instanceof Player pl && Parties.sameParty(caster, pl)) continue;
            onHit.accept(caster, e);
        }
    }

    private Spell() {}
}
