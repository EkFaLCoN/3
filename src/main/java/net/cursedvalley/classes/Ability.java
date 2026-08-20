package net.cursedvalley.classes;

/**
 * Bir silahin yetenegi.
 *
 * @param level   acildigi seviye
 * @param id      kod icindeki anahtar
 * @param name    goruntulenen ad
 * @param combo   tetikleyen tus kombinasyonu
 * @param cdTicks bekleme suresi (tick)
 * @param desc    kisa aciklama
 */
public record Ability(int level, String id, String name, Combo combo, int cdTicks, String desc) {

    /** Kural: hicbir yetenek 60 saniyeden kisa beklemez (ozel olarak belirtilenler haric). */
    public static Ability of(int level, String id, String name, Combo combo, int cdTicks, String desc) {
        int cd = (combo == Combo.PASSIVE) ? 0 : Math.max(cdTicks, 1200);
        return new Ability(level, id, name, combo, cd, desc);
    }

    /** Kullanicinin acikca istedigi istisna sureler (30 sn, 10 sn, beklemesiz). */
    public static Ability exact(int level, String id, String name, Combo combo, int cdTicks, String desc) {
        return new Ability(level, id, name, combo, cdTicks, desc);
    }
}
