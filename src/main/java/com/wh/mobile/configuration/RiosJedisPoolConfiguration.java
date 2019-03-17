package com.wh.mobile.configuration;

import lombok.Data;

/**
 * Created by sam on 2018/10/25.
 */
@Data
public class RiosJedisPoolConfiguration {
    private int minIdle;
    private int maxIdle;
    private int maxActive;
    private int maxTotal;
    private Long maxWait;
}
