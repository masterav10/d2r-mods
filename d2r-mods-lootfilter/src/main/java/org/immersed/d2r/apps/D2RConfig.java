package org.immersed.d2r.apps;

import java.nio.file.Path;
import java.nio.file.Paths;

import org.immersed.d2r.model.CascDatabase;
import org.immersed.d2r.model.CascSettings;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * Main configuration beans for various applications.
 * 
 * @author Dan Avila
 *
 */
@Configuration
public class D2RConfig
{
    /**
     * Settings for my local D2R.
     * 
     * @return the settings object.
     */
    @Bean
    public CascSettings settings(ObjectMapper objectMapper)
    {
        final Path home = Paths.get("C:", "Program Files (x86)", "Diablo II Resurrected");
        final Path install = home.resolve("Data");
        final Path mods = home.resolve("mods");
        final Path settings = Paths.get("C:", "ProgramData", "d2r-mods");

        return new CascSettings.Builder().installation(install)
                                         .extraction(settings)
                                         .mods(mods)
                                         .mapper(objectMapper)
                                         .build();
    }

    @Bean(destroyMethod = "close")
    public CascDatabase database(CascSettings settings)
    {
        return new CascDatabase(settings);
    }

    /**
     * Json object mapper, to be used for reading and writing files.
     * 
     * @return the mapper bean.
     */
    @Bean
    public ObjectMapper objectMapper()
    {
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(JsonParser.Feature.ALLOW_COMMENTS, true);
        return mapper;
    }
}
