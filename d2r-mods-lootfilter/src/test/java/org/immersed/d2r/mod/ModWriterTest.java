package org.immersed.d2r.mod;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Collections;

import org.immersed.d2r.apps.D2RConfig;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import com.fasterxml.jackson.databind.ObjectWriter;

public class ModWriterTest
{
    @Test
    public void testModWriting(@TempDir
    Path tempDir) throws IOException
    {
        D2RConfig config = new D2RConfig();

        ObjectWriter mapper = config.objectMapper()
                                    .writerWithDefaultPrettyPrinter();

        ModWriterSettings.Builder builder = new ModWriterSettings.Builder();

        builder.mapper(mapper);
        builder.modBuilder()
               .name("mod");
        builder.settingsBuilder()
               .mods(tempDir);

        ModWriter writer = new ModWriter(builder.buildPartial());
        writer.write(Collections.emptyList());
    }
}
