package org.immersed.d2r.apps;

import java.util.Set;
import java.util.TreeSet;

import org.immersed.d2r.model.CascDatabase;
import org.immersed.d2r.model.CascFile;
import org.immersed.d2r.model.CascSettings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;

public class D2RFolderMetrics
{
    private static final Logger LOG = LoggerFactory.getLogger(D2RFolderMetrics.class);

    public static void main(String... args)
    {
        try (ConfigurableApplicationContext ctx = SpringApplication.run(D2RConfig.class, args))
        {
            final CascSettings settings = ctx.getBean(CascSettings.class);

            try (CascDatabase database = new CascDatabase(settings.installation()))
            {
                Set<String> extensions = new TreeSet<>();
                int noExt = 0;

                for (CascFile file : database.getFiles())
                {
                    final String name = file.name();

                    int index = name.lastIndexOf('.');

                    if (index >= 0)
                    {
                        extensions.add(name.substring(name.lastIndexOf('.'), name.length()));
                    }
                    else
                    {
                        noExt++;
                    }
                }

                LOG.info("{}", extensions);
                LOG.info("{}", noExt);
            }
        }
    }
}
