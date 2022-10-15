package org.immersed.d2r.model;

import org.bytedeco.casclib.global.casclib;

public class CascUtil
{
    public static void check(boolean success)
    {
        if (!success)
        {
            throw new IllegalStateException(String.format("%d", casclib.GetCascError()));
        }
    }

    private CascUtil()
    {
    }
}
