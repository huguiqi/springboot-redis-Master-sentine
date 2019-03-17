package com.wh.mobile.configuration;

import lombok.Data;

/**
 * Created by sam on 2018/10/25.
 */
@Data
public class RiosMasterJedisProperty extends RiosJedisProperty {
    private int database;
    private Long timeout;

    private RiosJedisPoolConfiguration pool;
}
