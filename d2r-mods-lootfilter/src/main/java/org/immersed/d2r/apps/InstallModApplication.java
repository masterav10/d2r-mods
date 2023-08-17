package org.immersed.d2r.apps;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import org.immersed.d2r.mod.JsonModFile;
import org.immersed.d2r.mod.Mod;
import org.immersed.d2r.mod.ModFile;
import org.immersed.d2r.mod.ModWriter;
import org.immersed.d2r.mod.ModWriterSettings;
import org.immersed.d2r.model.CascDatabase;
import org.immersed.d2r.model.CascFile;
import org.immersed.d2r.model.CascSettings;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;

public class InstallModApplication
{
    public static void main(String[] args) throws IOException
    {
        try (ConfigurableApplicationContext ctx = SpringApplication.run(D2RConfig.class, args))
        {
            final Map<String, String> codeToSubs = new HashMap<>();
            populateSubs(codeToSubs);

            final CascSettings settings = ctx.getBean(CascSettings.class);
            final CascDatabase database = ctx.getBean(CascDatabase.class);

            Mod mod = new Mod.Builder().name("custom-mods")
                                       .settings(settings)
                                       .database(database)
                                       .build();

            Collection<ModFile> moddedFiles = new ArrayList<>();

            CascFile itemNamesFile = database.getFileByName("item-names.json");
            JsonModFile itemNamesJson = new JsonModFile(mod, itemNamesFile);
            moddedFiles.add(itemNamesJson);

            itemNamesJson.updateJson(root ->
            {
                installMissingEntry(root, map ->
                {
                    map.put("id", 2247);
                    map.put("Key", "gsb");
                    map.put("enUS", "Sapphire");
                    map.put("zhTW", "藍寶石");
                    map.put("deDE", "Saphir");
                    map.put("esES", "Zafiro");
                    map.put("frFR", "Saphir");
                    map.put("itIT", "Zaffiro");
                    map.put("koKR", "사파이어");
                    map.put("plPL", "Szafir");
                    map.put("esMX", "Zafiro");
                    map.put("jaJP", "サファイア");
                    map.put("ptBR", "Safira");
                    map.put("ruRU", "сапфир");
                    map.put("zhCN", "的蓝宝石");
                });

                installMissingEntry(root, map ->
                {
                    map.put("id", 2253);
                    map.put("Key", "gsg");
                    map.put("enUS", "Emerald");
                    map.put("zhTW", "藍寶石");
                    map.put("deDE", "Smaragd");
                    map.put("esES", "Esmeralda");
                    map.put("frFR", "Émeraude");
                    map.put("itIT", "Smeraldo");
                    map.put("koKR", "에메랄드");
                    map.put("plPL", "Szmaragd");
                    map.put("esMX", "Esmeralda");
                    map.put("jaJP", "エメラルド");
                    map.put("ptBR", "Esmeralda");
                    map.put("ruRU", "изумруд");
                    map.put("zhCN", "的绿宝石");
                });

                installMissingEntry(root, map ->
                {
                    map.put("id", 2258);
                    map.put("Key", "gsr");
                    map.put("enUS", "Ruby");
                    map.put("zhTW", "紅寶石");
                    map.put("deDE", "Rubin");
                    map.put("esES", "Rubí");
                    map.put("frFR", "Rubis");
                    map.put("itIT", "Rubino");
                    map.put("koKR", "루비");
                    map.put("plPL", "Rubin");
                    map.put("esMX", "Rubí");
                    map.put("jaJP", "ルビー");
                    map.put("ptBR", "Rubi");
                    map.put("ruRU", "рубин");
                    map.put("zhCN", "的红宝石");
                });

                installMissingEntry(root, map ->
                {
                    map.put("id", 2263);
                    map.put("Key", "gsw");
                    map.put("enUS", "Diamond");
                    map.put("zhTW", "鑽石");
                    map.put("deDE", "Diamant");
                    map.put("esES", "Diamante");
                    map.put("frFR", "Diamant");
                    map.put("itIT", "Diamante");
                    map.put("koKR", "다이아몬드");
                    map.put("plPL", "Diament");
                    map.put("esMX", "Diamante");
                    map.put("jaJP", "ダイヤモンド");
                    map.put("ptBR", "Diamante");
                    map.put("ruRU", "бриллиант");
                    map.put("zhCN", "的钻石");
                });
            });

            // data:data\global\excel\runes.txt

            // code
            substitute(codeToSubs, itemNamesJson);

            CascFile itemAffixesFile = database.getFileByName("item-nameaffixes.json");
            JsonModFile itemAffixesJson = new JsonModFile(mod, itemAffixesFile);
            moddedFiles.add(itemAffixesJson);

            substitute(codeToSubs, itemAffixesJson);

            CascFile itemRunesFile = database.getFileByName("item-runes.json");
            JsonModFile itemRunesJson = new JsonModFile(mod, itemRunesFile);
            moddedFiles.add(itemRunesJson);

            itemRunesJson.updateJsonStrings(map ->
            {
                Object obj = map.get("enUS");
                String name = obj.toString();

                if (!name.endsWith("Rune"))
                {
                    return;
                }

                final String pickMe = "         Pick  Me         ";
                final int spaces = pickMe.length() - name.length();

                StringBuilder builder = new StringBuilder("\n~");

                for (int i = 0; i < spaces / 2; i++)
                {
                    builder.append(" ");
                }

                builder.append(name);

                int remaining = pickMe.length() - (spaces / 2 + name.length());

                for (int i = 0; i < remaining; i++)
                {
                    builder.append(" ");
                }

                builder.append("~\n");

                builder.append("ÿc;~");
                builder.append(pickMe);
                builder.append("~ÿc0\n");

                map.put("enUS", builder.toString());
            });

            ModWriterSettings mws = new ModWriterSettings.Builder().mod(mod)
                                                                   .build();

            ModWriter writer = new ModWriter(mws);
            writer.write(moddedFiles);
        }
    }

    private static void populateSubs(Map<String, String> codeToSubs)
    {
        // data:data\global\excel\misc.txt
        rename(codeToSubs, "gld", "Gold", "ÿc7Gÿc0");

        // data:data\global\excel\misc.txt
        rename(codeToSubs, "elx", "Elixir", "");
        rename(codeToSubs, "vps", "Stamina Potion", "Stamina");
        rename(codeToSubs, "yps", "Antidote Potion", "Antidote");
        rename(codeToSubs, "rvs", "Rejuvenation Potion", " ÿc;3ÿc0 RP ");
        rename(codeToSubs, "rvl", "Full Rejuvenation Potion", " ÿc;5ÿc0 RP ");
        rename(codeToSubs, "wms", "Thawing Potion", "Thaw");
        rename(codeToSubs, "tbk", "Tome of Town Portal", "TP Tome");
        rename(codeToSubs, "ibk", "Tome of Identify", "ID Tome");
        rename(codeToSubs, "amu", "Amulet", "");
        rename(codeToSubs, "vip", "Top of the Horadric Staff", "");
        rename(codeToSubs, "rin", "Ring", "");
        rename(codeToSubs, "bks", "Scroll of Inifuss", "");
        rename(codeToSubs, "bkd", "Key to the Cairn Stones", "");
        rename(codeToSubs, "aqv", "Arrows", "");
        rename(codeToSubs, "tch", "Torch", "");
        rename(codeToSubs, "cqv", "Bolts", "");
        rename(codeToSubs, "tsc", "Scroll of Town Portal", " TP ");
        rename(codeToSubs, "isc", "Scroll of Identify", " ID ");
        rename(codeToSubs, "hrt", "Heart", "");
        rename(codeToSubs, "brz", "Brain", "");
        rename(codeToSubs, "jaw", "Jawbone", "");
        rename(codeToSubs, "eyz", "Eye", "");
        rename(codeToSubs, "hrn", "Horn", "");
        rename(codeToSubs, "tal", "Tail", "");
        rename(codeToSubs, "flg", "Flag", "");
        rename(codeToSubs, "fng", "Fang", "");
        rename(codeToSubs, "qll", "Quill", "");
        rename(codeToSubs, "sol", "Soul", "");
        rename(codeToSubs, "scz", "Scalp", "");
        rename(codeToSubs, "spe", "Spleen", "");
        rename(codeToSubs, "key", "Key", "");
        rename(codeToSubs, "luv", "The Black Tower Key", "");
        rename(codeToSubs, "j34", "A Jade Figurine", "");
        rename(codeToSubs, "g34", "The Golden Bird", "");
        rename(codeToSubs, "bbb", "Lam Esen's Tome", "");
        rename(codeToSubs, "box", "Horadric Cube", "");
        rename(codeToSubs, "tr1", "Horadric Scroll", "");
        rename(codeToSubs, "mss", "Mephisto's Soulstone", "");
        rename(codeToSubs, "qey", "Khalim's Eye", "");
        rename(codeToSubs, "qhr", "Khalim's Heart", "");
        rename(codeToSubs, "qbr", "Khalim's Brain", "");
        rename(codeToSubs, "ear", "Ear", "");
        rename(codeToSubs, "gcv", "Chipped Amethyst", "1 Amethyst");
        rename(codeToSubs, "gfv", "Flawed Amethyst", "2 Amethyst");
        rename(codeToSubs, "gsv", "Amethyst", "3 Amethyst");
        rename(codeToSubs, "gzv", "Flawless Amethyst", "4 Amethyst");
        rename(codeToSubs, "gpv", "Perfect Amethyst", "5 Amethyst");
        rename(codeToSubs, "gcy", "Chipped Topaz", "1 Topaz");
        rename(codeToSubs, "gfy", "Flawed Topaz", "2 Topaz");
        rename(codeToSubs, "gsy", "Topaz", "3 Topaz");
        rename(codeToSubs, "gly", "Flawless Topaz", "4 Topaz");
        rename(codeToSubs, "gpy", "Perfect Topaz", "5 Topaz");
        rename(codeToSubs, "gcb", "Chipped Sapphire", "1 Sapphire");
        rename(codeToSubs, "gfb", "Flawed Sapphire", "2 Sapphire");
        rename(codeToSubs, "gsb", "Sapphire", "3 Sapphire");
        rename(codeToSubs, "glb", "Flawless Sapphire", "4 Sapphire");
        rename(codeToSubs, "gpb", "Perfect Sapphire", "5 Sapphire");
        rename(codeToSubs, "gcg", "Chipped Emerald", "1 Emerald");
        rename(codeToSubs, "gfg", "Flawed Emerald", "2 Emerald");
        rename(codeToSubs, "gsg", "Emerald", "3 Emerald");
        rename(codeToSubs, "glg", "Flawless Emerald", "4 Emerald");
        rename(codeToSubs, "gpg", "Perfect Emerald", "5 Emerald");
        rename(codeToSubs, "gcr", "Chipped Ruby", "1 Ruby");
        rename(codeToSubs, "gfr", "Flawed Ruby", "2 Ruby");
        rename(codeToSubs, "gsr", "Ruby", "3 Ruby");
        rename(codeToSubs, "glr", "Flawless Ruby", "4 Ruby");
        rename(codeToSubs, "gpr", "Perfect Ruby", "5 Ruby");
        rename(codeToSubs, "gcw", "Chipped Diamond", "1 Diamond");
        rename(codeToSubs, "gfw", "Flawed Diamond", "2 Diamond");
        rename(codeToSubs, "gsw", "Diamond", "3 Diamond");
        rename(codeToSubs, "glw", "Flawless Diamond", "4 Diamond");
        rename(codeToSubs, "gpw", "Perfect Diamond", "5 Diamond");
        rename(codeToSubs, "hp1", "Minor Healing Potion", " ÿc11ÿc0 HP ");
        rename(codeToSubs, "hp2", "Light Healing Potion", " ÿc12ÿc0 HP ");
        rename(codeToSubs, "hp3", "Healing Potion", " ÿc13ÿc0 HP ");
        rename(codeToSubs, "hp4", "Greater Healing Potion", " ÿc14ÿc0 HP ");
        rename(codeToSubs, "hp5", "Super Healing Potion", " ÿc15ÿc0 HP ");
        rename(codeToSubs, "mp1", "Minor Mana Potion", " ÿc31ÿc0 MP ");
        rename(codeToSubs, "mp2", "Light Mana Potion", " ÿc32ÿc0 MP ");
        rename(codeToSubs, "mp3", "Mana Potion", " ÿc33ÿc0 MP ");
        rename(codeToSubs, "mp4", "Greater Mana Potion", " ÿc34ÿc0 MP ");
        rename(codeToSubs, "mp5", "Super Mana Potion", " ÿc35ÿc0 MP ");
        rename(codeToSubs, "skc", "Chipped Skull", "1 Skull");
        rename(codeToSubs, "skf", "Flawed Skull", "2 Skull");
        rename(codeToSubs, "sku", "Skull", "3 Skull");
        rename(codeToSubs, "skl", "Flawless Skull", "4 Skull");
        rename(codeToSubs, "skz", "Perfect Skull", "5 Skull");
        rename(codeToSubs, "hrb", "Herb", "");
        rename(codeToSubs, "cm1", "Small Charm", "");
        rename(codeToSubs, "cm2", "Large Charm", "");
        rename(codeToSubs, "cm3", "Grand Charm", "");
        rename(codeToSubs, "jew", "Jewel", "");
        rename(codeToSubs, "0sc", "Scroll of Knowledge", "");
        rename(codeToSubs, "pk1", "Key of Terror", "");
        rename(codeToSubs, "pk2", "Key of Hate", "");
        rename(codeToSubs, "pk3", "Key of Destruction", "");
        rename(codeToSubs, "dhn", "Diablo's Horn", "");
        rename(codeToSubs, "bey", "Baal's Eye", "");
        rename(codeToSubs, "mbr", "Mephisto's Brain", "");
        rename(codeToSubs, "tes", "Twisted Essence of Suffering", "");
        rename(codeToSubs, "ceh", "Charged Essence of Hatred", "");
        rename(codeToSubs, "bet", "Burning Essence of Terror", "");
        rename(codeToSubs, "fed", "Festering Essence of Destruction", "");
        rename(codeToSubs, "std", "Standard of Heroes", "");

        // data:data\global\excel\armor.txt
        rename(codeToSubs, "cap", "Cap", "");
        rename(codeToSubs, "skp", "Skull Cap", "");
        rename(codeToSubs, "hlm", "Helm", "");
        rename(codeToSubs, "fhl", "Full Helm", "");
        rename(codeToSubs, "ghm", "Great Helm", "");
        rename(codeToSubs, "crn", "Crown", "");
        rename(codeToSubs, "msk", "Mask", "");
        rename(codeToSubs, "qui", "Quilted Armor", "");
        rename(codeToSubs, "lea", "Leather Armor", "");
        rename(codeToSubs, "hla", "Hard Leather Armor", "");
        rename(codeToSubs, "stu", "Studded Leather", "");
        rename(codeToSubs, "rng", "Ring Mail", "");
        rename(codeToSubs, "scl", "Scale Mail", "");
        rename(codeToSubs, "chn", "Chain Mail", "");
        rename(codeToSubs, "brs", "Breast Plate", "");
        rename(codeToSubs, "spl", "Splint Mail", "");
        rename(codeToSubs, "plt", "Plate Mail", "");
        rename(codeToSubs, "fld", "Field Plate", "");
        rename(codeToSubs, "gth", "Gothic Plate", "");
        rename(codeToSubs, "ful", "Full Plate Mail", "");
        rename(codeToSubs, "aar", "Ancient Armor", "");
        rename(codeToSubs, "ltp", "Light Plate", "");
        rename(codeToSubs, "buc", "Buckler", "");
        rename(codeToSubs, "sml", "Small Shield", "");
        rename(codeToSubs, "lrg", "Large Shield", "");
        rename(codeToSubs, "kit", "Kite Shield", "");
        rename(codeToSubs, "tow", "Tower Shield", "");
        rename(codeToSubs, "gts", "Gothic Shield", "");
        rename(codeToSubs, "lgl", "Leather Gloves", "");
        rename(codeToSubs, "vgl", "Heavy Gloves", "");
        rename(codeToSubs, "mgl", "Chain Gloves", "");
        rename(codeToSubs, "tgl", "Light Gauntlets", "");
        rename(codeToSubs, "hgl", "Gauntlets", "");
        rename(codeToSubs, "lbt", "Boots", "");
        rename(codeToSubs, "vbt", "Heavy Boots", "");
        rename(codeToSubs, "mbt", "Chain Boots", "");
        rename(codeToSubs, "tbt", "Light Plated Boots", "");
        rename(codeToSubs, "hbt", "Greaves", "");
        rename(codeToSubs, "lbl", "Sash", "");
        rename(codeToSubs, "vbl", "Light Belt", "");
        rename(codeToSubs, "mbl", "Belt", "");
        rename(codeToSubs, "tbl", "Heavy Belt", "");
        rename(codeToSubs, "hbl", "Plated Belt", "");
        rename(codeToSubs, "bhm", "Bone Helm", "");
        rename(codeToSubs, "bsh", "Bone Shield", "");
        rename(codeToSubs, "spk", "Spiked Shield", "");
        rename(codeToSubs, "xap", "War Hat", "");
        rename(codeToSubs, "xkp", "Sallet", "");
        rename(codeToSubs, "xlm", "Casque", "");
        rename(codeToSubs, "xhl", "Basinet", "");
        rename(codeToSubs, "xhm", "Winged Helm", "");
        rename(codeToSubs, "xrn", "Grand Crown", "");
        rename(codeToSubs, "xsk", "Death Mask", "");
        rename(codeToSubs, "xui", "Ghost Armor", "");
        rename(codeToSubs, "xea", "Serpentskin Armor", "");
        rename(codeToSubs, "xla", "Demonhide Armor", "");
        rename(codeToSubs, "xtu", "Trellised Armor", "");
        rename(codeToSubs, "xng", "Linked Mail", "");
        rename(codeToSubs, "xcl", "Tigulated Mail", "");
        rename(codeToSubs, "xhn", "Mesh Armor", "");
        rename(codeToSubs, "xrs", "Cuirass", "");
        rename(codeToSubs, "xpl", "Russet Armor", "");
        rename(codeToSubs, "xlt", "Templar Coat", "");
        rename(codeToSubs, "xld", "Sharktooth Armor", "");
        rename(codeToSubs, "xth", "Embossed Plate", "");
        rename(codeToSubs, "xul", "Chaos Armor", "");
        rename(codeToSubs, "xar", "Ornate Plate", "");
        rename(codeToSubs, "xtp", "Mage Plate", "");
        rename(codeToSubs, "xuc", "Defender", "");
        rename(codeToSubs, "xml", "Round Shield", "");
        rename(codeToSubs, "xrg", "Scutum", "");
        rename(codeToSubs, "xit", "Dragon Shield", "");
        rename(codeToSubs, "xow", "Pavise", "");
        rename(codeToSubs, "xts", "Ancient Shield", "");
        rename(codeToSubs, "xlg", "Demonhide Gloves", "");
        rename(codeToSubs, "xvg", "Sharkskin Gloves", "");
        rename(codeToSubs, "xmg", "Heavy Bracers", "");
        rename(codeToSubs, "xtg", "Battle Gauntlets", "");
        rename(codeToSubs, "xhg", "War Gauntlets", "");
        rename(codeToSubs, "xlb", "Demonhide Boots", "");
        rename(codeToSubs, "xvb", "Sharkskin Boots", "");
        rename(codeToSubs, "xmb", "Mesh Boots", "");
        rename(codeToSubs, "xtb", "Battle Boots", "");
        rename(codeToSubs, "xhb", "War Boots", "");
        rename(codeToSubs, "zlb", "Demonhide Sash", "");
        rename(codeToSubs, "zvb", "Sharkskin Belt", "");
        rename(codeToSubs, "zmb", "Mesh Belt", "");
        rename(codeToSubs, "ztb", "Battle Belt", "");
        rename(codeToSubs, "zhb", "War Belt", "");
        rename(codeToSubs, "xh9", "Grim Helm", "");
        rename(codeToSubs, "xsh", "Grim Shield", "");
        rename(codeToSubs, "xpk", "Barbed Shield", "");
        rename(codeToSubs, "dr1", "Wolf Head", "");
        rename(codeToSubs, "dr2", "Hawk Helm", "");
        rename(codeToSubs, "dr3", "Antlers", "");
        rename(codeToSubs, "dr4", "Falcon Mask", "");
        rename(codeToSubs, "dr5", "Spirit Mask", "");
        rename(codeToSubs, "ba1", "Jawbone Cap", "");
        rename(codeToSubs, "ba2", "Fanged Helm", "");
        rename(codeToSubs, "ba3", "Horned Helm", "");
        rename(codeToSubs, "ba4", "Assault Helmet", "");
        rename(codeToSubs, "ba5", "Avenger Guard", "");
        rename(codeToSubs, "pa1", "Targe", "");
        rename(codeToSubs, "pa2", "Rondache", "");
        rename(codeToSubs, "pa3", "Heraldic Shield", "");
        rename(codeToSubs, "pa4", "Aerin Shield", "");
        rename(codeToSubs, "pa5", "Crown Shield", "");
        rename(codeToSubs, "ne1", "Preserved Head", "");
        rename(codeToSubs, "ne2", "Zombie Head", "");
        rename(codeToSubs, "ne3", "Unraveller Head", "");
        rename(codeToSubs, "ne4", "Gargoyle Head", "");
        rename(codeToSubs, "ne5", "Demon Head", "");
        rename(codeToSubs, "ci0", "Circlet", "");
        rename(codeToSubs, "ci1", "Coronet", "");
        rename(codeToSubs, "ci2", "Tiara", "");
        rename(codeToSubs, "ci3", "Diadem", "");
        rename(codeToSubs, "uap", "Shako", "");
        rename(codeToSubs, "ukp", "Hydraskull", "");
        rename(codeToSubs, "ulm", "Armet", "");
        rename(codeToSubs, "uhl", "Giant Conch", "");
        rename(codeToSubs, "uhm", "Spired Helm", "");
        rename(codeToSubs, "urn", "Corona", "");
        rename(codeToSubs, "usk", "Demonhead", "");
        rename(codeToSubs, "uui", "Dusk Shroud", "");
        rename(codeToSubs, "uea", "Wyrmhide", "");
        rename(codeToSubs, "ula", "Scarab Husk", "");
        rename(codeToSubs, "utu", "Wire Fleece", "");
        rename(codeToSubs, "ung", "Diamond Mail", "");
        rename(codeToSubs, "ucl", "Loricated Mail", "");
        rename(codeToSubs, "uhn", "Boneweave", "");
        rename(codeToSubs, "urs", "Great Hauberk", "");
        rename(codeToSubs, "upl", "Balrog Skin", "");
        rename(codeToSubs, "ult", "Hellforge Plate", "");
        rename(codeToSubs, "uld", "Kraken Shell", "");
        rename(codeToSubs, "uth", "Lacquered Plate", "");
        rename(codeToSubs, "uul", "Shadow Plate", "");
        rename(codeToSubs, "uar", "Sacred Armor", "");
        rename(codeToSubs, "utp", "Archon Plate", "");
        rename(codeToSubs, "uuc", "Heater", "");
        rename(codeToSubs, "uml", "Luna", "");
        rename(codeToSubs, "urg", "Hyperion", "");
        rename(codeToSubs, "uit", "Monarch", "");
        rename(codeToSubs, "uow", "Aegis", "");
        rename(codeToSubs, "uts", "Ward", "");
        rename(codeToSubs, "ulg", "Bramble Mitts", "");
        rename(codeToSubs, "uvg", "Vampirebone Gloves", "");
        rename(codeToSubs, "umg", "Vambraces", "");
        rename(codeToSubs, "utg", "Crusader Gauntlets", "");
        rename(codeToSubs, "uhg", "Ogre Gauntlets", "");
        rename(codeToSubs, "ulb", "Wyrmhide Boots", "");
        rename(codeToSubs, "uvb", "Scarabshell Boots", "");
        rename(codeToSubs, "umb", "Boneweave Boots", "");
        rename(codeToSubs, "utb", "Mirrored Boots", "");
        rename(codeToSubs, "uhb", "Myrmidon Greaves", "");
        rename(codeToSubs, "ulc", "Spiderweb Sash", "");
        rename(codeToSubs, "uvc", "Vampirefang Belt", "");
        rename(codeToSubs, "umc", "Mithril Coil", "");
        rename(codeToSubs, "utc", "Troll Belt", "");
        rename(codeToSubs, "uhc", "Colossus Girdle", "");
        rename(codeToSubs, "uh9", "Bone Visage", "");
        rename(codeToSubs, "ush", "Troll Nest", "");
        rename(codeToSubs, "upk", "Blade Barrier", "");
        rename(codeToSubs, "dr6", "Alpha Helm", "");
        rename(codeToSubs, "dr7", "Griffon Headdress", "");
        rename(codeToSubs, "dr8", "Hunter's Guise", "");
        rename(codeToSubs, "dr9", "Sacred Feathers", "");
        rename(codeToSubs, "dra", "Totemic Mask", "");
        rename(codeToSubs, "ba6", "Jawbone Visor", "");
        rename(codeToSubs, "ba7", "Lion Helm", "");
        rename(codeToSubs, "ba8", "Rage Mask", "");
        rename(codeToSubs, "ba9", "Savage Helmet", "");
        rename(codeToSubs, "baa", "Slayer Guard", "");
        rename(codeToSubs, "pa6", "Akaran Targe", "");
        rename(codeToSubs, "pa7", "Akaran Rondache", "");
        rename(codeToSubs, "pa8", "Protector Shield", "");
        rename(codeToSubs, "pa9", "Gilded Shield", "");
        rename(codeToSubs, "paa", "Royal Shield", "");
        rename(codeToSubs, "ne6", "Mummified Trophy", "");
        rename(codeToSubs, "ne7", "Fetish Trophy", "");
        rename(codeToSubs, "ne8", "Sexton Trophy", "");
        rename(codeToSubs, "ne9", "Cantor Trophy", "");
        rename(codeToSubs, "nea", "Hierophant Trophy", "");
        rename(codeToSubs, "drb", "Blood Spirit", "");
        rename(codeToSubs, "drc", "Sun Spirit", "");
        rename(codeToSubs, "drd", "Earth Spirit", "");
        rename(codeToSubs, "dre", "Sky Spirit", "");
        rename(codeToSubs, "drf", "Dream Spirit", "");
        rename(codeToSubs, "bab", "Carnage Helm", "");
        rename(codeToSubs, "bac", "Fury Visor", "");
        rename(codeToSubs, "bad", "Destroyer Helm", "");
        rename(codeToSubs, "bae", "Conqueror Crown", "");
        rename(codeToSubs, "baf", "Guardian Crown", "");
        rename(codeToSubs, "pab", "Sacred Targe", "");
        rename(codeToSubs, "pac", "Sacred Rondache", "");
        rename(codeToSubs, "pad", "Kurast Shield", "");
        rename(codeToSubs, "pae", "Zakarum Shield", "");
        rename(codeToSubs, "paf", "Vortex Shield", "");
        rename(codeToSubs, "neb", "Minion Skull", "");
        rename(codeToSubs, "neg", "Hellspawn Skull", "");
        rename(codeToSubs, "ned", "Overseer Skull", "");
        rename(codeToSubs, "nee", "Succubus Skull", "");
        rename(codeToSubs, "nef", "Bloodlord Skull", "");

        // data:data\global\excel\weapons.txt
        rename(codeToSubs, "hax", "Hand Axe", "");
        rename(codeToSubs, "axe", "Axe", "");
        rename(codeToSubs, "2ax", "Double Axe", "");
        rename(codeToSubs, "mpi", "Military Pick", "");
        rename(codeToSubs, "wax", "War Axe", "");
        rename(codeToSubs, "lax", "Large Axe", "");
        rename(codeToSubs, "bax", "Broad Axe", "");
        rename(codeToSubs, "btx", "Battle Axe", "");
        rename(codeToSubs, "gax", "Great Axe", "");
        rename(codeToSubs, "gix", "Giant Axe", "");
        rename(codeToSubs, "wnd", "Wand", "");
        rename(codeToSubs, "ywn", "Yew Wand", "");
        rename(codeToSubs, "bwn", "Bone Wand", "");
        rename(codeToSubs, "gwn", "Grim Wand", "");
        rename(codeToSubs, "clb", "Club", "");
        rename(codeToSubs, "scp", "Scepter", "");
        rename(codeToSubs, "gsc", "Grand Scepter", "");
        rename(codeToSubs, "wsp", "War Scepter", "");
        rename(codeToSubs, "spc", "Spiked Club", "");
        rename(codeToSubs, "mac", "Mace", "");
        rename(codeToSubs, "mst", "Morning Star", "");
        rename(codeToSubs, "fla", "Flail", "");
        rename(codeToSubs, "whm", "War Hammer", "");
        rename(codeToSubs, "mau", "Maul", "");
        rename(codeToSubs, "gma", "Great Maul", "");
        rename(codeToSubs, "ssd", "Short Sword", "");
        rename(codeToSubs, "scm", "Scimitar", "");
        rename(codeToSubs, "sbr", "Sabre", "");
        rename(codeToSubs, "flc", "Falchion", "");
        rename(codeToSubs, "crs", "Crystal Sword", "");
        rename(codeToSubs, "bsd", "Broad Sword", "");
        rename(codeToSubs, "lsd", "Long Sword", "");
        rename(codeToSubs, "wsd", "War Sword", "");
        rename(codeToSubs, "2hs", "Two-Handed Sword", "");
        rename(codeToSubs, "clm", "Claymore", "");
        rename(codeToSubs, "gis", "Giant Sword", "");
        rename(codeToSubs, "bsw", "Bastard Sword", "");
        rename(codeToSubs, "flb", "Flamberge", "");
        rename(codeToSubs, "gsd", "Great Sword", "");
        rename(codeToSubs, "dgr", "Dagger", "");
        rename(codeToSubs, "dir", "Dirk", "");
        rename(codeToSubs, "kri", "Kris", "");
        rename(codeToSubs, "bld", "Blade", "");
        rename(codeToSubs, "tkf", "Throwing Knife", "");
        rename(codeToSubs, "tax", "Throwing Axe", "");
        rename(codeToSubs, "bkf", "Balanced Knife", "");
        rename(codeToSubs, "bal", "Balanced Axe", "");
        rename(codeToSubs, "jav", "Javelin", "");
        rename(codeToSubs, "pil", "Pilum", "");
        rename(codeToSubs, "ssp", "Short Spear", "");
        rename(codeToSubs, "glv", "Glaive", "");
        rename(codeToSubs, "tsp", "Throwing Spear", "");
        rename(codeToSubs, "spr", "Spear", "");
        rename(codeToSubs, "tri", "Trident", "");
        rename(codeToSubs, "brn", "Brandistock", "");
        rename(codeToSubs, "spt", "Spetum", "");
        rename(codeToSubs, "pik", "Pike", "");
        rename(codeToSubs, "bar", "Bardiche", "");
        rename(codeToSubs, "vou", "Voulge", "");
        rename(codeToSubs, "scy", "Scythe", "");
        rename(codeToSubs, "pax", "Poleaxe", "");
        rename(codeToSubs, "hal", "Halberd", "");
        rename(codeToSubs, "wsc", "War Scythe", "");
        rename(codeToSubs, "sst", "Short Staff", "");
        rename(codeToSubs, "lst", "Long Staff", "");
        rename(codeToSubs, "cst", "Gnarled Staff", "");
        rename(codeToSubs, "bst", "Battle Staff", "");
        rename(codeToSubs, "wst", "War Staff", "");
        rename(codeToSubs, "sbw", "Short Bow", "");
        rename(codeToSubs, "hbw", "Hunter's Bow", "");
        rename(codeToSubs, "lbw", "Long Bow", "");
        rename(codeToSubs, "cbw", "Composite Bow", "");
        rename(codeToSubs, "sbb", "Short Battle Bow", "");
        rename(codeToSubs, "lbb", "Long Battle Bow", "");
        rename(codeToSubs, "swb", "Short War Bow", "");
        rename(codeToSubs, "lwb", "Long War Bow", "");
        rename(codeToSubs, "lxb", "Light Crossbow", "");
        rename(codeToSubs, "mxb", "Crossbow", "");
        rename(codeToSubs, "hxb", "Heavy Crossbow", "");
        rename(codeToSubs, "rxb", "Repeating Crossbow", "");
        rename(codeToSubs, "gps", "Rancid Gas Potion", "");
        rename(codeToSubs, "ops", "Oil Potion", "");
        rename(codeToSubs, "gpm", "Choking Gas Potion", "");
        rename(codeToSubs, "opm", "Exploding Potion", "");
        rename(codeToSubs, "gpl", "Strangling Gas Potion", "");
        rename(codeToSubs, "opl", "Fulminating Potion", "");
        rename(codeToSubs, "d33", "Decoy Gidbinn", "");
        rename(codeToSubs, "g33", "The Gidbinn", "");
        rename(codeToSubs, "leg", "Wirt's Leg", "");
        rename(codeToSubs, "hdm", "Horadric Malus", "");
        rename(codeToSubs, "hfh", "Hell Forge Hammer", "");
        rename(codeToSubs, "hst", "Horadric Staff", "");
        rename(codeToSubs, "msf", "Shaft of the Horadric Staff", "");
        rename(codeToSubs, "9ha", "Hatchet", "");
        rename(codeToSubs, "9ax", "Cleaver", "");
        rename(codeToSubs, "92a", "Twin Axe", "");
        rename(codeToSubs, "9mp", "Crowbill", "");
        rename(codeToSubs, "9wa", "Naga", "");
        rename(codeToSubs, "9la", "Military Axe", "");
        rename(codeToSubs, "9ba", "Bearded Axe", "");
        rename(codeToSubs, "9bt", "Tabar", "");
        rename(codeToSubs, "9ga", "Gothic Axe", "");
        rename(codeToSubs, "9gi", "Ancient Axe", "");
        rename(codeToSubs, "9wn", "Burnt Wand", "");
        rename(codeToSubs, "9yw", "Petrified Wand", "");
        rename(codeToSubs, "9bw", "Tomb Wand", "");
        rename(codeToSubs, "9gw", "Grave Wand", "");
        rename(codeToSubs, "9cl", "Cudgel", "");
        rename(codeToSubs, "9sc", "Rune Scepter", "");
        rename(codeToSubs, "9qs", "Holy Water Sprinkler", "");
        rename(codeToSubs, "9ws", "Divine Scepter", "");
        rename(codeToSubs, "9sp", "Barbed Club", "");
        rename(codeToSubs, "9ma", "Flanged Mace", "");
        rename(codeToSubs, "9mt", "Jagged Star", "");
        rename(codeToSubs, "9fl", "Knout", "");
        rename(codeToSubs, "9wh", "Battle Hammer", "");
        rename(codeToSubs, "9m9", "War Club", "");
        rename(codeToSubs, "9gm", "Martel de Fer", "");
        rename(codeToSubs, "9ss", "Gladius", "");
        rename(codeToSubs, "9sm", "Cutlass", "");
        rename(codeToSubs, "9sb", "Shamshir", "");
        rename(codeToSubs, "9fc", "Tulwar", "");
        rename(codeToSubs, "9cr", "Dimensional Blade", "");
        rename(codeToSubs, "9bs", "Battle Sword", "");
        rename(codeToSubs, "9ls", "Rune Sword", "");
        rename(codeToSubs, "9wd", "Ancient Sword", "");
        rename(codeToSubs, "92h", "Espandon", "");
        rename(codeToSubs, "9cm", "Dacian Falx", "");
        rename(codeToSubs, "9gs", "Tusk Sword", "");
        rename(codeToSubs, "9b9", "Gothic Sword", "");
        rename(codeToSubs, "9fb", "Zweihander", "");
        rename(codeToSubs, "9gd", "Executioner Sword", "");
        rename(codeToSubs, "9dg", "Poignard", "");
        rename(codeToSubs, "9di", "Rondel", "");
        rename(codeToSubs, "9kr", "Cinquedeas", "");
        rename(codeToSubs, "9bl", "Stiletto", "");
        rename(codeToSubs, "9tk", "Battle Dart", "");
        rename(codeToSubs, "9ta", "Francisca", "");
        rename(codeToSubs, "9bk", "War Dart", "");
        rename(codeToSubs, "9b8", "Hurlbat", "");
        rename(codeToSubs, "9ja", "War Javelin", "");
        rename(codeToSubs, "9pi", "Great Pilum", "");
        rename(codeToSubs, "9s9", "Simbilan", "");
        rename(codeToSubs, "9gl", "Spiculum", "");
        rename(codeToSubs, "9ts", "Harpoon", "");
        rename(codeToSubs, "9sr", "War Spear", "");
        rename(codeToSubs, "9tr", "Fuscina", "");
        rename(codeToSubs, "9br", "War Fork", "");
        rename(codeToSubs, "9st", "Yari", "");
        rename(codeToSubs, "9p9", "Lance", "");
        rename(codeToSubs, "9b7", "Lochaber Axe", "");
        rename(codeToSubs, "9vo", "Bill", "");
        rename(codeToSubs, "9s8", "Battle Scythe", "");
        rename(codeToSubs, "9pa", "Partizan", "");
        rename(codeToSubs, "9h9", "Bec-de-Corbin", "");
        rename(codeToSubs, "9wc", "Grim Scythe", "");
        rename(codeToSubs, "8ss", "Jo Staff", "");
        rename(codeToSubs, "8ls", "Quarterstaff", "");
        rename(codeToSubs, "8cs", "Cedar Staff", "");
        rename(codeToSubs, "8bs", "Gothic Staff", "");
        rename(codeToSubs, "8ws", "Rune Staff", "");
        rename(codeToSubs, "8sb", "Edge Bow", "");
        rename(codeToSubs, "8hb", "Razor Bow", "");
        rename(codeToSubs, "8lb", "Cedar Bow", "");
        rename(codeToSubs, "8cb", "Double Bow", "");
        rename(codeToSubs, "8s8", "Short Siege Bow", "");
        rename(codeToSubs, "8l8", "Large Siege Bow", "");
        rename(codeToSubs, "8sw", "Rune Bow", "");
        rename(codeToSubs, "8lw", "Gothic Bow", "");
        rename(codeToSubs, "8lx", "Arbalest", "");
        rename(codeToSubs, "8mx", "Siege Crossbow", "");
        rename(codeToSubs, "8hx", "Ballista", "");
        rename(codeToSubs, "8rx", "Chu-Ko-Nu", "");
        rename(codeToSubs, "qf1", "Khalim's Flail", "");
        rename(codeToSubs, "qf2", "Khalim's Will", "");
        rename(codeToSubs, "ktr", "Katar", "");
        rename(codeToSubs, "wrb", "Wrist Blade", "");
        rename(codeToSubs, "axf", "Hatchet Hands", "");
        rename(codeToSubs, "ces", "Cestus", "");
        rename(codeToSubs, "clw", "Claws", "");
        rename(codeToSubs, "btl", "Blade Talons", "");
        rename(codeToSubs, "skr", "Scissors Katar", "");
        rename(codeToSubs, "9ar", "Quhab", "");
        rename(codeToSubs, "9wb", "Wrist Spike", "");
        rename(codeToSubs, "9xf", "Fascia", "");
        rename(codeToSubs, "9cs", "Hand Scythe", "");
        rename(codeToSubs, "9lw", "Greater Claws", "");
        rename(codeToSubs, "9tw", "Greater Talons", "");
        rename(codeToSubs, "9qr", "Scissors Quhab", "");
        rename(codeToSubs, "7ar", "Suwayyah", "");
        rename(codeToSubs, "7wb", "Wrist Sword", "");
        rename(codeToSubs, "7xf", "War Fist", "");
        rename(codeToSubs, "7cs", "Battle Cestus", "");
        rename(codeToSubs, "7lw", "Feral Claws", "");
        rename(codeToSubs, "7tw", "Runic Talons", "");
        rename(codeToSubs, "7qr", "Scissors Suwayyah", "");
        rename(codeToSubs, "7ha", "Tomahawk", "");
        rename(codeToSubs, "7ax", "Small Crescent", "");
        rename(codeToSubs, "72a", "Ettin Axe", "");
        rename(codeToSubs, "7mp", "War Spike", "");
        rename(codeToSubs, "7wa", "Berserker Axe", "");
        rename(codeToSubs, "7la", "Feral Axe", "");
        rename(codeToSubs, "7ba", "Silver-edged Axe", "");
        rename(codeToSubs, "7bt", "Decapitator", "");
        rename(codeToSubs, "7ga", "Champion Axe", "");
        rename(codeToSubs, "7gi", "Glorious Axe", "");
        rename(codeToSubs, "7wn", "Polished Wand", "");
        rename(codeToSubs, "7yw", "Ghost Wand", "");
        rename(codeToSubs, "7bw", "Lich Wand", "");
        rename(codeToSubs, "7gw", "Unearthed Wand", "");
        rename(codeToSubs, "7cl", "Truncheon", "");
        rename(codeToSubs, "7sc", "Mighty Scepter", "");
        rename(codeToSubs, "7qs", "Seraph Rod", "");
        rename(codeToSubs, "7ws", "Caduceus", "");
        rename(codeToSubs, "7sp", "Tyrant Club", "");
        rename(codeToSubs, "7ma", "Reinforced Mace", "");
        rename(codeToSubs, "7mt", "Devil Star", "");
        rename(codeToSubs, "7fl", "Scourge", "");
        rename(codeToSubs, "7wh", "Legendary Mallet", "");
        rename(codeToSubs, "7m7", "Ogre Maul", "");
        rename(codeToSubs, "7gm", "Thunder Maul", "");
        rename(codeToSubs, "7ss", "Falcata", "");
        rename(codeToSubs, "7sm", "Ataghan", "");
        rename(codeToSubs, "7sb", "Elegant Blade", "");
        rename(codeToSubs, "7fc", "Hydra Edge", "");
        rename(codeToSubs, "7cr", "Phase Blade", "");
        rename(codeToSubs, "7bs", "Conquest Sword", "");
        rename(codeToSubs, "7ls", "Cryptic Sword", "");
        rename(codeToSubs, "7wd", "Mythical Sword", "");
        rename(codeToSubs, "72h", "Legend Sword", "");
        rename(codeToSubs, "7cm", "Highland Blade", "");
        rename(codeToSubs, "7gs", "Balrog Blade", "");
        rename(codeToSubs, "7b7", "Champion Sword", "");
        rename(codeToSubs, "7fb", "Colossus Sword", "");
        rename(codeToSubs, "7gd", "Colossus Blade", "");
        rename(codeToSubs, "7dg", "Bone Knife", "");
        rename(codeToSubs, "7di", "Mithril Point", "");
        rename(codeToSubs, "7kr", "Fanged Knife", "");
        rename(codeToSubs, "7bl", "Legend Spike", "");
        rename(codeToSubs, "7tk", "Flying Knife", "");
        rename(codeToSubs, "7ta", "Flying Axe", "");
        rename(codeToSubs, "7bk", "Winged Knife", "");
        rename(codeToSubs, "7b8", "Winged Axe", "");
        rename(codeToSubs, "7ja", "Hyperion Javelin", "");
        rename(codeToSubs, "7pi", "Stygian Pilum", "");
        rename(codeToSubs, "7s7", "Balrog Spear", "");
        rename(codeToSubs, "7gl", "Ghost Glaive", "");
        rename(codeToSubs, "7ts", "Winged Harpoon", "");
        rename(codeToSubs, "7sr", "Hyperion Spear", "");
        rename(codeToSubs, "7tr", "Stygian Pike", "");
        rename(codeToSubs, "7br", "Mancatcher", "");
        rename(codeToSubs, "7st", "Ghost Spear", "");
        rename(codeToSubs, "7p7", "War Pike", "");
        rename(codeToSubs, "7o7", "Ogre Axe", "");
        rename(codeToSubs, "7vo", "Colossus Voulge", "");
        rename(codeToSubs, "7s8", "Thresher", "");
        rename(codeToSubs, "7pa", "Cryptic Axe", "");
        rename(codeToSubs, "7h7", "Great Poleaxe", "");
        rename(codeToSubs, "7wc", "Giant Thresher", "");
        rename(codeToSubs, "6ss", "Walking Stick", "");
        rename(codeToSubs, "6ls", "Stalagmite", "");
        rename(codeToSubs, "6cs", "Elder Staff", "");
        rename(codeToSubs, "6bs", "Shillelagh", "");
        rename(codeToSubs, "6ws", "Archon Staff", "");
        rename(codeToSubs, "6sb", "Spider Bow", "");
        rename(codeToSubs, "6hb", "Blade Bow", "");
        rename(codeToSubs, "6lb", "Shadow Bow", "");
        rename(codeToSubs, "6cb", "Great Bow", "");
        rename(codeToSubs, "6s7", "Diamond Bow", "");
        rename(codeToSubs, "6l7", "Crusader Bow", "");
        rename(codeToSubs, "6sw", "Ward Bow", "");
        rename(codeToSubs, "6lw", "Hydra Bow", "");
        rename(codeToSubs, "6lx", "Pellet Bow", "");
        rename(codeToSubs, "6mx", "Gorgon Crossbow", "");
        rename(codeToSubs, "6hx", "Colossus Crossbow", "");
        rename(codeToSubs, "6rx", "Demon Crossbow", "");
        rename(codeToSubs, "ob1", "Eagle Orb", "");
        rename(codeToSubs, "ob2", "Sacred Globe", "");
        rename(codeToSubs, "ob3", "Smoked Sphere", "");
        rename(codeToSubs, "ob4", "Clasped Orb", "");
        rename(codeToSubs, "ob5", "Jared's Stone", "");
        rename(codeToSubs, "am1", "Stag Bow", "");
        rename(codeToSubs, "am2", "Reflex Bow", "");
        rename(codeToSubs, "am3", "Maiden Spear", "");
        rename(codeToSubs, "am4", "Maiden Pike", "");
        rename(codeToSubs, "am5", "Maiden Javelin", "");
        rename(codeToSubs, "ob6", "Glowing Orb", "");
        rename(codeToSubs, "ob7", "Crystalline Globe", "");
        rename(codeToSubs, "ob8", "Cloudy Sphere", "");
        rename(codeToSubs, "ob9", "Sparkling Ball", "");
        rename(codeToSubs, "oba", "Swirling Crystal", "");
        rename(codeToSubs, "am6", "Ashwood Bow", "");
        rename(codeToSubs, "am7", "Ceremonial Bow", "");
        rename(codeToSubs, "am8", "Ceremonial Spear", "");
        rename(codeToSubs, "am9", "Ceremonial Pike", "");
        rename(codeToSubs, "ama", "Ceremonial Javelin", "");
        rename(codeToSubs, "obb", "Heavenly Stone", "");
        rename(codeToSubs, "obc", "Eldritch Orb", "");
        rename(codeToSubs, "obd", "Demon Heart", "");
        rename(codeToSubs, "obe", "Vortex Orb", "");
        rename(codeToSubs, "obf", "Dimensional Shard", "");
        rename(codeToSubs, "amb", "Matriarchal Bow", "");
        rename(codeToSubs, "amc", "Grand Matron Bow", "");
        rename(codeToSubs, "amd", "Matriarchal Spear", "");
        rename(codeToSubs, "ame", "Matriarchal Pike", "");
        rename(codeToSubs, "amf", "Matriarchal Javelin", "");
    }

    private static void substitute(Map<String, String> codeToSubs, JsonModFile jsonFile)
    {
        jsonFile.updateJsonStrings(entry ->
        {
            String key = (String) entry.get("Key");

            if (codeToSubs.containsKey(key))
            {
                String replacement = codeToSubs.get(key);
                entry.put("enUS", replacement);
            }
        });
    }

    @SuppressWarnings("unchecked")
    private static void installMissingEntry(Object root, Consumer<Map<String, Object>> mapBuilder)
    {
        Map<String, Object> newMap = new LinkedHashMap<>();
        mapBuilder.accept(newMap);

        Integer newId = (Integer) newMap.get("id");

        List<Object> list = (List<Object>) root;

        for (int i = 0; i < list.size(); i++)
        {
            Map<String, Object> entry = (Map<String, Object>) list.get(i);
            Integer currentId = (Integer) entry.get("id");

            if (currentId + 1 == newId)
            {
                list.add(i + 1, newMap);
                return;
            }
        }
    }

    private static void rename(Map<String, String> codeToSubs, String key, String originalName, String newName)
    {
        if ("".equals(newName.trim()))
        {
            codeToSubs.put(key, originalName);
        }
        else
        {
            codeToSubs.put(key, newName);
        }
    }
}
