package org.immersed.d2r.mod;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

import org.immersed.d2r.model.CascDatabase;
import org.immersed.d2r.model.CascFile;
import org.immersed.d2r.model.CascSettings;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.ObjectWriter;

/**
 * Represents a json file.
 * 
 * @author Dan Avila
 */
public class JsonModFile implements ModFile
{
    private final Mod mod;
    private final CascFile item;
    private final JsonNode node;

    public JsonModFile(Mod mod, CascFile item) throws IOException
    {
        this.mod = mod;
        this.item = item;

        CascSettings settings = mod.settings();
        CascDatabase database = mod.database();
        ObjectReader reader = settings.reader();

        byte[] data = item.getFileBytes(database);
        this.node = reader.readTree(data);
    }

    @Override
    public void save(Path dataRoot)
    {
        CascSettings settings = mod.settings();
        ObjectWriter writer = settings.writer();

        Path modFile = dataRoot.resolve(item.relativePath());

        try
        {
            Files.createDirectories(modFile.getParent());
            writer.writeValue(modFile.toFile(), this.node);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

}
