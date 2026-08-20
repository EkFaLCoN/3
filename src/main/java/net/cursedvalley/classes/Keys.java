package net.cursedvalley.classes;

import org.bukkit.NamespacedKey;
import org.bukkit.plugin.Plugin;

/** Esyalarda kullanilan PDC anahtarlari. */
public final class Keys {
    public static NamespacedKey WEAPON;   // silah id'si (string)
    public static NamespacedKey LEVEL;    // seviye (int)
    public static NamespacedKey XP;       // oldurme sayaci (int)
    public static NamespacedKey OWNER;    // sahibi (uuid string) - sadece bilgi

    public static void init(Plugin p) {
        WEAPON = new NamespacedKey(p, "weapon");
        LEVEL  = new NamespacedKey(p, "level");
        XP     = new NamespacedKey(p, "xp");
        OWNER  = new NamespacedKey(p, "owner");
    }

    private Keys() {}
}
