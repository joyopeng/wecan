package com.gofirst.scenecollection.evidence.utils;

import android.hardware.Camera.Size;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class CameraUtils {
    
    public static Size getProperSize(List<Size> sizeList, float displayRatio)
    {
        //先对传进来的size列表进行排序
        Collections.sort(sizeList, new SizeComparator());
        
        Size result = null;
        for(Size size: sizeList)
        {
            float curRatio =  ((float)size.width) / size.height;
            if(curRatio - displayRatio == 0)
            {
                result = size;
            }
        }
        if(null == result)
        {
            for(Size size: sizeList)
            {
                float curRatio =  ((float)size.width) / size.height;
                if(curRatio == 9f/16)
                {
                    result = size;
                }
            }
        }
        return result;
    }
    
    static class SizeComparator implements Comparator<Size>
    {

        @Override
        public int compare(Size lhs, Size rhs) {
            // TODO Auto-generated method stub
            Size size1 = lhs;
            Size size2 = rhs;
            if(size1.width < size2.width 
                    || size1.width == size2.width && size1.height < size2.height)
            {
                return -1;
            }
            else if(!(size1.width == size2.width && size1.height == size2.height))
            {
                return 1;
            }
            return 0;
        }
        
    }
}
