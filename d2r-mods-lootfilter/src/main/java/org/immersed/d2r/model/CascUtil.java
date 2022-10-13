package org.immersed.d2r.model;

import org.bytedeco.systems.global.windows;

public class CascUtil
{
    public static void check(boolean success)
    {
        if (!success)
        {
            throw new IllegalStateException(String.format("%d", windows.GetLastError()));
        }
    }

    private CascUtil()
    {
    }
}
