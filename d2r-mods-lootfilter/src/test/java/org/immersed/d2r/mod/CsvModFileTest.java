package org.immersed.d2r.mod;

import static org.assertj.core.api.Assertions.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import org.immersed.d2r.apps.D2RConfig;
import org.immersed.d2r.model.CascDatabase;
import org.immersed.d2r.model.CascFile;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

public class CsvModFileTest
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
    public void testFileSavingDoesNotCorruptFile(@TempDir Path tempDir) throws IOException
    {
        CascFile file = this.database.getFileByName("misc.txt");

        byte[] root = file.getFileBytes(database);

        Mod mod = new Mod.Builder().database(database)
                                   .buildPartial();

        CsvModFile csvFile = new CsvModFile(mod, file);

        csvFile.save(tempDir);

        Path outputFile = Files.walk(tempDir)
                               .filter(Files::isRegularFile)
                               .findFirst()
                               .orElse(null);

        Files.readAllBytes(outputFile);

        assertThat(outputFile).hasBinaryContent(root);
    }
}
