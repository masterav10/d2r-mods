package org.immersed.d2r.mod;

import org.immersed.d2r.model.CascDatabase;
import org.immersed.d2r.model.CascSettings;
import org.inferred.freebuilder.FreeBuilder;

/**
 * Information associated with a top-level mod.
 * 
 * @author Dan Avila
 */
@FreeBuilder
public interface Mod
{
    /**
     * Used to create a mod.
     * 
     * @author Dan Avila
     *
     */
    class Builder extends Mod_Builder
    {
    }

    /**
     * The name of the mod.
     * 
     * @return a string name.
     */
    String name();

    /**
     * This settings for the current installation.
     * 
     * @return the settings used in this application.
     */
    CascSettings settings();

    /**
     * The underlying original data model.
     * 
     * @return this
     */
    CascDatabase database();
}
