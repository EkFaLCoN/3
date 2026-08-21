package net.cursedvalley.classes;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.util.*;

/** Aktif yeteneklerin uygulanmasi. */
public final class Cast {

    /** Gecici durumlar (tick sayaci ile). */
    public static final Map<UUID, Integer> FRENZY = new HashMap<>();       // cinnet
    public static final Map<UUID, Integer> ARMOR_BREAK = new HashMap<>();  // zirh kirici (vurus sayisi)
    public static final Map<UUID, Integer> INVIS = new HashMap<>();        // gizlenme
    public static final Map<UUID, Integer> IMMUNE = new HashMap<>();       // dokunulmazlik
    public static final Map<UUID, Integer> CROSSBOW_RAGE = new HashMap<>();// cilgin atis
    public static final Map<UUID, Integer> STORM_CHARGE = new HashMap<>(); // sarj
    public static final Map<UUID, Integer> FIVE_SHOT = new HashMap<>();    // besli ok
    public static final Map<UUID, Integer> EXECUTE_SHOT = new HashMap<>(); // infaz oku
    public static final Map<UUID, Integer> PIERCE_SHOT = new HashMap<>();  // delik desik
    public static final Map<UUID, Location> DASH_BACK = new HashMap<>();   // geri donus

    public static boolean run(CursedClasses plugin, Player p, ItemStack item,
                              WeaponType w, Ability a) {
        World world = p.getWorld();
        Location eye = p.getEyeLocation();

        switch (a.id()) {

            // ---------------- SAVASCI: balta ----------------
            case "cinnet" -> {
                FRENZY.put(p.getUniqueId(), 200);
                p.addPotionEffect(new PotionEffect(PotionEffectType.STRENGTH, 200, 1, false, true));
                world.playSound(eye, Sound.ENTITY_RAVAGER_ROAR, 1.4f, 0.7f);
                world.spawnParticle(Particle.ANGRY_VILLAGER, p.getLocation().add(0, 2, 0), 12, .4, .4, .4);
                msg(p, "CINNET! Savunman dustu, vurusun agirlasti.");
            }
            case "zirhkirici" -> {
                ARMOR_BREAK.put(p.getUniqueId(), 3);
                world.playSound(eye, Sound.ITEM_TRIDENT_RETURN, 1f, 0.6f);
                msg(p, "Sonraki 3 vurus zirhi delip gecer.");
            }
            case "nara" -> {
                world.playSound(eye, Sound.ENTITY_WARDEN_ROAR, 1.6f, 0.8f);
                for (LivingEntity e : nearby(p, 6)) {
                    e.setVelocity(e.getLocation().toVector().subtract(p.getLocation().toVector())
                            .normalize().multiply(1.2).setY(0.45));
                    e.addPotionEffect(new PotionEffect(PotionEffectType.WEAKNESS, 80, 1));
                }
            }
            case "kasirga" -> {
                world.playSound(eye, Sound.ENTITY_PLAYER_ATTACK_SWEEP, 1.4f, 0.7f);
                for (int i = 0; i < 24; i++) {
                    double ang = Math.PI * 2 * i / 24.0;
                    world.spawnParticle(Particle.SWEEP_ATTACK,
                            p.getLocation().add(Math.cos(ang) * 3, 1, Math.sin(ang) * 3), 1);
                }
                for (LivingEntity e : nearby(p, 4)) e.damage(12, p);
            }

            // ---------------- SAVASCI: tirpan ----------------
            case "cekis" -> {
                LivingEntity t = target(p, 20);
                if (t == null) { msg(p, "Hedef yok."); return false; }
                t.setVelocity(p.getLocation().toVector().subtract(t.getLocation().toVector())
                        .normalize().multiply(1.4).setY(0.35));
                world.playSound(eye, Sound.ENTITY_ENDERMAN_TELEPORT, 1f, 0.6f);
            }
            case "zincir" -> {
                int n = 0;
                for (LivingEntity e : nearby(p, 8)) {
                    if (n++ >= 3) break;
                    e.setVelocity(p.getLocation().toVector().subtract(e.getLocation().toVector())
                            .normalize().multiply(1.1).setY(0.3));
                }
                world.playSound(eye, Sound.ENTITY_ENDERMAN_TELEPORT, 1f, 0.5f);
            }
            case "korku" -> {
                for (LivingEntity e : nearby(p, 5)) {
                    if (e instanceof Mob mob) {
                        mob.setTarget(null);
                        mob.setVelocity(e.getLocation().toVector().subtract(p.getLocation().toVector())
                                .normalize().multiply(0.9));
                    }
                    e.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 100, 1));
                }
                world.playSound(eye, Sound.ENTITY_GHAST_SCREAM, 1.2f, 0.5f);
            }
            case "hasat" -> Spell.bolt(plugin, p, Particle.SOUL, 14, 0.6, true, (c, e) -> {
                e.damage(10, c);
                e.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 60, 0));
            });

            // ---------------- SAMAN ----------------
            case "sok" -> {
                for (LivingEntity e : nearby(p, 5))
                    e.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 60, 2));
                world.playSound(eye, Sound.ITEM_MACE_SMASH_GROUND, 1.4f, 0.8f);
            }
            case "arinma" -> {
                for (PotionEffect e : new ArrayList<>(p.getActivePotionEffects())) {
                    if (isBad(e.getType())) p.removePotionEffect(e.getType());
                }
                p.setFireTicks(0);
                world.spawnParticle(Particle.END_ROD, p.getLocation().add(0, 1, 0), 40, .5, .8, .5, .02);
                msg(p, "Arindin.");
            }
            case "yargi" -> {
                LivingEntity t = target(p, 24);
                if (t == null) { msg(p, "Hedef yok."); return false; }
                world.strikeLightningEffect(t.getLocation());
                t.damage(10, p);
            }
            case "sifa" -> {
                LivingEntity t = target(p, 24);
                Player heal = (t instanceof Player pl && Parties.sameParty(p, pl)) ? pl : p;
                double amount = 8 + (Abilities.has(w, Items.levelOf(item), "ruhsal") ? 2 : 0);
                heal(heal, amount);
                if (Abilities.has(w, Items.levelOf(item), "canlandirma"))
                    heal.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 60, 1));
                world.spawnParticle(Particle.HEART, heal.getLocation().add(0, 2, 0), 8, .4, .4, .4);
                world.playSound(heal.getLocation(), Sound.BLOCK_BEACON_ACTIVATE, 1f, 1.6f);
                msg(p, heal.getName() + " iyilestirildi.");
            }
            case "toplu" -> {
                for (Player m : Parties.online(p)) {
                    if (m.getLocation().distance(p.getLocation()) <= 8) {
                        heal(m, 6);
                        m.getWorld().spawnParticle(Particle.HEART, m.getLocation().add(0, 2, 0), 6, .4, .4, .4);
                    }
                }
            }
            case "kalkanruhu" -> {
                LivingEntity t = target(p, 20);
                Player tgt = (t instanceof Player pl && Parties.sameParty(p, pl)) ? pl : p;
                tgt.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, 100, 1));
                msg(p, tgt.getName() + " korumaya alindi.");
            }
            case "diriltme" -> {
                for (Player m : Parties.online(p)) {
                    if (m.getHealth() <= 1.0 && m.getLocation().distance(p.getLocation()) <= 10) {
                        heal(m, 10);
                        m.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, 100, 2));
                        msg(p, m.getName() + " ayaga kaldirildi.");
                        return true;
                    }
                }
                msg(p, "Yakinda dusmus parti uyesi yok.");
                return false;
            }

            // ---------------- SAMAN: kalkan ----------------
            case "dokunulmaz" -> {
                IMMUNE.put(p.getUniqueId(), 40);
                world.spawnParticle(Particle.END_ROD, p.getLocation().add(0, 1, 0), 60, .6, 1, .6, .03);
                world.playSound(eye, Sound.ITEM_TOTEM_USE, 1f, 1.4f);
                msg(p, "2 saniye dokunulmazsin.");
            }
            case "sarsinti" -> {
                for (LivingEntity e : nearby(p, 4))
                    e.setVelocity(e.getLocation().toVector().subtract(p.getLocation().toVector())
                            .normalize().multiply(1.4).setY(0.5));
                world.playSound(eye, Sound.ENTITY_IRON_GOLEM_ATTACK, 1.4f, 0.7f);
            }
            case "kutsalduvar" -> {
                for (Player m : Parties.online(p)) {
                    if (m.getLocation().distance(p.getLocation()) <= 6) {
                        IMMUNE.put(m.getUniqueId(), 40);
                        m.getWorld().spawnParticle(Particle.END_ROD, m.getLocation().add(0, 1, 0), 40, .5, 1, .5, .02);
                    }
                }
                msg(p, "Kutsal duvar!");
            }

            // ---------------- SUIKASTCI ----------------
            case "gizlen" -> {
                INVIS.put(p.getUniqueId(), 600);
                p.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 600, 0, false, false));
                for (Player o : Bukkit.getOnlinePlayers()) if (!o.equals(p)) o.hidePlayer(plugin, p);
                world.playSound(eye, Sound.ENTITY_ILLUSIONER_PREPARE_MIRROR, 1f, 1.2f);
                msg(p, "Golgelere karistin (30 sn). Saldirirsan bozulur.");
            }
            case "golge" -> {
                LivingEntity t = target(p, 20);
                if (t == null) { msg(p, "Hedef yok."); return false; }
                Location behind = t.getLocation().clone()
                        .subtract(t.getLocation().getDirection().normalize().multiply(1.4));
                behind.setYaw(t.getLocation().getYaw());
                p.teleport(behind);
                world.playSound(behind, Sound.ENTITY_ENDERMAN_TELEPORT, 1f, 1.4f);
            }
            case "atilim" -> {
                DASH_BACK.put(p.getUniqueId(), p.getLocation().clone());
                p.setVelocity(p.getLocation().getDirection().normalize().multiply(2.2).setY(0.25));
                world.playSound(eye, Sound.ENTITY_BREEZE_SHOOT, 1f, 1.3f);
            }
            case "geridonus" -> {
                Location back = DASH_BACK.remove(p.getUniqueId());
                if (back == null) { msg(p, "Donulecek nokta yok."); return false; }
                p.teleport(back);
                world.playSound(back, Sound.ENTITY_ENDERMAN_TELEPORT, 1f, 0.9f);
            }
            case "infaz" -> {
                LivingEntity t = target(p, 6);
                if (t == null) { msg(p, "Hedef yok."); return false; }
                double ratio = t.getHealth() / t.getAttribute(Attribute_MAX()).getValue();
                t.damage(ratio < 0.35 ? 24 : 12, p);
                world.playSound(eye, Sound.ENTITY_PLAYER_ATTACK_CRIT, 1.2f, 0.8f);
            }

            // ---------------- BUYUCU ----------------
            case "isik" -> Spell.bolt(plugin, p, Particle.END_ROD, 30, 0.7, false, (c, e) -> {
                Spell.magicDamage(c, e, 5);
                e.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 60, 0));
            });
            case "isiknova" -> Spell.nova(plugin, p, Particle.END_ROD, 10, (c, e) -> {
                Spell.magicDamage(c, e, 3);
                e.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 60, 0));
            });
            case "buztopu" -> Spell.bolt(plugin, p, Particle.SNOWFLAKE, 30, 0.7, false, (c, e) -> {
                Spell.magicDamage(c, e, 7 + bonus(item, "buzzirhi"));
                e.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 60, 1));
            });
            case "donma" -> {
                LivingEntity t = target(p, 20);
                if (t == null) { msg(p, "Hedef yok."); return false; }
                t.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 40, 6));
                t.addPotionEffect(new PotionEffect(PotionEffectType.JUMP_BOOST, 40, 128));
                t.getWorld().spawnParticle(Particle.SNOWFLAKE, t.getLocation().add(0, 1, 0), 40, .4, .8, .4, .02);
            }
            case "buzmizrak" -> Spell.bolt(plugin, p, Particle.SNOWFLAKE, 24, 0.8, true,
                    (c, e) -> Spell.magicDamage(c, e, 9));
            case "buznova" -> Spell.nova(plugin, p, Particle.SNOWFLAKE, 10, (c, e) -> {
                Spell.magicDamage(c, e, 5);
                e.addPotionEffect(new PotionEffect(PotionEffectType.SLOWNESS, 80, 2));
            });
            case "atestopu" -> Spell.bolt(plugin, p, Particle.FLAME, 30, 0.7, false, (c, e) -> {
                Spell.magicDamage(c, e, 10 + bonus(item, "kor"));
                e.setFireTicks(100);
            });
            case "alevkalkan" -> {
                p.addPotionEffect(new PotionEffect(PotionEffectType.FIRE_RESISTANCE, 100, 0));
                msg(p, "5 saniye ates hasari almazsin.");
            }
            case "alevdalga" -> Spell.bolt(plugin, p, Particle.FLAME, 20, 0.8, true, (c, e) -> {
                Spell.magicDamage(c, e, 8);
                e.setFireTicks(80);
            });
            case "atesnova" -> Spell.nova(plugin, p, Particle.FLAME, 10, (c, e) -> {
                Spell.magicDamage(c, e, 7);
                e.setFireTicks(100);
            });
            case "yildirimtopu" -> Spell.bolt(plugin, p, Particle.ELECTRIC_SPARK, 30, 0.8, false, (c, e) -> {
                Spell.magicDamage(c, e, 6 + bonus(item, "gerilim"));
                int chance = STORM_CHARGE.getOrDefault(c.getUniqueId(), 0) > 0 ? 25 : 5;
                if (ThreadLocal_rand() < chance) {
                    // Yildirim bir sonraki tick'te dusurulur: hasar olayinin
                    // ortasinda dogrudan cagirinca sunucu bunu yutuyordu.
                    Location at = e.getLocation().clone();
                    Bukkit.getScheduler().runTask(plugin, () -> {
                        at.getWorld().strikeLightning(at);
                        for (LivingEntity le : at.getWorld().getNearbyLivingEntities(at, 2.0)) {
                            if (le.equals(c)) continue;
                            Spell.magicDamage(c, le, 8);
                            le.setFireTicks(60);
                        }
                    });
                    Cast.msg(c, "Yildirim carpti!");
                }
            });
            case "sarj" -> {
                STORM_CHARGE.put(p.getUniqueId(), 160);
                msg(p, "Sarj edildin: yildirim ihtimali %25 (8 sn).");
            }
            case "zincirsimsek" -> {
                int n = 0;
                for (LivingEntity e : nearby(p, 14)) {
                    if (n++ >= 3) break;
                    e.getWorld().strikeLightningEffect(e.getLocation());
                    Spell.magicDamage(p, e, 7);
                }
            }
            case "yildirimnova" -> Spell.nova(plugin, p, Particle.ELECTRIC_SPARK, 10, (c, e) -> {
                e.getWorld().strikeLightningEffect(e.getLocation());
                Spell.magicDamage(c, e, 6);
            });

            // ---------------- OKCU ----------------
            case "besli" -> {
                FIVE_SHOT.put(p.getUniqueId(), 200);
                msg(p, "Sonraki atis 5 ok.");
            }
            case "geriadim" -> {
                p.setVelocity(p.getLocation().getDirection().normalize().multiply(-1.4).setY(0.35));
                p.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 60, 1));
            }
            case "yagmur" -> {
                Location t = p.getTargetBlockExact(40) != null
                        ? p.getTargetBlockExact(40).getLocation().add(0.5, 0, 0.5)
                        : p.getLocation().add(p.getLocation().getDirection().multiply(15));
                for (int i = 0; i < 12; i++) {
                    final int k = i;
                    Bukkit.getScheduler().runTaskLater(plugin, () -> {
                        Location sp = t.clone().add(rand(3), 14, rand(3));
                        Arrow ar = world.spawn(sp, Arrow.class);
                        ar.setShooter(p);
                        ar.setVelocity(new Vector(0, -2.2, 0));
                        ar.setDamage(4);
                    }, k * 2L);
                }
                world.playSound(eye, Sound.ENTITY_ARROW_SHOOT, 1.2f, 0.8f);
            }
            case "cilgin" -> {
                CROSSBOW_RAGE.put(p.getUniqueId(), 100);
                p.addPotionEffect(new PotionEffect(PotionEffectType.HASTE, 100, 9, false, false));

                // Arbaletin kurma suresini KISALTAN tek sey Hizli Kurma buyusudur;
                // Hiz/Acele etkilerinin arbalete etkisi yok. Bu yuzden yetenek
                // suresince gecici olarak ekleniyor.
                item.addUnsafeEnchantment(org.bukkit.enchantments.Enchantment.QUICK_CHARGE, 5);
                final ItemStack ref = item;
                org.bukkit.Bukkit.getScheduler().runTaskLater(plugin, () -> {
                    ref.removeEnchantment(org.bukkit.enchantments.Enchantment.QUICK_CHARGE);
                }, 100L);

                world.playSound(eye, Sound.ITEM_CROSSBOW_QUICK_CHARGE_3, 1.2f, 1.4f);
                msg(p, "Cilgin Atis: 5 saniye!");
            }
            case "delikdesik" -> {
                PIERCE_SHOT.put(p.getUniqueId(), 3);
                msg(p, "Sonraki 3 ok zirhi delip gecer.");
            }
            case "infazoku" -> {
                EXECUTE_SHOT.put(p.getUniqueId(), 1);
                msg(p, "Infaz Oku hazir.");
            }

            default -> {
                msg(p, a.name() + " pasif bir yetenek.");
                return false;
            }
        }
        return true;
    }

    // ---------------- yardimcilar ----------------

    private static org.bukkit.attribute.Attribute Attribute_MAX() {
        return org.bukkit.attribute.Attribute.MAX_HEALTH;
    }

    private static int ThreadLocal_rand() {
        return java.util.concurrent.ThreadLocalRandom.current().nextInt(100);
    }

    private static double rand(double r) {
        return java.util.concurrent.ThreadLocalRandom.current().nextDouble(-r, r);
    }

    private static int bonus(ItemStack it, String abilityId) {
        WeaponType w = Items.typeOf(it);
        return (w != null && Abilities.has(w, Items.levelOf(it), abilityId)) ? 2 : 0;
    }

    public static void heal(LivingEntity e, double amount) {
        double max = e.getAttribute(Attribute_MAX()).getValue();
        e.setHealth(Math.min(max, e.getHealth() + amount));
    }

    public static List<LivingEntity> nearby(Player p, double r) {
        List<LivingEntity> out = new ArrayList<>();
        for (LivingEntity e : p.getWorld().getNearbyLivingEntities(p.getLocation(), r)) {
            if (e.equals(p)) continue;
            if (e instanceof Player pl && Parties.sameParty(p, pl)) continue;
            out.add(e);
        }
        return out;
    }

    /** Bakilan yondeki ilk canli. */
    public static LivingEntity target(Player p, double range) {
        var res = p.getWorld().rayTraceEntities(p.getEyeLocation(),
                p.getEyeLocation().getDirection(), range, 1.2,
                e -> e instanceof LivingEntity && !e.equals(p));
        return (res != null && res.getHitEntity() instanceof LivingEntity le) ? le : null;
    }

    private static boolean isBad(PotionEffectType t) {
        return t == PotionEffectType.POISON || t == PotionEffectType.WITHER
                || t == PotionEffectType.SLOWNESS || t == PotionEffectType.WEAKNESS
                || t == PotionEffectType.BLINDNESS || t == PotionEffectType.NAUSEA
                || t == PotionEffectType.MINING_FATIGUE || t == PotionEffectType.LEVITATION;
    }

    public static void msg(Player p, String s) {
        p.sendActionBar(Component.text(s, NamedTextColor.GOLD));
    }

    private Cast() {}
}
