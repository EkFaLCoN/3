package net.cursedvalley.classes;

import org.bukkit.Material;
import org.bukkit.inventory.EquipmentSlot;

/** Sinifa ozel zirh parcalari. */
public enum ArmorType {

    // SAVASCI: agir plaka
    W_HELM ("savasci_migfer",   "Savasci Migferi",   ClassType.WARRIOR,  EquipmentSlot.HEAD,  Material.NETHERITE_HELMET,     4, 3, 0.0),
    W_CHEST("savasci_gogusluk", "Savasci Goguslugu", ClassType.WARRIOR,  EquipmentSlot.CHEST, Material.NETHERITE_CHESTPLATE, 9, 3, 0.0),
    W_LEGS ("savasci_pantolon", "Savasci Pantolonu", ClassType.WARRIOR,  EquipmentSlot.LEGS,  Material.NETHERITE_LEGGINGS,   7, 3, 0.0),
    W_BOOTS("savasci_bot",      "Savasci Botu",      ClassType.WARRIOR,  EquipmentSlot.FEET,  Material.NETHERITE_BOOTS,      4, 3, 0.0),

    // SAMAN: kaftan (biraz daha az zirh, iyilestirme odakli)
    S_HELM ("saman_baslik",     "Saman Basligi",     ClassType.SHAMAN,   EquipmentSlot.HEAD,  Material.NETHERITE_HELMET,     3, 2, 0.0),
    S_CHEST("saman_kaftan",     "Saman Kaftani",     ClassType.SHAMAN,   EquipmentSlot.CHEST, Material.NETHERITE_CHESTPLATE, 7, 2, 0.0),
    S_LEGS ("saman_etek",       "Saman Etegi",       ClassType.SHAMAN,   EquipmentSlot.LEGS,  Material.NETHERITE_LEGGINGS,   5, 2, 0.0),
    S_BOOTS("saman_sargi",      "Saman Sargisi",     ClassType.SHAMAN,   EquipmentSlot.FEET,  Material.NETHERITE_BOOTS,      3, 2, 0.0),

    // SUIKASTCI: hafif deri (zirh dusuk, hiz yuksek)
    A_HELM ("suikast_kukuleta", "Suikast Kukuletasi",ClassType.ASSASSIN, EquipmentSlot.HEAD,  Material.NETHERITE_HELMET,     2, 1, 0.02),
    A_CHEST("suikast_yelek",    "Suikast Yelegi",    ClassType.ASSASSIN, EquipmentSlot.CHEST, Material.NETHERITE_CHESTPLATE, 5, 1, 0.02),
    A_LEGS ("suikast_pantolon", "Suikast Pantolonu", ClassType.ASSASSIN, EquipmentSlot.LEGS,  Material.NETHERITE_LEGGINGS,   4, 1, 0.02),
    A_BOOTS("suikast_bot",      "Suikast Botu",      ClassType.ASSASSIN, EquipmentSlot.FEET,  Material.NETHERITE_BOOTS,      2, 1, 0.02),

    // BUYUCU: runlu kaftan
    M_HELM ("buyucu_kukuleta",  "Buyucu Kukuletasi", ClassType.MAGE,     EquipmentSlot.HEAD,  Material.NETHERITE_HELMET,     2, 1, 0.0),
    M_CHEST("buyucu_kaftan",    "Buyucu Kaftani",    ClassType.MAGE,     EquipmentSlot.CHEST, Material.NETHERITE_CHESTPLATE, 6, 1, 0.0),
    M_LEGS ("buyucu_etek",      "Buyucu Etegi",      ClassType.MAGE,     EquipmentSlot.LEGS,  Material.NETHERITE_LEGGINGS,   4, 1, 0.0),
    M_BOOTS("buyucu_ayakkabi",  "Buyucu Ayakkabisi", ClassType.MAGE,     EquipmentSlot.FEET,  Material.NETHERITE_BOOTS,      2, 1, 0.0),

    // OKCU: deri + sadak kayisi
    R_HELM ("okcu_kukuleta",    "Okcu Kukuletasi",   ClassType.ARCHER,   EquipmentSlot.HEAD,  Material.NETHERITE_HELMET,     3, 1, 0.01),
    R_CHEST("okcu_yelek",       "Okcu Yelegi",       ClassType.ARCHER,   EquipmentSlot.CHEST, Material.NETHERITE_CHESTPLATE, 6, 1, 0.01),
    R_LEGS ("okcu_pantolon",    "Okcu Pantolonu",    ClassType.ARCHER,   EquipmentSlot.LEGS,  Material.NETHERITE_LEGGINGS,   5, 1, 0.01),
    R_BOOTS("okcu_bot",         "Okcu Botu",         ClassType.ARCHER,   EquipmentSlot.FEET,  Material.NETHERITE_BOOTS,      3, 1, 0.01);

    private final String id, tr;
    private final ClassType owner;
    private final EquipmentSlot slot;
    private final Material material;
    private final double armor, tough, speed;

    ArmorType(String id, String tr, ClassType owner, EquipmentSlot slot, Material material,
              double armor, double tough, double speed) {
        this.id = id; this.tr = tr; this.owner = owner; this.slot = slot;
        this.material = material; this.armor = armor; this.tough = tough; this.speed = speed;
    }

    public String id() { return id; }
    public String tr() { return tr; }
    public ClassType owner() { return owner; }
    public EquipmentSlot slot() { return slot; }
    public Material material() { return material; }
    public double armor() { return armor; }
    public double toughness() { return tough; }
    public double speed() { return speed; }

    /** Resourcepack'teki equipment modeli (sinif basina tek doku takimi). */
    public String modelKey() { return owner.name().toLowerCase(); }

    public static ArmorType byId(String id) {
        for (ArmorType a : values()) if (a.id.equalsIgnoreCase(id)) return a;
        return null;
    }
}
