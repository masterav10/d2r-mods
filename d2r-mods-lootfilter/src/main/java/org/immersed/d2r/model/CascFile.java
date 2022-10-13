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

@FreeBuilder
public interface CascFile
{
    class Builder extends CascFile_Builder
    {
        public Builder mergeFromCasc(CASC_FIND_DATA pFindData)
        {
            super.name(pFindData.szFileName()
                                .getString());
            super.isPresent(pFindData.bFileAvailable() != 0);

            super.size(pFindData.FileSize());
            super.span(pFindData.dwSpanCount());
            
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

    boolean isPresent();

    long size();
    
    int span();
    
    default String getFileContents(CascDatabase database)
    {
        final HANDLE hStorage = database.hStorage();

        try (PointerPointer<HANDLE> hFilePtr = new PointerPointer<>(1L))
        {
            final String name = name();
            final Pointer szFileName = new BytePointer(name.getBytes());
            final int dwLocalFlags = 0;
            final int dwOpenFlags = CASC_OPEN_BY_NAME;
            check(CascOpenFile(hStorage, szFileName, dwLocalFlags, dwOpenFlags, hFilePtr));

            final HANDLE hFile = hFilePtr.get(HANDLE.class);

            final BytePointer lpBuffer = new BytePointer(size());
            final int dwToRead = (int) lpBuffer.capacity();
            final IntPointer pdwRead = null;
            check(CascReadFile(hFile, lpBuffer, dwToRead, pdwRead));

            check(CascCloseFile(hFile));
            
            return lpBuffer.getString();
        }
    }
}
