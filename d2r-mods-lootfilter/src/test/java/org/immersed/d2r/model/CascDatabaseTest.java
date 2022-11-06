package org.immersed.d2r.model;

import static org.assertj.core.api.Assertions.*;

import org.immersed.d2r.apps.D2RConfig;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * Provides convenience functions for wrapping around a CascStorage.
 * 
 * @author Dan Avila
 */
class CascDatabaseTest
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
    void testD2RLoading()
    {
        assertThat(database.getFiles()).isNotEmpty();

        database.getFiles()
                .stream()
                .filter(file ->
                {
                    String name = file.name();

                    return name.endsWith(".txt") && file.isPresent();
                })
                .findFirst()
                .ifPresent(file ->
                {
                    System.out.println(file.getFileContents(database));
                });
    }

    @Test
    void testPrintingOutCapabilities()
    {
        database.printOutStorageInfo();
    }
}
