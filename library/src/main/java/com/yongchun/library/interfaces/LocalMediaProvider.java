package com.yongchun.library.interfaces;

import com.yongchun.library.model.LocalMedia;

/**
 * @author hzzhengrui
 * @Date 16/10/17
 * @Description
 */
public interface LocalMediaProvider {
    public LocalMedia getLocalMediaForPosition(int position);
    public int getCount();
}
