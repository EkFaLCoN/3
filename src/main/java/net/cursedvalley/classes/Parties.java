package net.cursedvalley.classes;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.*;

/** Basit parti sistemi: bir lider, en fazla 6 uye. */
public final class Parties {

    public static final int MAX = 6;

    /** uye -> lider */
    private static final Map<UUID, UUID> MEMBER_OF = new HashMap<>();
    /** lider -> uyeler (lider dahil) */
    private static final Map<UUID, Set<UUID>> PARTY = new HashMap<>();
    /** davet edilen -> lider */
    private static final Map<UUID, UUID> INVITES = new HashMap<>();

    public static UUID leaderOf(Player p) { return MEMBER_OF.get(p.getUniqueId()); }

    public static Set<UUID> membersOf(Player p) {
        UUID lead = MEMBER_OF.get(p.getUniqueId());
        if (lead == null) return Set.of(p.getUniqueId());
        return PARTY.getOrDefault(lead, Set.of(p.getUniqueId()));
    }

    public static boolean sameParty(Player a, Player b) {
        UUID la = MEMBER_OF.get(a.getUniqueId());
        UUID lb = MEMBER_OF.get(b.getUniqueId());
        return la != null && la.equals(lb);
    }

    /** Partideki (ya da yalniz oyuncunun kendisi) canli oyuncular. */
    public static List<Player> online(Player p) {
        List<Player> out = new ArrayList<>();
        for (UUID id : membersOf(p)) {
            Player m = Bukkit.getPlayer(id);
            if (m != null && m.isOnline()) out.add(m);
        }
        if (out.isEmpty()) out.add(p);
        return out;
    }

    public static void invite(Player leader, Player target) {
        INVITES.put(target.getUniqueId(), leader.getUniqueId());
        target.sendMessage(Component.text(leader.getName()
                + " seni partiye cagirdi. Kabul icin: /parti kabul", NamedTextColor.AQUA));
        leader.sendMessage(Component.text("Davet gonderildi: " + target.getName(), NamedTextColor.GRAY));
    }

    public static boolean accept(Player p) {
        UUID lead = INVITES.remove(p.getUniqueId());
        if (lead == null) return false;
        Set<UUID> set = PARTY.computeIfAbsent(lead, k -> new LinkedHashSet<>(Set.of(lead)));
        if (set.size() >= MAX) return false;
        set.add(p.getUniqueId());
        MEMBER_OF.put(lead, lead);
        MEMBER_OF.put(p.getUniqueId(), lead);
        broadcast(lead, p.getName() + " partiye katildi.");
        return true;
    }

    public static void leave(Player p) {
        UUID lead = MEMBER_OF.remove(p.getUniqueId());
        if (lead == null) return;
        Set<UUID> set = PARTY.get(lead);
        if (set != null) {
            set.remove(p.getUniqueId());
            broadcast(lead, p.getName() + " partiden ayrildi.");
            if (p.getUniqueId().equals(lead) || set.size() <= 1) {
                for (UUID id : new ArrayList<>(set)) MEMBER_OF.remove(id);
                PARTY.remove(lead);
            }
        }
    }

    public static void broadcast(UUID leader, String msg) {
        Set<UUID> set = PARTY.getOrDefault(leader, Set.of());
        for (UUID id : set) {
            Player m = Bukkit.getPlayer(id);
            if (m != null) m.sendMessage(Component.text("[Parti] " + msg, NamedTextColor.AQUA));
        }
    }

    private Parties() {}
}
