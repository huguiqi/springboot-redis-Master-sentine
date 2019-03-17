package com.wh.mobile.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Created by sam on 2018/10/25.
 */
@Component
@ConfigurationProperties(prefix = "bzy.jedis")
@Data
public class RiosJedisConfiguration {

    private RiosMasterJedisProperty master;
    private RiosJedisProperty slave1;
    private RiosJedisProperty slave2;
    private RiosJedisProperty sentinel;
}
