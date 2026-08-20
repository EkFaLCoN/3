package net.cursedvalley.classes;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.NamespacedKey;
import org.bukkit.attribute.Attribute;
import org.bukkit.attribute.AttributeModifier;
import org.bukkit.inventory.EquipmentSlotGroup;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataContainer;
import org.bukkit.persistence.PersistentDataType;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import org.bukkit.inventory.meta.components.EquippableComponent;

/** Sinif silahlarini uretir ve okur. */
public final class Items {

    public static ItemStack create(WeaponType w, int level) {
        ItemStack it = new ItemStack(w.material());
        ItemMeta m = it.getItemMeta();

        m.itemName(Component.text(w.tr(), NamedTextColor.GOLD)
                .decoration(TextDecoration.ITALIC, false));
        m.setItemModel(w.modelKey());
        m.setUnbreakable(true);

        PersistentDataContainer pdc = m.getPersistentDataContainer();
        pdc.set(Keys.WEAPON, PersistentDataType.STRING, w.id());
        pdc.set(Keys.LEVEL, PersistentDataType.INTEGER, Math.max(1, level));
        pdc.set(Keys.XP, PersistentDataType.INTEGER, 0);

        if (w.damage() > 0) {
            m.addAttributeModifier(Attribute.ATTACK_DAMAGE, new AttributeModifier(
                    new NamespacedKey("cursedclasses", "dmg_" + w.id()),
                    w.damage() - 1.0, AttributeModifier.Operation.ADD_NUMBER,
                    EquipmentSlotGroup.MAINHAND));
        }
        if (w.speed() > 0) {
            m.addAttributeModifier(Attribute.ATTACK_SPEED, new AttributeModifier(
                    new NamespacedKey("cursedclasses", "spd_" + w.id()),
                    w.speed() - 4.0, AttributeModifier.Operation.ADD_NUMBER,
                    EquipmentSlotGroup.MAINHAND));
        }
        if (w == WeaponType.SHAMAN_SHIELD) {
            m.addAttributeModifier(Attribute.MAX_HEALTH, new AttributeModifier(
                    new NamespacedKey("cursedclasses", "hp_shield"),
                    6.0, AttributeModifier.Operation.ADD_NUMBER,
                    EquipmentSlotGroup.OFFHAND));
            m.addAttributeModifier(Attribute.ARMOR, new AttributeModifier(
                    new NamespacedKey("cursedclasses", "arm_shield"),
                    4.0, AttributeModifier.Operation.ADD_NUMBER,
                    EquipmentSlotGroup.OFFHAND));
        }

        it.setItemMeta(m);
        lore(it);
        return it;
    }

    /** Sinif zirhi uretir. */
    public static ItemStack armor(ArmorType a) {
        ItemStack it = new ItemStack(a.material());
        ItemMeta m = it.getItemMeta();

        m.itemName(Component.text(a.tr(), NamedTextColor.AQUA)
                .decoration(TextDecoration.ITALIC, false));
        m.setItemModel(new NamespacedKey("cursedclasses", a.id()));
        m.setUnbreakable(true);
        m.getPersistentDataContainer().set(Keys.ARMOR, PersistentDataType.STRING, a.id());

        // Giyildiginde gorunecek doku: assets/cursedclasses/equipment/<sinif>.json
        EquippableComponent eq = m.getEquippable();
        eq.setSlot(a.slot());
        eq.setModel(new NamespacedKey("cursedclasses", a.modelKey()));
        eq.setDamageOnHurt(false);
        m.setEquippable(eq);

        EquipmentSlotGroup group = switch (a.slot()) {
            case HEAD -> EquipmentSlotGroup.HEAD;
            case CHEST -> EquipmentSlotGroup.CHEST;
            case LEGS -> EquipmentSlotGroup.LEGS;
            default -> EquipmentSlotGroup.FEET;
        };
        m.addAttributeModifier(Attribute.ARMOR, new AttributeModifier(
                new NamespacedKey("cursedclasses", "arm_" + a.id()),
                a.armor(), AttributeModifier.Operation.ADD_NUMBER, group));
        m.addAttributeModifier(Attribute.ARMOR_TOUGHNESS, new AttributeModifier(
                new NamespacedKey("cursedclasses", "tuf_" + a.id()),
                a.toughness(), AttributeModifier.Operation.ADD_NUMBER, group));
        if (a.speed() > 0) {
            m.addAttributeModifier(Attribute.MOVEMENT_SPEED, new AttributeModifier(
                    new NamespacedKey("cursedclasses", "spd_" + a.id()),
                    a.speed(), AttributeModifier.Operation.ADD_SCALAR, group));
        }

        List<Component> l = new ArrayList<>();
        l.add(line(a.owner().tr() + " zirhi", NamedTextColor.DARK_AQUA));
        l.add(line("Sadece " + a.owner().tr() + " sinifi giyebilir", NamedTextColor.GRAY));
        m.lore(l);

        it.setItemMeta(m);
        return it;
    }

    public static ArmorType armorOf(ItemStack it) {
        if (it == null || !it.hasItemMeta()) return null;
        String id = it.getItemMeta().getPersistentDataContainer()
                .get(Keys.ARMOR, PersistentDataType.STRING);
        return id == null ? null : ArmorType.byId(id);
    }

    // ---------- okuma ----------

    public static WeaponType typeOf(ItemStack it) {
        if (it == null || !it.hasItemMeta()) return null;
        String id = it.getItemMeta().getPersistentDataContainer()
                .get(Keys.WEAPON, PersistentDataType.STRING);
        return id == null ? null : WeaponType.byId(id);
    }

    public static int levelOf(ItemStack it) {
        if (it == null || !it.hasItemMeta()) return 0;
        Integer l = it.getItemMeta().getPersistentDataContainer()
                .get(Keys.LEVEL, PersistentDataType.INTEGER);
        return l == null ? 1 : l;
    }

    public static int xpOf(ItemStack it) {
        if (it == null || !it.hasItemMeta()) return 0;
        Integer x = it.getItemMeta().getPersistentDataContainer()
                .get(Keys.XP, PersistentDataType.INTEGER);
        return x == null ? 0 : x;
    }

    public static void setProgress(ItemStack it, int level, int xp) {
        ItemMeta m = it.getItemMeta();
        m.getPersistentDataContainer().set(Keys.LEVEL, PersistentDataType.INTEGER, level);
        m.getPersistentDataContainer().set(Keys.XP, PersistentDataType.INTEGER, xp);
        it.setItemMeta(m);
        lore(it);
    }

    public static void stampOwner(ItemStack it, UUID id) {
        ItemMeta m = it.getItemMeta();
        m.getPersistentDataContainer().set(Keys.OWNER, PersistentDataType.STRING, id.toString());
        it.setItemMeta(m);
    }

    /** Aciklamayi seviyeye gore yeniden yazar. */
    public static void lore(ItemStack it) {
        WeaponType w = typeOf(it);
        if (w == null) return;
        int lvl = levelOf(it);
        int xp = xpOf(it);

        List<Component> l = new ArrayList<>();
        l.add(line(w.owner().tr() + " silahi", NamedTextColor.DARK_AQUA));
        l.add(line("Seviye " + lvl + " / " + w.maxLevel(), NamedTextColor.YELLOW));
        if (lvl < w.maxLevel()) {
            int need = Levels.needed(lvl);
            l.add(line("Ilerleme: " + xp + " / " + need, NamedTextColor.GRAY));
        } else {
            l.add(line("USTA SEVIYE", NamedTextColor.LIGHT_PURPLE));
        }
        l.add(Component.empty());
        for (Ability a : Abilities.of(w)) {
            boolean open = lvl >= a.level();
            l.add(line((open ? "+ " : "- ") + a.name() + "  [Lv" + a.level() + "]",
                    open ? NamedTextColor.GREEN : NamedTextColor.DARK_GRAY));
            l.add(line("   " + a.combo().tr() + " | " + a.desc(),
                    open ? NamedTextColor.GRAY : NamedTextColor.DARK_GRAY));
        }
        ItemMeta m = it.getItemMeta();
        m.lore(l);
        it.setItemMeta(m);
    }

    private static Component line(String s, NamedTextColor c) {
        return Component.text(s, c).decoration(TextDecoration.ITALIC, false);
    }

    private Items() {}
}
