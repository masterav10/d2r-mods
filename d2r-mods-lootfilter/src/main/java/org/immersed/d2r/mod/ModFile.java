package org.immersed.d2r.mod;

import java.nio.file.Path;

/**
 * Represents a file we can modify for modding purposes. Implementations can
 * further define how to modify content.
 * 
 * @author Dan Avila
 *
 */
public interface ModFile
{
    /**
     * Saves the content to disk.
     * 
     * @param root
     */
    void save(Path root);
}
