package org.immersed.d2r.mod;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.immersed.d2r.model.CascSettings;

import com.fasterxml.jackson.databind.ObjectWriter;

/**
 * 
 * 
 * @author Dan Avila
 */
public class ModWriter
{
    private final ModWriterSettings modSettings;

    public ModWriter(ModWriterSettings settings)
    {
        this.modSettings = settings;
    }

    public void writeDirect(Collection<ModFile> items) throws IOException
    {
        Path path = modSettings.settings()
                               .installation();

        write(items, path);
    }

    private void write(Collection<ModFile> items, Path modRoot) throws IOException
    {
        Path dataRoot = Files.createDirectories(modRoot);

        for (ModFile modfile : items)
        {
            modfile.save(dataRoot);
        }
    }

    public void write(Collection<ModFile> items) throws IOException
    {
        final Mod mod = modSettings.mod();
        final CascSettings settings = modSettings.settings();
        final ObjectWriter objectMapper = modSettings.mapper();

        final String modName = mod.name();

        Path mods = settings.mods();
        Path modRoot = Files.createDirectories(mods.resolve(modName)
                                                   .resolve(modName + ".mpq"));

        Map<String, Object> values = new HashMap<>();
        values.put("name", modName);
        values.put("savepath", "../");

        Path data = modRoot.resolve("modinfo.json");
        objectMapper.writeValue(data.toFile(), values);

        Path dataRoot = Files.createDirectories(modRoot.resolve("Data"));

        write(items, dataRoot);
    }
}
