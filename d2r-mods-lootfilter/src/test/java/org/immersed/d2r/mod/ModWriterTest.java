package org.immersed.d2r.mod;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;

import org.immersed.d2r.apps.D2RConfig;
import org.immersed.d2r.model.CascDatabase;
import org.immersed.d2r.model.CascFile;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

public class ModWriterTest
{
    private CascDatabase database;

    @BeforeEach
    public void loadDatabase()
    {
        D2RConfig config = new D2RConfig();
        this.database = new CascDatabase(config.settings(config.objectMapper()));
    }

    @AfterEach
    public void closeDatabase()
    {
        this.database.close();
    }

    @Test
    public void testModWriting(@TempDir Path tempDir) throws IOException
    {
        D2RConfig config = new D2RConfig();

        ModWriterSettings.Builder builder = new ModWriterSettings.Builder();

        builder.modBuilder()
               .name("mod")
               .database(database)
               .settingsBuilder()
               .mapper(config.objectMapper())
               .mods(tempDir);

        CascFile casc = database.getFiles()
                                .stream()
                                .filter(cf -> cf.name()
                                                .contains("item-names.json"))
                                .findFirst()
                                .get();

        ModWriterSettings mws = builder.buildPartial();
        JsonModFile file = new JsonModFile(mws.mod(), casc);

        ModWriter writer = new ModWriter(mws);
        writer.write(Arrays.asList(file));

        Files.walk(mws.settings()
                      .mods())
             .filter(Files::isRegularFile)
             .forEach(System.out::println);
    }
}
