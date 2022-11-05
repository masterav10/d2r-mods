package org.immersed.d2r.apps;

import static java.nio.file.StandardOpenOption.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.immersed.d2r.model.CascDatabase;
import org.immersed.d2r.model.CascFile;
import org.immersed.d2r.model.CascSettings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Exports as many files from the application as possible.
 * 
 * @author Dan Avila
 *
 */
public class D2RExportAll
{
    private static final Logger LOG = LoggerFactory.getLogger(D2RExportAll.class);

    public static void main(String[] args) throws IOException
    {
        try (ConfigurableApplicationContext ctx = SpringApplication.run(D2RConfig.class, args))
        {
            final CascSettings settings = ctx.getBean(CascSettings.class);
            final ObjectMapper mapper = ctx.getBean(ObjectMapper.class);

            try (CascDatabase database = new CascDatabase(settings.installation()))
            {
                final Path root = Files.createDirectories(settings.extraction());

                Set<String> includeExts = new HashSet<>(Arrays.asList(".json", ".template", ".mht", ".txt", ".template",
                        ".frontend", ".params", ".dat"));

                for (CascFile file : database.getFiles())
                {
                    final String name = file.name();

                    final Path extractedLocation = root.resolve(name.replace("data:", ""));
                    LOG.info("  {}", extractedLocation);

                    int index = name.lastIndexOf('.');

                    if (index >= 0)
                    {
                        String ext = name.substring(name.lastIndexOf('.'), name.length());

                        if (!includeExts.contains(ext))
                        {
                            continue;
                        }

                        Files.createDirectories(extractedLocation.getParent());
                        Files.deleteIfExists(extractedLocation);

                        final byte[] contents = file.getFileBytes(database);

                        if (".json".equals(ext))
                        {
                            try
                            {
                                Object json = mapper.readValue(contents, Object.class);
                                mapper.writerWithDefaultPrettyPrinter()
                                      .writeValue(extractedLocation.toFile(), json);
                            }
                            catch (Exception e)
                            {
                                Files.write(extractedLocation, contents, CREATE, WRITE);
                            }
                        }
                        else
                        {
                            Files.write(extractedLocation, contents, CREATE, WRITE);
                        }
                    }
                }
            }
        }
    }
}
