package net.cursedvalley.classes;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * Silah seviyesi.
 *
 * Ilerleme = oldurulen yaratik/oyuncu sayisi. Yaratik 1, oyuncu 10 sayilir.
 * Kalkan icin: engellenen her 20 hasar 1 sayilir.
 */
public final class Levels {

    /** Bir sonraki seviyeye gecmek icin toplam gereken puan (seviye 1..9 icin). */
    private static final int[] NEED = {25, 60, 120, 200, 320, 480, 700, 1000, 1400};

    public static int needed(int level) {
        if (level < 1) level = 1;
        if (level > NEED.length) return Integer.MAX_VALUE;
        return NEED[level - 1];
    }

    /** Puan ekler, gerekiyorsa seviye atlatir. Seviye atladiysa true doner. */
    public static boolean gain(Player p, ItemStack it, int amount) {
        WeaponType w = Items.typeOf(it);
        if (w == null || amount <= 0) return false;

        int lvl = Items.levelOf(it);
        if (lvl >= w.maxLevel()) return false;

        int xp = Items.xpOf(it) + amount;
        boolean up = false;

        while (lvl < w.maxLevel() && xp >= needed(lvl)) {
            xp -= needed(lvl);
            lvl++;
            up = true;
            announce(p, w, lvl);
        }
        Items.setProgress(it, lvl, xp);
        return up;
    }

    private static void announce(Player p, WeaponType w, int lvl) {
        p.playSound(p.getLocation(), Sound.UI_TOAST_CHALLENGE_COMPLETE, 1f, 1.2f);
        p.sendMessage(Component.text(w.tr() + " seviye " + lvl + "!", NamedTextColor.GOLD));

        for (Ability a : Abilities.of(w)) {
            if (a.level() == lvl) {
                p.sendMessage(Component.text("  Yeni yetenek: " + a.name()
                        + "  (" + a.combo().tr() + ")", NamedTextColor.GREEN));
            }
        }
        if (lvl >= w.maxLevel() && w.maxLevel() >= 10) {
            String tag = "usta_" + w.id();
            p.addScoreboardTag(tag);
            p.sendMessage(Component.text("USTA unvani kazandin: " + tag, NamedTextColor.LIGHT_PURPLE));
            p.getServer().broadcast(Component.text(
                    p.getName() + " " + w.tr() + " silahini USTA seviyesine cikardi!",
                    NamedTextColor.LIGHT_PURPLE));
        }
    }

    private Levels() {}
}
