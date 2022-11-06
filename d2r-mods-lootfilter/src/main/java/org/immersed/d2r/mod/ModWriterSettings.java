package org.immersed.d2r.mod;

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

    CascSettings settings();

    ObjectWriter mapper();
}
