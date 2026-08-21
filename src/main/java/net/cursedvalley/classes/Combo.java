package net.cursedvalley.classes;

/**
 * Sunucunun gorebildigi tus girdileri.
 *
 * ONEMLI: Minecraft sunucusu klavyedeki harf tuslarini (Z, X, C...) GORMEZ.
 * Istemci sadece su girdileri paket olarak gonderir: sag/sol tik, comelme (Shift),
 * el degistirme (F), esya birakma (Q), ziplama, slot degistirme.
 * Bu yuzden kombinasyonlar bu tuslardan kuruldu.
 */
public enum Combo {
    PASSIVE      ("Pasif"),
    SHIFT_SWAP   ("Shift + F"),
    SHIFT_JUMP   ("Shift + Space (comelirken zipla)"),
    SHIFT_RIGHT  ("Shift + Sag tik"),
    DOUBLE_SHIFT ("Shift x2 (hizli)"),
    SHOOT        ("Ok atisi");

    private final String tr;
    Combo(String tr) { this.tr = tr; }
    public String tr() { return tr; }
}
