package net.cursedvalley.classes;

/** Oyuncu siniflari. */
public enum ClassType {
    WARRIOR("Savasci"),
    SHAMAN("Saman"),
    ASSASSIN("Suikastci"),
    MAGE("Buyucu"),
    ARCHER("Okcu");

    private final String tr;
    ClassType(String tr) { this.tr = tr; }
    public String tr() { return tr; }

    public static ClassType byName(String s) {
        if (s == null) return null;
        for (ClassType c : values()) {
            if (c.name().equalsIgnoreCase(s) || c.tr.equalsIgnoreCase(s)) return c;
        }
        return switch (s.toLowerCase()) {
            case "savasci", "savaşçı", "savasçi" -> WARRIOR;
            case "saman", "şaman"                -> SHAMAN;
            case "suikastci", "suikastçı"        -> ASSASSIN;
            case "buyucu", "büyücü"              -> MAGE;
            case "okcu", "okçu"                  -> ARCHER;
            default -> null;
        };
    }
}
