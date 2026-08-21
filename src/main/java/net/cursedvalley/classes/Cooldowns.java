package net.cursedvalley.classes;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/** Yetenek bekleme sureleri. */
public final class Cooldowns {

    private static final Map<UUID, Map<String, Long>> MAP = new HashMap<>();

    public static boolean ready(Player p, String key) {
        Long t = MAP.getOrDefault(p.getUniqueId(), Map.of()).get(key);
        return t == null || System.currentTimeMillis() >= t;
    }

    public static int secondsLeft(Player p, String key) {
        Long t = MAP.getOrDefault(p.getUniqueId(), Map.of()).get(key);
        if (t == null) return 0;
        return (int) Math.max(0, (t - System.currentTimeMillis() + 999) / 1000);
    }

    public static void set(Player p, String key, int ticks) {
        if (ticks <= 0) return;
        MAP.computeIfAbsent(p.getUniqueId(), k -> new HashMap<>())
           .put(key, System.currentTimeMillis() + ticks * 50L);
    }

    public static void clear(Player p) {
        MAP.remove(p.getUniqueId());
    }

    public static void deny(Player p, String key) {
        p.sendActionBar(Component.text("Bekleme: " + secondsLeft(p, key) + " sn",
                NamedTextColor.RED));
    }

    private Cooldowns() {}
}
