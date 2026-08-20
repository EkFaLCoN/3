package net.cursedvalley.classes;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;

/** Silah -> yetenek tablosu. */
public final class Abilities {

    private static final Map<WeaponType, List<Ability>> TABLE = new EnumMap<>(WeaponType.class);

    static {
        // ================= SAVASCI =================
        TABLE.put(WeaponType.WAR_AXE, List.of(
            Ability.of(1,  "cinnet",     "Cinnet",        Combo.SHIFT_SWAP,  1200, "10 sn: hasar +%50, savunma -%50"),
            Ability.of(2,  "kanli",      "Kanli Vurus",   Combo.PASSIVE,        0, "Verdigin hasarin %10'u can olarak doner"),
            Ability.of(4,  "zirhkirici", "Zirh Kirici",   Combo.SHIFT_DROP,  1200, "Sonraki 3 vurus zirhi delip gecer"),
            Ability.of(5,  "nara",       "Savas Narasi",  Combo.SHIFT_RIGHT, 1800, "6 blok: dusmanlari savurur ve 4 sn zayiflatir"),
            Ability.of(7,  "direnc",     "Kanla Beslenen",Combo.PASSIVE,        0, "Canin %30'un altindayken gelen hasar %15 azalir"),
            Ability.of(9,  "kasirga",    "Kasirga",       Combo.DOUBLE_SHIFT,1200, "4 blok cevredeki herkese 12 hasar"),
            Ability.of(10, "olumsuz",    "Olumsuz Ofke",  Combo.PASSIVE,        0, "Olumcul hasarda 1 canla ayakta kalir (5 dk'da bir)")
        ));

        TABLE.put(WeaponType.WAR_SCYTHE, List.of(
            Ability.of(2,  "keskin",     "Keskinlik",     Combo.PASSIVE,        0, "Hasar +2"),
            Ability.of(4,  "ruh",        "Ruh Emme",      Combo.PASSIVE,        0, "Oldurdugun her hedef 2 can verir"),
            Ability.of(5,  "zincir",     "Zincirleme",    Combo.SHIFT_DROP,  1200, "8 blok icindeki 3 hedefi birden ceker"),
            Ability.of(7,  "korku",      "Korku Salma",   Combo.SHIFT_RIGHT, 1200, "5 blok: yaratiklar 5 sn kacar"),
            Ability.of(9,  "hasat",      "Hasat",         Combo.DOUBLE_SHIFT,1200, "Onundeki cizgide 10 hasar + 3 sn kanama"),
            Ability.of(10, "olum",       "Olum Dokunusu", Combo.PASSIVE,        0, "Canin %20 altindaki hedefi vurus aninda yok eder")
        ));

        // ================= SAMAN =================
        TABLE.put(WeaponType.JUSTICE_HAMMER, List.of(
            Ability.of(2,  "kutsama",    "Kutsama",       Combo.PASSIVE,        0, "Hasar +2"),
            Ability.of(4,  "sok",        "Sok Dalgasi",   Combo.SHIFT_DROP,  1200, "5 blok: sersemletir (yavaslik III, 3 sn)"),
            Ability.of(5,  "arinma",     "Arinma",        Combo.SHIFT_RIGHT, 1200, "Uzerindeki tum olumsuz etkileri temizler"),
            Ability.of(7,  "ceza",       "Ilahi Ceza",    Combo.PASSIVE,        0, "%15 ihtimalle vurus 6 ek buyu hasari verir"),
            Ability.of(9,  "yargi",      "Yargi",         Combo.DOUBLE_SHIFT,1200, "Hedefe yildirim + 10 hasar"),
            Ability.of(10, "adaletusta", "Adaletin Eli",  Combo.PASSIVE,        0, "Parti uyeleri 2 blok yakininda +%10 hasar alir")
        ));

        TABLE.put(WeaponType.SHAMAN_MACE, List.of(
            Ability.of(1,  "sifa",       "Sifa",          Combo.SHIFT_RIGHT, 1200, "Secili parti uyesini (yoksa kendini) 8 can iyilestirir"),
            Ability.of(2,  "ruhsal",     "Ruhsal Baglilik",Combo.PASSIVE,       0, "Iyilestirme +2"),
            Ability.of(4,  "toplu",      "Toplu Sifa",    Combo.SHIFT_DROP,  1200, "8 blok: tum parti 6 can"),
            Ability.of(5,  "kalkanruhu", "Ruh Kalkani",   Combo.SHIFT_SWAP,  1200, "Secili hedefe 5 sn hasar azaltma"),
            Ability.of(7,  "canlandirma","Canlandirma",   Combo.PASSIVE,        0, "Iyilestirdigin hedef 3 sn yenilenme alir"),
            Ability.of(9,  "diriltme",   "Diriltme",      Combo.DOUBLE_SHIFT,3600, "Yakindaki olen parti uyesini ayaga kaldirir"),
            Ability.of(10, "kutsalcember","Kutsal Cember",Combo.PASSIVE,        0, "Parti uyeleri 6 blok icinde saniyede 1 can yeniler")
        ));

        TABLE.put(WeaponType.SHAMAN_SHIELD, List.of(
            Ability.of(1,  "dokunulmaz", "Dokunulmazlik", Combo.SHIFT_SWAP,  1200, "2 saniye hicbir hasar almazsin"),
            Ability.of(2,  "saglam",     "Saglam Demir",  Combo.PASSIVE,        0, "Kalkan cani +%20"),
            Ability.of(4,  "yansima",    "Yansima",       Combo.PASSIVE,        0, "Blokladigin hasarin %20'si saldirana doner"),
            Ability.of(5,  "sarsinti",   "Sarsinti",      Combo.SHIFT_DROP,  1200, "4 blok: herkesi savurur"),
            Ability.of(7,  "siper",      "Siper",         Combo.PASSIVE,        0, "Blok halindeyken hareket yavaslamaz"),
            Ability.of(9,  "kutsalduvar","Kutsal Duvar",  Combo.DOUBLE_SHIFT,1800, "6 blok: tum parti 2 sn dokunulmaz"),
            Ability.of(10, "kalkanusta", "Bozulmaz",      Combo.PASSIVE,        0, "Blokladigin hasar %30 daha az")
        ));

        // ================= SUIKASTCI =================
        TABLE.put(WeaponType.DAGGER_MAIN, List.of(
            Ability.exact(1, "gizlen",   "Gizlenme",      Combo.SHIFT_SWAP,   600, "30 sn tamamen gorunmez (zirh dahil); savas baslayinca biter"),
            Ability.of(2,  "sirtvurus",  "Sirttan Vurus", Combo.PASSIVE,        0, "Arkadan vurusta hasar +%40"),
            Ability.of(4,  "zehir",      "Zehirli Uc",    Combo.PASSIVE,        0, "Vuruslar 4 sn zehir birakir"),
            Ability.of(5,  "golge",      "Golge Adimi",   Combo.SHIFT_RIGHT, 1200, "Bakilan hedefin arkasina isinlanir"),
            Ability.of(7,  "cevik",      "Ceviklik",      Combo.PASSIVE,        0, "Surekli Hiz I"),
            Ability.of(9,  "infaz",      "Infaz",         Combo.DOUBLE_SHIFT,1200, "Canin %35 altindaki hedefe cift hasar"),
            Ability.of(10, "suikastusta","Sessiz Olum",   Combo.PASSIVE,        0, "Gizliyken ilk vurus 3 kat hasar")
        ));

        TABLE.put(WeaponType.DAGGER_OFF, List.of(
            Ability.of(1,  "atilim",     "Atilim",        Combo.SHIFT_DROP,  2400, "5 blok ileri firlar"),
            Ability.of(2,  "ciftbicak",  "Cift Bicak",    Combo.PASSIVE,        0, "Sag hancerin hasari +2"),
            Ability.of(4,  "kanama",     "Kanama",        Combo.PASSIVE,        0, "Vuruslar 3 sn kanama uygular"),
            Ability.of(5,  "savusturma", "Savusturma",    Combo.PASSIVE,        0, "%10 ihtimalle gelen hasari tamamen sekersin"),
            Ability.of(7,  "hizlibicak", "Hizli Bicak",   Combo.PASSIVE,        0, "Saldiri hizi +%15"),
            Ability.of(9,  "geridonus",  "Geri Donus",    Combo.DOUBLE_SHIFT,1800, "Atilim yaptigin noktaya geri isinlanir"),
            Ability.of(10, "solusta",    "Ikiz Bicaklar", Combo.PASSIVE,        0, "Cift hancerle hasar +%20")
        ));

        // ================= BUYUCU =================
        TABLE.put(WeaponType.STAFF_PLAIN, List.of(
            Ability.of(1,  "isik",       "Isik Topu",     Combo.SHIFT_SWAP,  1200, "Baktigin yone isik topu: 3 sn korluk + 5 buyu hasari"),
            Ability.of(3,  "isiknova",   "Isik Novasi",   Combo.SHIFT_DROP,  1200, "r=10 alan: herkes korlesir + 3 buyu hasari")
        ));
        TABLE.put(WeaponType.STAFF_ICE, List.of(
            Ability.of(1,  "buztopu",    "Buz Topu",      Combo.SHIFT_SWAP,  1200, "Baktigin yone buz topu: 3 sn yavaslik + 7 buyu hasari"),
            Ability.of(2,  "buzzirh",    "Buz Zirhi",     Combo.PASSIVE,        0, "Sana vuran 2 sn yavaslar"),
            Ability.of(4,  "donma",      "Donma",         Combo.SHIFT_RIGHT, 1200, "Hedefi 2 sn yerinde dondurur"),
            Ability.of(5,  "buzzirhi",   "Kirag",         Combo.PASSIVE,        0, "Buz hasari +2"),
            Ability.of(7,  "buzyol",     "Buz Yolu",      Combo.PASSIVE,        0, "Suda ve buzda hizli yurursun"),
            Ability.of(9,  "buzmizrak",  "Buz Mizragi",   Combo.SHIFT_DROP,  1200, "Delici buz: cizgideki herkese 9 hasar"),
            Ability.of(10, "buznova",    "Buz Novasi",    Combo.DOUBLE_SHIFT,1200, "r=10 alan: herkes yavaslar + 5 buyu hasari")
        ));
        TABLE.put(WeaponType.STAFF_FIRE, List.of(
            Ability.of(1,  "atestopu",   "Ates Topu",     Combo.SHIFT_SWAP,  1200, "Baktigin yone ates topu: yakar + 10 buyu hasari"),
            Ability.of(2,  "atesyay",    "Ates Aurasi",   Combo.PASSIVE,        0, "Sana vuran 2 sn yanar"),
            Ability.of(4,  "alevkalkan", "Alev Kalkani",  Combo.SHIFT_RIGHT, 1200, "5 sn ates hasari almazsin"),
            Ability.of(5,  "kor",        "Kor Ates",      Combo.PASSIVE,        0, "Ates hasari +2"),
            Ability.of(7,  "kul",        "Kul Izleri",    Combo.PASSIVE,        0, "Yanan hedefler sana %10 daha az hasar verir"),
            Ability.of(9,  "alevdalga",  "Alev Dalgasi",  Combo.SHIFT_DROP,  1200, "Cizgide ilerleyen alev: 8 hasar + yanma"),
            Ability.of(10, "atesnova",   "Ates Novasi",   Combo.DOUBLE_SHIFT,1200, "r=10 alan: herkes yanar + 7 buyu hasari")
        ));
        TABLE.put(WeaponType.STAFF_STORM, List.of(
            Ability.of(1,  "yildirimtopu","Yildirim Topu",Combo.SHIFT_SWAP,  1200, "Baktigin yone yildirim topu: %5 yildirim + 6 buyu hasari"),
            Ability.of(2,  "statik",     "Statik",        Combo.PASSIVE,        0, "Sana vuran 1 sn sersemler"),
            Ability.of(4,  "sarj",       "Sarj",          Combo.SHIFT_RIGHT, 1200, "8 sn: yildirim ihtimali %5 -> %25"),
            Ability.of(5,  "gerilim",    "Gerilim",       Combo.PASSIVE,        0, "Yildirim hasari +2"),
            Ability.of(7,  "iletken",    "Iletken",       Combo.PASSIVE,        0, "Yagmurda hasarin +%10"),
            Ability.of(9,  "zincirsimsek","Zincir Simsek",Combo.SHIFT_DROP,  1200, "Yildirim 3 hedefe siçrar, her biri 7 hasar"),
            Ability.of(10, "yildirimnova","Yildirim Novasi",Combo.DOUBLE_SHIFT,1200,"r=10 alan: herkese yildirim + 6 buyu hasari")
        ));

        // ================= OKCU =================
        TABLE.put(WeaponType.LONGBOW, List.of(
            Ability.exact(1, "uclu",     "Uclu Ok",       Combo.SHOOT,          0, "Her atis 3 ok firlatir (envanterdeki oka gore)"),
            Ability.of(2,  "keskingoz",  "Keskin Goz",    Combo.PASSIVE,        0, "Ok hasari +%10"),
            Ability.of(4,  "delici",     "Delici Atis",   Combo.PASSIVE,        0, "Oklar zirhin %25'ini yok sayar"),
            Ability.of(5,  "geriadim",   "Geri Adim",     Combo.SHIFT_DROP,  1200, "3 blok geri sicrar, 3 sn hiz verir"),
            Ability.of(7,  "hizliceki",  "Hizli Cekis",   Combo.PASSIVE,        0, "Yay cekme suresi %20 kisalir"),
            Ability.of(9,  "yagmur",     "Ok Yagmuru",    Combo.SHIFT_RIGHT, 1800, "Hedef noktaya 12 ok yagar"),
            Ability.exact(10,"besli",    "Besli Ok",      Combo.SHIFT_SWAP,   200, "Sonraki atis 5 ok firlatir (10 sn)")
        ));
        TABLE.put(WeaponType.HEAVY_CROSSBOW, List.of(
            Ability.of(1,  "cilgin",     "Cilgin Atis",   Combo.SHIFT_SWAP,  1200, "5 sn: cekme/vurma hizi +%1000, hasar +%200"),
            Ability.of(2,  "agiruc",     "Agir Uc",       Combo.PASSIVE,        0, "Ok hasari +%10"),
            Ability.of(4,  "sabitlik",   "Sabitlik",      Combo.PASSIVE,        0, "Oklar daha az dusus yapar"),
            Ability.of(5,  "sarsici",    "Sarsici Ok",    Combo.PASSIVE,        0, "Isabet eden hedef 2 sn yavaslar"),
            Ability.of(7,  "ciftkurus",  "Cift Kurus",    Combo.PASSIVE,        0, "%20 ihtimalle iki ok birden gider"),
            Ability.of(9,  "delikdesik", "Delik Desik",   Combo.SHIFT_DROP,  1200, "Sonraki 3 ok zirhi tamamen delip gecer"),
            Ability.exact(10,"infazoku", "Infaz Oku",     Combo.DOUBLE_SHIFT, 600, "Sonraki ok hedefin ANLIK caninin %25'ini goturur")
        ));
        TABLE.put(WeaponType.QUIVER, List.of(
            Ability.of(1,  "sonsuzok",   "Sonsuz Ok",     Combo.PASSIVE,        0, "Envanterindeyken oklarin tukenmez")
        ));
    }

    public static List<Ability> of(WeaponType w) {
        return TABLE.getOrDefault(w, List.of());
    }

    /** Belirli bir kombinasyona bagli, seviyesi yeten yetenegi bulur. */
    public static Ability find(WeaponType w, int level, Combo combo) {
        for (Ability a : of(w)) {
            if (a.combo() == combo && level >= a.level()) return a;
        }
        return null;
    }

    public static boolean has(WeaponType w, int level, String id) {
        for (Ability a : of(w)) if (a.id().equals(id)) return level >= a.level();
        return false;
    }

    private Abilities() {}
}
