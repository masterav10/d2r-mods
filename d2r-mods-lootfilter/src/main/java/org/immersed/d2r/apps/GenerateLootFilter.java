package org.immersed.d2r.apps;

import static java.lang.String.*;
import static java.lang.System.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;

import org.immersed.d2r.mod.CsvModFile;
import org.immersed.d2r.mod.JsonModFile;
import org.immersed.d2r.mod.Mod;
import org.immersed.d2r.mod.ModFile;
import org.immersed.d2r.model.CascDatabase;
import org.immersed.d2r.model.CascFile;
import org.immersed.d2r.model.CascSettings;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;

public class GenerateLootFilter
{
    public static void main(String[] args) throws IOException
    {
        try (ConfigurableApplicationContext ctx = SpringApplication.run(D2RConfig.class, args))
        {
            generate(ctx);
        }
    }

    private static void generate(ConfigurableApplicationContext ctx) throws IOException
    {
        final CascSettings settings = ctx.getBean(CascSettings.class);
        final CascDatabase database = ctx.getBean(CascDatabase.class);

        Mod mod = new Mod.Builder().name("loot-filter")
                                   .settings(settings)
                                   .database(database)
                                   .build();

        CascFile itemNamesFile = database.getFileByName("item-names.json");
        JsonModFile itemNamesJson = new JsonModFile(mod, itemNamesFile);

        Collection<ModFile> files = new ArrayList<>();

        for (String name : Arrays.asList("misc.txt", "armor.txt", "weapons.txt", "runes.txt"))
        {
            CascFile csvFile = database.getFileByName(name);
            CsvModFile csv = new CsvModFile(mod, csvFile);

            out.println();
            out.println(format("// %s", csvFile.name()));

            processCsv(csv, itemNamesJson);

            files.add(csv);
        }
    }

    private static void processCsv(CsvModFile csv, JsonModFile itemNamesJson)
    {
        final int rowCount = csv.getRowCount();

        for (int i = 0; i < rowCount; i++)
        {
            String quest = csv.getValueAt("quest", i);
            String unique = csv.getValueAt("unique", i);

            boolean isQuestItem = !"".equals(quest);
            boolean isUnique = "1".equals(unique);

            boolean commentOut = isQuestItem || isUnique;
            final String code = csv.getValueAt("code", i);

            itemNamesJson.updateJsonStrings(map ->
            {
                Object key = map.get("Key");

                if (key.equals(code))
                {
                    Object english = map.get("enUS");
                    String processed = processName(english.toString());
                    String statement = format("rename(codeToSubs, \"%s\", \"%s\", \"\");", key, english);

                    out.println(statement);
                }
            });
        }
    }

    private static final String processName(String english)
    {
        return english;
    }
}
