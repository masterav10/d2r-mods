package org.immersed.d2r.model;

import java.nio.file.Path;

import org.inferred.freebuilder.FreeBuilder;

/**
 * Settings to be used throughout the plugins.
 * 
 * @author Dan Avila
 *
 */
@FreeBuilder
public interface CascSettings
{
    /**
     * Creates settings builder.
     * 
     * @author Dan Avila
     *
     */
    class Builder extends CascSettings_Builder
    {
    }

    /**
     * Gets the location of the game files (top-level) directory.
     * 
     * @return the path.
     */
    Path installation();

    /**
     * The location to store plugin data.
     * 
     * @return the path.
     */
    Path extraction();

    /**
     * The location to install the mod.
     * 
     * @return the path.
     */
    Path mods();
}
