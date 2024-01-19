package it.gov.pagopa.apiconfig.cache.util;

import it.gov.pagopa.apiconfig.cache.model.node.v1.ConfigDataV1;
import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.context.config.ConfigData;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Map;

public class ConfigDataUtil {

    public static ConfigDataV1 cacheToConfigDataV1(Map<String,Object> inMemoryCache, String[] keys){
        ConfigDataV1 configDataV1 = new ConfigDataV1();
        Arrays.stream(keys).forEach(k->{
            Method sumInstanceMethod = ReflectionUtils.findMethod(ConfigDataV1.class,"set"+(StringUtils.capitalize(k)),null);
            try {
                sumInstanceMethod.invoke(configDataV1,inMemoryCache.get(k));
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            } catch (InvocationTargetException e) {
                throw new RuntimeException(e);
            }
        });
        return configDataV1;
    }
}
