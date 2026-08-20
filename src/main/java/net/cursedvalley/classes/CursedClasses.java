package net.cursedvalley.classes;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.*;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import java.util.*;

public final class CursedClasses extends JavaPlugin implements Listener {

    private Store store;
    private final Map<UUID, Long> lastSneak = new HashMap<>();

    // ==================== YASAM DONGUSU ====================

    @Override
    public void onEnable() {
        Keys.init(this);
        saveDefaultConfig();
        store = new Store(this);
        getServer().getPluginManager().registerEvents(this, this);
        getServer().getScheduler().runTaskTimer(this, this::tick, 1L, 1L);
        getServer().getScheduler().runTaskTimer(this,
                () -> Bukkit.getOnlinePlayers().forEach(this::armorCheck), 20L, 20L);
        getLogger().info("CursedClasses etkin.");
    }

    @Override
    public void onDisable() {
        if (store != null) store.save();
    }

    public Store store() { return store; }

    /** Gecici durum sayaclarini eritir. */
    private void tick() {
        decay(Cast.FRENZY);
        decay(Cast.IMMUNE);
        decay(Cast.CROSSBOW_RAGE);
        decay(Cast.STORM_CHARGE);
        decay(Cast.FIVE_SHOT);

        for (Iterator<Map.Entry<UUID, Integer>> it = Cast.INVIS.entrySet().iterator(); it.hasNext(); ) {
            var e = it.next();
            int left = e.getValue() - 1;
            Player p = Bukkit.getPlayer(e.getKey());
            if (left <= 0 || p == null) {
                it.remove();
                if (p != null) reveal(p);
            } else {
                e.setValue(left);
            }
        }
    }

    private void decay(Map<UUID, Integer> m) {
        m.entrySet().removeIf(e -> {
            e.setValue(e.getValue() - 1);
            return e.getValue() <= 0;
        });
    }

    private void reveal(Player p) {
        p.removePotionEffect(PotionEffectType.INVISIBILITY);
        for (Player o : Bukkit.getOnlinePlayers()) o.showPlayer(this, p);
    }

    // ==================== TUS KOMBINASYONLARI ====================
    //
    // Sunucu harf tuslarini goremez. Kullanilabilen girdiler:
    //   Shift + F (el degistir), Shift + Q (birak), Shift + sag tik, Shift x2.

    @EventHandler
    public void onSwap(PlayerSwapHandItemsEvent e) {
        if (!e.getPlayer().isSneaking()) return;
        if (fire(e.getPlayer(), Combo.SHIFT_SWAP)) e.setCancelled(true);
    }

    @EventHandler
    public void onDrop(PlayerDropItemEvent e) {
        if (!e.getPlayer().isSneaking()) return;
        if (fire(e.getPlayer(), Combo.SHIFT_DROP)) e.setCancelled(true);
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent e) {
        if (e.getHand() != EquipmentSlot.HAND) return;
        if (!e.getAction().isRightClick() || !e.getPlayer().isSneaking()) return;
        if (fire(e.getPlayer(), Combo.SHIFT_RIGHT)) e.setCancelled(true);
    }

    @EventHandler
    public void onSneak(PlayerToggleSneakEvent e) {
        if (!e.isSneaking()) return;
        Player p = e.getPlayer();
        long now = System.currentTimeMillis();
        Long prev = lastSneak.put(p.getUniqueId(), now);
        if (prev != null && now - prev < 400) {
            lastSneak.remove(p.getUniqueId());
            fire(p, Combo.DOUBLE_SHIFT);
        }
    }

    /** Eldeki (ana ya da yan el) silahin bu kombinasyona bagli yetenegini calistirir. */
    private boolean fire(Player p, Combo combo) {
        for (ItemStack item : new ItemStack[]{
                p.getInventory().getItemInMainHand(),
                p.getInventory().getItemInOffHand()}) {

            WeaponType w = Items.typeOf(item);
            if (w == null) continue;
            if (!canUse(p, item)) { denyUse(p); return true; }

            int lvl = Items.levelOf(item);
            Ability a = Abilities.find(w, lvl, combo);
            if (a == null) continue;

            // Sol hancer sadece sag hancerle birlikte calisir
            boolean hasMain = Items.typeOf(p.getInventory().getItemInMainHand()) == WeaponType.DAGGER_MAIN
                    || Items.typeOf(p.getInventory().getItemInOffHand()) == WeaponType.DAGGER_MAIN;
            if (w == WeaponType.DAGGER_OFF && !hasMain) {
                Cast.msg(p, "Sol hancer ancak sag hancerle birlikte calisir.");
                return true;
            }

            String key = w.id() + ":" + a.id();
            if (!Cooldowns.ready(p, key)) { Cooldowns.deny(p, key); return true; }

            if (Cast.run(this, p, item, w, a)) {
                Cooldowns.set(p, key, a.cdTicks());
            }
            return true;
        }
        return false;
    }

    // ==================== SINIF KISITI ====================

    /** Bu esyayi bu oyuncu kullanabilir mi? */
    public boolean canUse(Player p, ItemStack item) {
        WeaponType w = Items.typeOf(item);
        ClassType cls = store.classOf(p);

        if (w != null) {
            if (cls == null) return false;
            if (w.owner() == cls) return true;
            return store.allowed(cls).contains(w.id());
        }
        if (item == null || cls == null) return true;

        // Okcu: yay/arbalet disinda silah kullanamaz
        if (cls == ClassType.ARCHER && isWeaponMaterial(item.getType())
                && item.getType() != Material.BOW && item.getType() != Material.CROSSBOW) {
            return store.allowed(cls).contains(item.getType().name().toLowerCase());
        }
        return true;
    }

    private boolean isWeaponMaterial(Material m) {
        String n = m.name();
        return n.endsWith("_SWORD") || n.endsWith("_AXE") || n.endsWith("_SHOVEL")
                || n.endsWith("_PICKAXE") || n.endsWith("_HOE") || n.equals("TRIDENT");
    }

    private void denyUse(Player p) {
        ClassType c = store.classOf(p);
        Cast.msg(p, c == null ? "Once sinif sec: /sinif sec <sinif>"
                              : "Bu esya " + c.tr() + " sinifina ait degil.");
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onAttack(EntityDamageByEntityEvent e) {
        if (!(e.getDamager() instanceof Player p)) return;
        ItemStack hand = p.getInventory().getItemInMainHand();

        if (!canUse(p, hand)) { e.setCancelled(true); denyUse(p); return; }

        // sol hancer ana elde -> calismaz
        if (Items.typeOf(hand) == WeaponType.DAGGER_OFF) {
            e.setCancelled(true);
            Cast.msg(p, "Sol hancer sag ele alinamaz.");
            return;
        }
        // sag hancer yan elde -> calismaz
        if (Items.typeOf(p.getInventory().getItemInOffHand()) == WeaponType.DAGGER_MAIN) {
            e.setCancelled(true);
            Cast.msg(p, "Sag hancer sol ele alinamaz.");
            return;
        }

        WeaponType w = Items.typeOf(hand);
        int lvl = Items.levelOf(hand);

        // gizlenme saldirinca bozulur (ilk vurus bonusu ile)
        if (Cast.INVIS.containsKey(p.getUniqueId())) {
            if (w == WeaponType.DAGGER_MAIN && Abilities.has(w, lvl, "suikastusta"))
                e.setDamage(e.getDamage() * 3);
            Cast.INVIS.remove(p.getUniqueId());
            reveal(p);
        }

        if (w == null) return;

        // ---- pasifler ----
        if (w == WeaponType.WAR_AXE) {
            if (Cast.FRENZY.containsKey(p.getUniqueId())) e.setDamage(e.getDamage() * 1.5);
            if (Abilities.has(w, lvl, "kanli")) Cast.heal(p, e.getDamage() * 0.10);
            Integer breaks = Cast.ARMOR_BREAK.get(p.getUniqueId());
            if (breaks != null && e.getEntity() instanceof LivingEntity le) {
                Spell.magicDamage(p, le, e.getDamage());
                e.setDamage(0.01);
                if (breaks <= 1) Cast.ARMOR_BREAK.remove(p.getUniqueId());
                else Cast.ARMOR_BREAK.put(p.getUniqueId(), breaks - 1);
            }
        }
        if (w == WeaponType.WAR_SCYTHE) {
            if (Abilities.has(w, lvl, "keskin")) e.setDamage(e.getDamage() + 2);
            if (Abilities.has(w, lvl, "olum") && e.getEntity() instanceof LivingEntity le) {
                double max = le.getAttribute(org.bukkit.attribute.Attribute.MAX_HEALTH).getValue();
                if (le.getHealth() / max < 0.20) e.setDamage(le.getHealth() + 10);
            }
        }
        if (w == WeaponType.JUSTICE_HAMMER) {
            if (Abilities.has(w, lvl, "kutsama")) e.setDamage(e.getDamage() + 2);
            if (Abilities.has(w, lvl, "ceza") && rnd(100) < 15
                    && e.getEntity() instanceof LivingEntity le) {
                Spell.magicDamage(p, le, 6);
            }
        }
        if (w == WeaponType.DAGGER_MAIN) {
            boolean off = Items.typeOf(p.getInventory().getItemInOffHand()) == WeaponType.DAGGER_OFF;
            if (off) {
                ItemStack o = p.getInventory().getItemInOffHand();
                int ol = Items.levelOf(o);
                if (Abilities.has(WeaponType.DAGGER_OFF, ol, "ciftbicak")) e.setDamage(e.getDamage() + 2);
                if (Abilities.has(WeaponType.DAGGER_OFF, ol, "solusta")) e.setDamage(e.getDamage() * 1.2);
                if (Abilities.has(WeaponType.DAGGER_OFF, ol, "kanama")
                        && e.getEntity() instanceof LivingEntity le)
                    le.addPotionEffect(new PotionEffect(PotionEffectType.WITHER, 60, 0));
            }
            if (Abilities.has(w, lvl, "sirtvurus") && e.getEntity() instanceof LivingEntity le) {
                Vector toVictim = le.getLocation().toVector().subtract(p.getLocation().toVector());
                if (le.getLocation().getDirection().dot(toVictim.normalize()) > 0.4)
                    e.setDamage(e.getDamage() * 1.4);
            }
            if (Abilities.has(w, lvl, "zehir") && e.getEntity() instanceof LivingEntity le)
                le.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 80, 0));
        }
    }

    /** Gelen hasar: dokunulmazlik, cinnet zafiyeti, kalkan pasifleri. */
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onTakeDamage(EntityDamageEvent e) {
        if (!(e.getEntity() instanceof Player p)) return;

        if (Cast.IMMUNE.containsKey(p.getUniqueId())) { e.setCancelled(true); return; }

        if (Cast.FRENZY.containsKey(p.getUniqueId())) e.setDamage(e.getDamage() * 1.5);

        ItemStack main = p.getInventory().getItemInMainHand();
        if (Items.typeOf(main) == WeaponType.WAR_AXE
                && Abilities.has(WeaponType.WAR_AXE, Items.levelOf(main), "direnc")) {
            double max = p.getAttribute(org.bukkit.attribute.Attribute.MAX_HEALTH).getValue();
            if (p.getHealth() / max < 0.30) e.setDamage(e.getDamage() * 0.85);
        }

        ItemStack off = p.getInventory().getItemInOffHand();
        if (Items.typeOf(off) == WeaponType.SHAMAN_SHIELD && p.isBlocking()) {
            int lvl = Items.levelOf(off);
            if (Abilities.has(WeaponType.SHAMAN_SHIELD, lvl, "kalkanusta"))
                e.setDamage(e.getDamage() * 0.7);
            if (Abilities.has(WeaponType.SHAMAN_SHIELD, lvl, "yansima")
                    && e instanceof EntityDamageByEntityEvent ee
                    && ee.getDamager() instanceof LivingEntity att) {
                att.damage(e.getDamage() * 0.2, p);
            }
            // engellenen hasar seviye kazandirir
            Levels.gain(p, off, (int) Math.max(1, e.getDamage() / 20));
        }

        if (Items.typeOf(off) == WeaponType.DAGGER_OFF
                && Abilities.has(WeaponType.DAGGER_OFF, Items.levelOf(off), "savusturma")
                && rnd(100) < 10) {
            e.setCancelled(true);
            Cast.msg(p, "Savusturdun!");
        }
    }

    /** Oldurme -> seviye ilerlemesi. */
    @EventHandler
    public void onDeath(EntityDeathEvent e) {
        Player killer = e.getEntity().getKiller();
        if (killer == null) return;
        ItemStack hand = killer.getInventory().getItemInMainHand();
        WeaponType w = Items.typeOf(hand);
        if (w == null) return;

        int gain = (e.getEntity() instanceof Player) ? 10 : 1;
        Levels.gain(killer, hand, gain);

        if (w == WeaponType.WAR_SCYTHE
                && Abilities.has(w, Items.levelOf(hand), "ruh")) Cast.heal(killer, 2);
    }

    // ==================== OKCU ====================

    @EventHandler
    public void onShoot(EntityShootBowEvent e) {
        if (!(e.getEntity() instanceof Player p)) return;
        ItemStack bow = e.getBow();
        WeaponType w = Items.typeOf(bow);
        if (w == null) return;
        if (!canUse(p, bow)) { e.setCancelled(true); denyUse(p); return; }

        int lvl = Items.levelOf(bow);

        if (w == WeaponType.LONGBOW) {
            int extra = Cast.FIVE_SHOT.remove(p.getUniqueId()) != null ? 4 : 2;
            for (int i = 0; i < extra; i++) {
                Arrow a = p.launchProjectile(Arrow.class,
                        e.getProjectile().getVelocity().clone().add(new Vector(
                                (rnd(20) - 10) / 100.0, (rnd(20) - 10) / 100.0, (rnd(20) - 10) / 100.0)));
                a.setShooter(p);
                a.setDamage(4 * (Abilities.has(w, lvl, "keskingoz") ? 1.1 : 1.0));
                a.setPickupStatus(AbstractArrow.PickupStatus.CREATIVE_ONLY);
                tagArrow(a, p);
            }
        }
        if (e.getProjectile() instanceof AbstractArrow ar) tagArrow(ar, p);
    }

    private final Set<UUID> pierceArrows = new HashSet<>();
    private final Set<UUID> executeArrows = new HashSet<>();
    private final Set<UUID> rageArrows = new HashSet<>();

    private void tagArrow(AbstractArrow a, Player p) {
        Integer pierce = Cast.PIERCE_SHOT.get(p.getUniqueId());
        if (pierce != null) {
            pierceArrows.add(a.getUniqueId());
            if (pierce <= 1) Cast.PIERCE_SHOT.remove(p.getUniqueId());
            else Cast.PIERCE_SHOT.put(p.getUniqueId(), pierce - 1);
        }
        if (Cast.EXECUTE_SHOT.remove(p.getUniqueId()) != null) executeArrows.add(a.getUniqueId());
        if (Cast.CROSSBOW_RAGE.containsKey(p.getUniqueId())) rageArrows.add(a.getUniqueId());
    }

    @EventHandler(priority = EventPriority.HIGH)
    public void onArrowHit(EntityDamageByEntityEvent e) {
        if (!(e.getDamager() instanceof AbstractArrow ar)) return;
        if (!(ar.getShooter() instanceof Player p)) return;
        UUID id = ar.getUniqueId();

        if (rageArrows.remove(id)) e.setDamage(e.getDamage() * 3.0);   // +%200

        if (pierceArrows.remove(id) && e.getEntity() instanceof LivingEntity le) {
            Spell.magicDamage(p, le, e.getDamage());
            e.setDamage(0.01);
        }
        if (executeArrows.remove(id) && e.getEntity() instanceof LivingEntity le) {
            double cut = le.getHealth() * 0.25;
            Spell.magicDamage(p, le, cut);
            Cast.msg(p, "Infaz Oku: " + String.format("%.1f", cut) + " can goturdu.");
        }
    }

    /** Sadak: envanterde oldugu surece ok tukenmez. */
    @EventHandler
    public void onArrowConsume(PlayerPickupArrowEvent e) { /* toplama serbest */ }

    @EventHandler
    public void onShootConsume(EntityShootBowEvent e) {
        if (!(e.getEntity() instanceof Player p)) return;
        if (hasQuiver(p)) e.setConsumeItem(false);
    }

    private boolean hasQuiver(Player p) {
        for (ItemStack it : p.getInventory().getContents()) {
            if (Items.typeOf(it) == WeaponType.QUIVER) return true;
        }
        return false;
    }

    // ==================== SAMAN: kalkan + gurz bonusu ====================

    @EventHandler
    public void onHeld(PlayerItemHeldEvent e) {
        getServer().getScheduler().runTask(this, () -> shamanBonus(e.getPlayer()));
    }

    @EventHandler
    public void onClickInv(InventoryClickEvent e) {
        if (e.getWhoClicked() instanceof Player p)
            getServer().getScheduler().runTask(this, () -> shamanBonus(p));
    }

    /** Sag elde gurz + sol elde savas kalkani -> savunma bonusu. */
    private void shamanBonus(Player p) {
        boolean combo = Items.typeOf(p.getInventory().getItemInMainHand()) == WeaponType.SHAMAN_MACE
                && Items.typeOf(p.getInventory().getItemInOffHand()) == WeaponType.SHAMAN_SHIELD;
        if (combo) {
            p.addPotionEffect(new PotionEffect(PotionEffectType.RESISTANCE, 60, 0, true, false));
        }
        // Adalet Cekici elindeyken savas kalkani kullanilamaz
        if (Items.typeOf(p.getInventory().getItemInMainHand()) == WeaponType.JUSTICE_HAMMER
                && Items.typeOf(p.getInventory().getItemInOffHand()) == WeaponType.SHAMAN_SHIELD) {
            ItemStack shield = p.getInventory().getItemInOffHand();
            p.getInventory().setItemInOffHand(null);
            p.getInventory().addItem(shield);
            Cast.msg(p, "Adalet Cekici ile savas kalkani kullanilamaz (normal kalkan serbest).");
        }
    }

    // ==================== KOMUTLAR ====================

    @Override
    public boolean onCommand(CommandSender s, Command cmd, String label, String[] args) {
        switch (cmd.getName().toLowerCase()) {
            case "sinif" -> { return sinif(s, args); }
            case "cclass" -> { return admin(s, args); }
            case "parti" -> { return parti(s, args); }
            case "para" -> { return para(s, args); }
        }
        return false;
    }

    private boolean sinif(CommandSender s, String[] a) {
        if (!(s instanceof Player p)) return true;
        if (a.length >= 2 && a[0].equalsIgnoreCase("sec")) {
            ClassType t = ClassType.byName(a[1]);
            if (t == null) { p.sendMessage(Component.text("Bilinmeyen sinif.", NamedTextColor.RED)); return true; }
            if (store.classOf(p) != null) {
                p.sendMessage(Component.text("Zaten " + store.classOf(p).tr()
                        + " sinifindasin. Degistirmek icin yetkiliye basvur.", NamedTextColor.RED));
                return true;
            }
            store.setClass(p, t);
            p.sendMessage(Component.text("Sinifin: " + t.tr(), NamedTextColor.GREEN));
            return true;
        }
        ClassType c = store.classOf(p);
        p.sendMessage(Component.text("Sinifin: " + (c == null ? "yok" : c.tr()), NamedTextColor.AQUA));
        p.sendMessage(Component.text("/sinif sec <savasci|saman|suikastci|buyucu|okcu>", NamedTextColor.GRAY));
        return true;
    }

    private boolean admin(CommandSender s, String[] a) {
        if (!s.hasPermission("cursedclasses.admin")) { s.sendMessage("Yetkin yok."); return true; }
        if (a.length == 0) {
            s.sendMessage("/cclass ver <silah> [seviye] | /cclass zirh <sinif> | /cclass benimse <silah> | /cclass izin <sinif> <ekle|sil> <id> | /cclass izinler <sinif> | /cclass sinif <oyuncu> <sinif>");
            return true;
        }
        switch (a[0].toLowerCase()) {
            case "ver" -> {
                if (!(s instanceof Player p) || a.length < 2) return true;
                WeaponType w = WeaponType.byId(a[1]);
                if (w == null) { s.sendMessage("Bilinmeyen silah."); return true; }
                int lvl = a.length > 2 ? Integer.parseInt(a[2]) : 1;
                p.getInventory().addItem(Items.create(w, lvl));
                s.sendMessage(w.tr() + " verildi (seviye " + lvl + ").");
            }
            case "benimse" -> {
                // Eldeki mevcut esyayi (datapack silahi) seviye sistemine dahil eder
                if (!(s instanceof Player p) || a.length < 2) return true;
                WeaponType w = WeaponType.byId(a[1]);
                ItemStack hand = p.getInventory().getItemInMainHand();
                if (w == null || hand.getType().isAir()) { s.sendMessage("Elinde esya yok / silah bilinmiyor."); return true; }
                var meta = hand.getItemMeta();
                meta.getPersistentDataContainer().set(Keys.WEAPON,
                        org.bukkit.persistence.PersistentDataType.STRING, w.id());
                meta.getPersistentDataContainer().set(Keys.LEVEL,
                        org.bukkit.persistence.PersistentDataType.INTEGER, 1);
                meta.getPersistentDataContainer().set(Keys.XP,
                        org.bukkit.persistence.PersistentDataType.INTEGER, 0);
                hand.setItemMeta(meta);
                Items.lore(hand);
                s.sendMessage("Eldeki esya " + w.tr() + " olarak kaydedildi, seviye sistemi acildi.");
            }
            case "zirh" -> {
                if (!(s instanceof Player p) || a.length < 2) { s.sendMessage("/cclass zirh <sinif>"); return true; }
                ClassType t = ClassType.byName(a[1]);
                if (t == null) { s.sendMessage("Bilinmeyen sinif."); return true; }
                for (ArmorType at : ArmorType.values())
                    if (at.owner() == t) p.getInventory().addItem(Items.armor(at));
                s.sendMessage(t.tr() + " zirh takimi verildi.");
            }
            case "izin" -> {
                if (a.length < 4) { s.sendMessage("/cclass izin <sinif> <ekle|sil> <id>"); return true; }
                ClassType t = ClassType.byName(a[1]);
                if (t == null) { s.sendMessage("Bilinmeyen sinif."); return true; }
                if (a[2].equalsIgnoreCase("ekle")) store.allow(t, a[3]);
                else store.disallow(t, a[3]);
                s.sendMessage(t.tr() + " izin listesi guncellendi.");
            }
            case "izinler" -> {
                ClassType t = a.length > 1 ? ClassType.byName(a[1]) : null;
                if (t == null) { s.sendMessage("Bilinmeyen sinif."); return true; }
                s.sendMessage(t.tr() + " ek izinleri: " + store.allowed(t));
            }
            case "sinif" -> {
                if (a.length < 3) return true;
                Player t = Bukkit.getPlayerExact(a[1]);
                ClassType c = ClassType.byName(a[2]);
                if (t == null || c == null) { s.sendMessage("Oyuncu / sinif bulunamadi."); return true; }
                store.setClass(t, c);
                s.sendMessage(t.getName() + " -> " + c.tr());
            }
            default -> s.sendMessage("Bilinmeyen alt komut.");
        }
        return true;
    }

    private boolean parti(CommandSender s, String[] a) {
        if (!(s instanceof Player p)) return true;
        if (a.length == 0) {
            p.sendMessage(Component.text("/parti cagir <oyuncu> | /parti kabul | /parti ayril | /parti liste",
                    NamedTextColor.GRAY));
            return true;
        }
        switch (a[0].toLowerCase()) {
            case "cagir" -> {
                Player t = a.length > 1 ? Bukkit.getPlayerExact(a[1]) : null;
                if (t == null) { p.sendMessage("Oyuncu bulunamadi."); return true; }
                Parties.invite(p, t);
            }
            case "kabul" -> {
                if (!Parties.accept(p)) p.sendMessage("Bekleyen davet yok ya da parti dolu.");
            }
            case "ayril" -> Parties.leave(p);
            case "liste" -> {
                StringBuilder sb = new StringBuilder("Parti: ");
                for (Player m : Parties.online(p)) sb.append(m.getName()).append(" ");
                p.sendMessage(Component.text(sb.toString(), NamedTextColor.AQUA));
            }
        }
        return true;
    }

    private boolean para(CommandSender s, String[] a) {
        if (!(s instanceof Player p)) return true;
        if (a.length == 0) {
            p.sendMessage(Component.text("Bakiyen: " + String.format("%.2f", store.money(p)),
                    NamedTextColor.GOLD));
            return true;
        }
        if (a[0].equalsIgnoreCase("gonder") && a.length >= 3) {
            Player t = Bukkit.getPlayerExact(a[1]);
            double amount = Double.parseDouble(a[2]);
            if (t == null || amount <= 0) { p.sendMessage("Hatali komut."); return true; }
            if (!store.take(p, amount)) { p.sendMessage("Yetersiz bakiye."); return true; }
            store.addMoney(t, amount);
            p.sendMessage("Gonderildi.");
            t.sendMessage(p.getName() + " sana " + amount + " gonderdi.");
        }
        if (a[0].equalsIgnoreCase("ver") && a.length >= 3 && s.hasPermission("cursedclasses.admin")) {
            Player t = Bukkit.getPlayerExact(a[1]);
            if (t != null) { store.addMoney(t, Double.parseDouble(a[2])); p.sendMessage("Verildi."); }
        }
        return true;
    }

    // ==================== kucuk yardimcilar ====================

    private static int rnd(int bound) {
        return java.util.concurrent.ThreadLocalRandom.current().nextInt(bound);
    }

    /** Baska sinifin zirhini giymeyi engeller (her saniye kontrol edilir). */
    private void armorCheck(Player p) {
        ClassType cls = store.classOf(p);
        for (ItemStack it : p.getInventory().getArmorContents()) {
            ArmorType a = Items.armorOf(it);
            if (a == null) continue;
            if (cls == null || a.owner() != cls) {
                p.getInventory().remove(it);
                p.getInventory().addItem(it);
                Cast.msg(p, a.tr() + " sadece " + a.owner().tr() + " sinifina ait.");
            }
        }
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        Cooldowns.clear(e.getPlayer());
        Parties.leave(e.getPlayer());
    }
}
