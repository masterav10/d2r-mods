package org.immersed.d2r.model;

import static org.bytedeco.casclib.global.casclib.*;
import static org.immersed.d2r.model.CascUtil.*;

import java.io.Closeable;
import java.io.File;
import java.nio.ByteBuffer;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

import org.bytedeco.casclib.CASC_FIND_DATA;
import org.bytedeco.casclib.global.casclib;
import org.bytedeco.javacpp.IntPointer;
import org.bytedeco.javacpp.Pointer;
import org.bytedeco.javacpp.PointerPointer;
import org.bytedeco.javacpp.SizeTPointer;
import org.bytedeco.systems.windows.HANDLE;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ReflectionUtils;

/**
 * Provides convenience functions for wrapping around a CascStorage.
 * 
 * @author Dan Avila
 */
public class CascDatabase implements Closeable
{
    private static final Logger LOG = LoggerFactory.getLogger(CascDatabase.class);

    private final HANDLE hStorage;
    private final List<CascFile> files;

    public CascDatabase(Path root)
    {
        String name = root.toAbsolutePath()
                          .toString();

        if (!name.endsWith(File.separator))
        {
            name += File.separator;
        }

        try (PointerPointer<HANDLE> handlePtr = new PointerPointer<>(1L))
        {
            check(CascOpenStorage(name.toCharArray(), 0, handlePtr));
            hStorage = handlePtr.get(HANDLE.class);
        }

        this.files = new ArrayList<>();
        final CascFile.Builder builder = new CascFile.Builder();

        String szMask = null;
        CASC_FIND_DATA pFindData = new CASC_FIND_DATA();
        char[] szListFile = null;

        Pointer hFind = CascFindFirstFile(hStorage, szMask, pFindData, szListFile);
        builder.mergeFromCasc(pFindData);

        if (pFindData.bFileAvailable() != 0)
        {
            this.files.add(builder.build());
        }
        while (CascFindNextFile(hFind, pFindData))
        {
            builder.mergeFromCasc(pFindData);

            if (pFindData.bFileAvailable() != 0)
            {
                this.files.add(builder.build());
            }
        }
    }

    public void printOutStorageInfo()
    {
        final int InfoClass = CascStorageFeatures;
        final IntPointer pvStorageInfo = new IntPointer(1L);
        final int cbStorageInfo = (int) pvStorageInfo.capacity() * Integer.BYTES;
        final SizeTPointer pcbLengthNeeded = new SizeTPointer(1L);

        check(CascGetStorageInfo(hStorage, InfoClass, pvStorageInfo, cbStorageInfo, pcbLengthNeeded));
        
        check(CascGetStorageInfo(hStorage, InfoClass, pvStorageInfo, cbStorageInfo, pcbLengthNeeded));

        int value = pvStorageInfo.get();

        LOG.info("The following feaatures are supported({}):", value);
        ReflectionUtils.doWithFields(casclib.class, f ->
        {
            String name = f.getName();
            if (name.contains("_FEATURE_"))
            {
                int bitField = (int) f.get(null);
                
                if ((value & bitField) != 0)
                {
                    LOG.info("  {}", name);
                }
            }
        });

    }

    public HANDLE hStorage()
    {
        return hStorage;
    }

    public List<CascFile> getFiles()
    {
        return this.files;
    }

    @Override
    public void close()
    {
        check(CascCloseStorage(hStorage));
    }

}
