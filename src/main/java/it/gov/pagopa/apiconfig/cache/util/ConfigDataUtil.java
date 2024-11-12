package it.gov.pagopa.apiconfig.cache.util;

import it.gov.pagopa.apiconfig.cache.model.node.v1.ConfigDataV1;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.modelmapper.ModelMapper;
import org.springframework.boot.context.config.ConfigData;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;

@Slf4j
public class ConfigDataUtil {

    public static ConfigDataV1 cacheToConfigDataV1(Map<String,Object> inMemoryCache, String[] keys){
        ConfigDataV1 configDataV1 = new ConfigDataV1();
        Arrays.stream(keys).forEach(k->{
            Method sumInstanceMethod = ReflectionUtils.findMethod(ConfigDataV1.class,"set"+(StringUtils.capitalize(k)),null);
            if (sumInstanceMethod != null) {
                try {
                    Type parameterType = sumInstanceMethod.getGenericParameterTypes()[0];
                    Class<?> valueType;
                    if (parameterType == Map.class) {//parameterType instanceof ParameterizedType) {
                        ParameterizedType parameterizedType = (ParameterizedType) parameterType;

                        // Get the actual type arguments for the parameterized Map type
                        Type[] typeArguments = parameterizedType.getActualTypeArguments();
                        valueType = (Class<?>) typeArguments[1];

                        Map<String, Object> data = (Map<String, Object>) inMemoryCache.get(k);

                        data.forEach((key, value) -> {
                            // value is a Station
                            ModelMapper mapper = new ModelMapper();
                            Object dataMapped = mapper.map(value, valueType);
                            data.put(key, dataMapped);
                        });
                        sumInstanceMethod.invoke(configDataV1, data);
                    }
                    else {
                        sumInstanceMethod.invoke(configDataV1, inMemoryCache.get(k));
                    }
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                } catch (InvocationTargetException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        return configDataV1;
    }
}
