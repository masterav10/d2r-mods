package org.immersed.d2r.model;

import static org.assertj.core.api.Assertions.*;

import java.nio.file.Path;
import java.nio.file.Paths;

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
        final Path root = Paths.get("C:", "Program Files (x86)", "Diablo II Resurrected", "Data");
        this.database = new CascDatabase(root);
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
