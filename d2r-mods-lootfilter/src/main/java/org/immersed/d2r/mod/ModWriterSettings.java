package org.immersed.d2r.mod;

import org.immersed.d2r.model.CascDatabase;
import org.immersed.d2r.model.CascSettings;
import org.inferred.freebuilder.FreeBuilder;

import com.fasterxml.jackson.databind.ObjectWriter;

@FreeBuilder
public interface ModWriterSettings
{
    class Builder extends ModWriterSettings_Builder
    {
    }

    Mod mod();

    default ObjectWriter mapper()
    {
        return settings().writer();
    }

    default CascSettings settings()
    {
        return mod().settings();
    }

    default CascDatabase database()
    {
        return mod().database();
    }
}
