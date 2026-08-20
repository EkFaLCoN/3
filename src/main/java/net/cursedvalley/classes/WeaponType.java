package net.cursedvalley.classes;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;

/**
 * Sinifa ait silahlar.
 *
 * maxLevel: seviye tavani (asalarin bonussuzu 3, digerleri 10)
 * unlocks : yeteneklerin acildigi seviyeler
 */
public enum WeaponType {

    // ---- SAVASCI ----
    WAR_AXE("savas_baltasi", "Savas Baltasi", ClassType.WARRIOR, Material.NETHERITE_AXE,
            17.0, 1.0, 10),
    WAR_SCYTHE("war_scythe", "War Scythe", ClassType.WARRIOR, Material.NETHERITE_SWORD,
            13.0, 1.2, 10),

    // ---- SAMAN ----
    JUSTICE_HAMMER("adalet_cekici", "Adalet Cekici", ClassType.SHAMAN, Material.NETHERITE_AXE,
            12.0, 0.9, 10),
    SHAMAN_MACE("saman_gurzu", "Saman Gurzu", ClassType.SHAMAN, Material.NETHERITE_AXE,
            9.0, 0.7, 10),
    SHAMAN_SHIELD("savas_kalkani", "Savas Kalkani", ClassType.SHAMAN, Material.SHIELD,
            0.0, 0.0, 10),

    // ---- SUIKASTCI ----
    DAGGER_MAIN("sag_hancer", "Sag Hancer", ClassType.ASSASSIN, Material.NETHERITE_SWORD,
            8.0, 2.4, 10),
    DAGGER_OFF("sol_hancer", "Sol Hancer", ClassType.ASSASSIN, Material.NETHERITE_SWORD,
            4.0, 2.4, 10),

    // ---- BUYUCU ----
    STAFF_PLAIN("asa", "Asa", ClassType.MAGE, Material.BLAZE_ROD, 3.0, 1.6, 3),
    STAFF_ICE("buz_asasi", "Buz Asasi", ClassType.MAGE, Material.BLAZE_ROD, 4.0, 1.6, 10),
    STAFF_FIRE("ates_asasi", "Ates Asasi", ClassType.MAGE, Material.BLAZE_ROD, 4.0, 1.6, 10),
    STAFF_STORM("yildirim_asasi", "Yildirim Asasi", ClassType.MAGE, Material.BLAZE_ROD, 4.0, 1.6, 10),

    // ---- OKCU ----
    LONGBOW("uzun_yay", "Uzun Yay", ClassType.ARCHER, Material.BOW, 0.0, 0.0, 10),
    HEAVY_CROSSBOW("agir_arbalet", "Agir Arbalet", ClassType.ARCHER, Material.CROSSBOW, 0.0, 0.0, 10),
    QUIVER("sadak", "Sadak", ClassType.ARCHER, Material.LEATHER, 0.0, 0.0, 1);

    /** Yeteneklerin acildigi seviyeler (1 = ana yetenek, baslangicta acik). */
    public static final int[] UNLOCKS = {1, 2, 4, 5, 7, 9, 10};

    private final String id, tr;
    private final ClassType owner;
    private final Material material;
    private final double damage, speed;
    private final int maxLevel;

    WeaponType(String id, String tr, ClassType owner, Material material,
               double damage, double speed, int maxLevel) {
        this.id = id; this.tr = tr; this.owner = owner; this.material = material;
        this.damage = damage; this.speed = speed; this.maxLevel = maxLevel;
    }

    public String id() { return id; }

    /**
     * Esyanin gorunecegi model anahtari.
     *
     * Adalet Cekici ve War Scythe ZATEN datapack/resourcepack'te var; onlarin
     * mevcut modelleri kullanilir, yenisi uretilmez.
     */
    public NamespacedKey modelKey() {
        return switch (this) {
            case JUSTICE_HAMMER -> new NamespacedKey("minecraft", "item/adaletcekici/adaletcekici");
            case WAR_SCYTHE     -> new NamespacedKey("minecraft", "item/warscythe/warscythe");
            default             -> new NamespacedKey("cursedclasses", id);
        };
    }
    public String tr() { return tr; }
    public ClassType owner() { return owner; }
    public Material material() { return material; }
    public double damage() { return damage; }
    public double speed() { return speed; }
    public int maxLevel() { return maxLevel; }

    public static WeaponType byId(String id) {
        for (WeaponType w : values()) if (w.id.equalsIgnoreCase(id)) return w;
        return null;
    }
}
