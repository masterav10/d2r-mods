package org.immersed.d2r.apps;

import static java.nio.file.StandardOpenOption.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

import org.immersed.d2r.model.CascDatabase;
import org.immersed.d2r.model.CascFile;
import org.immersed.d2r.model.CascSettings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;

public class D2RExportForFastLoadTimes
{
    private static final Logger LOG = LoggerFactory.getLogger(D2RExportAll.class);

    public static void main(String[] args) throws IOException
    {
        try (ConfigurableApplicationContext ctx = SpringApplication.run(D2RConfig.class, args))
        {
            final CascSettings settings = ctx.getBean(CascSettings.class);
            final Path root = settings.installation();

            final List<String> rootDirs = Arrays.asList("global", "hd", "local");

            try (CascDatabase database = new CascDatabase(settings.installation()))
            {
                for (CascFile file : database.getFiles())
                {
                    final String name = file.name();
                    final String relativePath = name.replace("data:data\\", "");

                    if (rootDirs.stream()
                                .map(key -> key + "\\")
                                .noneMatch(relativePath::startsWith))
                    {
                        continue;
                    }

                    final Path extractedLocation = root.resolve(relativePath);
                    LOG.info("  {}", extractedLocation);

                    final byte[] contents = file.getFileBytes(database);

                    if (Files.exists(extractedLocation))
                    {
                        byte[] currentBytes = Files.readAllBytes(extractedLocation);

                        if (Arrays.equals(contents, currentBytes))
                        {
                            continue;
                        }
                    }

                    // only overwrite file if it has different content
                    Files.createDirectories(extractedLocation.getParent());
                    Files.write(extractedLocation, contents, CREATE, WRITE);
                }
            }
        }
    }
}
