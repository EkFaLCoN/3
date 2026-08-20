package net.cursedvalley.classes;

import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.util.*;

/** Oyuncu sinifi, parasi ve partisi; ayrica sinif-esya izin listeleri. */
public final class Store {

    private final Plugin plugin;
    private final File file;
    private final YamlConfiguration yml;

    private final Map<UUID, ClassType> classes = new HashMap<>();
    private final Map<UUID, Double> money = new HashMap<>();
    /** Sinifa ek olarak izin verilen esya kimlikleri (silah id'si ya da Material adi). */
    private final Map<ClassType, Set<String>> extraAllowed = new EnumMap<>(ClassType.class);

    public Store(Plugin plugin) {
        this.plugin = plugin;
        this.file = new File(plugin.getDataFolder(), "players.yml");
        this.yml = YamlConfiguration.loadConfiguration(file);
        load();
    }

    private void load() {
        if (yml.isConfigurationSection("players")) {
            for (String k : yml.getConfigurationSection("players").getKeys(false)) {
                UUID id = UUID.fromString(k);
                String c = yml.getString("players." + k + ".class");
                if (c != null) {
                    ClassType t = ClassType.byName(c);
                    if (t != null) classes.put(id, t);
                }
                money.put(id, yml.getDouble("players." + k + ".money", 0));
            }
        }
        for (ClassType t : ClassType.values()) {
            extraAllowed.put(t, new HashSet<>(yml.getStringList("allowed." + t.name())));
        }
    }

    public void save() {
        for (Map.Entry<UUID, ClassType> e : classes.entrySet()) {
            yml.set("players." + e.getKey() + ".class", e.getValue().name());
        }
        for (Map.Entry<UUID, Double> e : money.entrySet()) {
            yml.set("players." + e.getKey() + ".money", e.getValue());
        }
        for (ClassType t : ClassType.values()) {
            yml.set("allowed." + t.name(), new ArrayList<>(extraAllowed.get(t)));
        }
        try {
            plugin.getDataFolder().mkdirs();
            yml.save(file);
        } catch (IOException ex) {
            plugin.getLogger().warning("players.yml yazilamadi: " + ex.getMessage());
        }
    }

    // ---- sinif ----
    public ClassType classOf(Player p) { return classes.get(p.getUniqueId()); }
    public void setClass(Player p, ClassType t) { classes.put(p.getUniqueId(), t); save(); }
    public void clearClass(Player p) { classes.remove(p.getUniqueId()); save(); }

    // ---- para ----
    public double money(Player p) { return money.getOrDefault(p.getUniqueId(), 0.0); }
    public void addMoney(Player p, double d) {
        money.merge(p.getUniqueId(), d, Double::sum);
        save();
    }
    public boolean take(Player p, double d) {
        double have = money(p);
        if (have < d) return false;
        money.put(p.getUniqueId(), have - d);
        save();
        return true;
    }

    // ---- izin listesi ----
    public Set<String> allowed(ClassType t) { return extraAllowed.get(t); }
    public void allow(ClassType t, String id) { extraAllowed.get(t).add(id.toLowerCase()); save(); }
    public void disallow(ClassType t, String id) { extraAllowed.get(t).remove(id.toLowerCase()); save(); }
}
