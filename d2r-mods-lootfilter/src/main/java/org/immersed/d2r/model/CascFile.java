package org.immersed.d2r.model;

import static org.bytedeco.casclib.global.casclib.*;
import static org.immersed.d2r.model.CascUtil.*;

import org.bytedeco.casclib.CASC_FIND_DATA;
import org.bytedeco.javacpp.BytePointer;
import org.bytedeco.javacpp.IntPointer;
import org.bytedeco.javacpp.Pointer;
import org.bytedeco.javacpp.PointerPointer;
import org.bytedeco.systems.windows.HANDLE;
import org.inferred.freebuilder.FreeBuilder;

import com.google.common.base.Preconditions;

/**
 * Represents a file stored within a Casc Database.
 * 
 * @author Dan Avila
 */
@FreeBuilder
public interface CascFile
{
    /**
     * Builds a file from the Casc Database
     * 
     * @author Dan Avila
     *
     */
    class Builder extends CascFile_Builder
    {
        /**
         * Populates all fields of this builder from the data provided from a file found
         * in casc storage.
         * 
         * @param pFindData the data from the casc database.
         * @return this.
         */
        public Builder mergeFromCasc(CASC_FIND_DATA pFindData)
        {
            super.name(pFindData.szFileName()
                                .getString());
            super.isPresent(pFindData.bFileAvailable() != 0);

            super.size(pFindData.FileSize());

            return this;
        }
    }

    /**
     * The name of the file within storage.
     * 
     * @return the name of the file within the database.
     * @see CASC_FIND_DATA#szFileName()
     */
    String name();

    /**
     * Check to see if the file is available.
     * 
     * @return true if the file is on the local disk, false otherwise.
     */
    boolean isPresent();

    /**
     * The size of the file.
     * 
     * @return size in bytes
     */
    long size();

    /**
     * Returns the contents of a file stored within a CascDatabase.
     * 
     * @param database the database holding the file.
     * @return the text of the file as a string.
     */
    default String getFileContents(CascDatabase database)
    {
        return new String(getFileBytes(database));
    }

    /**
     * Returns the contents of a file stored within a CascDatabase.
     * 
     * @param database the data base holding the file
     * @return the bytes of the file.
     * @see #getFileContents(CascDatabase)
     */
    default byte[] getFileBytes(CascDatabase database)
    {
        Preconditions.checkState(isPresent());

        final HANDLE hStorage = database.hStorage();

        try (PointerPointer<HANDLE> hFilePtr = new PointerPointer<>(1L))
        {
            final String name = name();
            final Pointer szFileName = new BytePointer(name);
            final int dwLocalFlags = 0;
            final int dwOpenFlags = CASC_OPEN_BY_NAME;
            check(CascOpenFile(hStorage, szFileName, dwLocalFlags, dwOpenFlags, hFilePtr));

            final HANDLE hFile = hFilePtr.get(HANDLE.class);

            final BytePointer lpBuffer = new BytePointer(size());
            final int dwToRead = (int) lpBuffer.capacity();
            final IntPointer pdwRead = null;
            check(CascReadFile(hFile, lpBuffer, dwToRead, pdwRead));

            check(CascCloseFile(hFile));

            return lpBuffer.getStringBytes();
        }
    }
}
