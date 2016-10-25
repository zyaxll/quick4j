package com.eliteams.quick4j.core.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

/**
 * Created by zya on 16/10/25.
 */
public class JSONUtil {
    private static Logger logger = LoggerFactory.getLogger(JSONUtil.class);

    private static ObjectMapper objectMapper;

    /**
     * 懒惰单例模式得到ObjectMapper实例 此对象为Jackson的核心
     */
    private static ObjectMapper getMapper() {
        if (objectMapper == null) {
            synchronized (ObjectMapper.class) {
                if (objectMapper == null) {
                    objectMapper = new ObjectMapper();
                    // to force escaping of non-ASCII characters:
                    objectMapper.configure(JsonGenerator.Feature.ESCAPE_NON_ASCII, false);
                    // to config null value not set to json string
                    objectMapper.configure(SerializationFeature.WRITE_NULL_MAP_VALUES, false);
                    // to config unknown propteries not set to json String
                    objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
                    // to write java.util.Date, Calendar as number (timestamp):
                    objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
                    // to accept empty string as null object
                    objectMapper.enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT);
                    // to accept single value as array
                    // objectMapper.disable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY);
                    objectMapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);

                    objectMapper.setSerializationInclusion(Include.NON_NULL);
                }
            }
        }
        return objectMapper;
    }

    /**
     * JSON对象序列化
     */
    public static String toJSON(Object obj) {
        String json = null;
        ObjectMapper mapper = getMapper();

        try {
            json = mapper.writeValueAsString(obj);
            return json;
        } catch (JsonGenerationException jge) {
            logger.error("JSON error" + jge.getMessage());
        } catch (IOException ioe) {
            logger.error("JSON error" + ioe.getMessage());
        }
        return null;
    }

    /**
     * JSON对象反序列化
     */
    public static <T> T fromJSON(String json, Class<T> clazz) {
        try {
            ObjectMapper mapper = getMapper();
            T value = mapper.readValue(json, clazz);
            return value;
        } catch (JsonParseException jpe) {
            logger.warn(String.format("JsonParseException, caurse:%s", jpe.getMessage()));
        } catch (JsonMappingException jme) {
            logger.error(String.format("JsonParseException, caurse:%s", jme.getMessage()));
        } catch (IOException ioe) {
            logger.error(String.format("JsonParseException, caurse:%s", ioe.getMessage()));
        }
        return null;
    }

    /**
     * JSON对象反序列化
     */
    public static <T> T fromJSON(String json, TypeReference<T> valueTypeRef) {
        try {
            ObjectMapper mapper = getMapper();
            T value = mapper.readValue(json, valueTypeRef);
            return value;
        } catch (JsonParseException jpe) {
            logger.warn(String.format("JsonParseException, caurse:%s", jpe.getMessage()));
        } catch (JsonMappingException jme) {
            logger.error(String.format("JsonParseException, caurse:%s", jme.getMessage()));
        } catch (IOException ioe) {
            logger.error(String.format("JsonParseException, caurse:%s", ioe.getMessage()));
        }
        return null;
    }

    /**
     * 从json中读取tagPath处的值 tagPath用 :分隔
     *
     * @param json
     * @param tagPath
     * @return
     * @throws Exception
     */
    public static List<String> readValueFromJson(String json, String tagPath) throws Exception {
        // 返回值
        List<String> value = new ArrayList<String>();
        if (ObjectUtil.isEmpty(json) || (ObjectUtil.isEmpty(tagPath))) {
            return value;
        }
        ObjectMapper mapper = getMapper();
        String[] path = tagPath.split(":");
        JsonNode node = mapper.readTree(json);
        getJsonValue(node, path, value, 1);
        return value;
    }

    private static void getJsonValue(JsonNode node, String[] path, List<String> values, int nextIndex) {
        if (ObjectUtil.isEmpty(node)) {
            return;
        }
        // 是路径的最后就直接取值
        if (nextIndex == path.length) {
            if (node.isArray()) {
                for (int i = 0; i < node.size(); i++) {
                    JsonNode child = node.get(i).get(path[nextIndex - 1]);
                    if (ObjectUtil.isEmpty(child)) {
                        continue;
                    }
                    values.add(child.toString().replace("\"", ""));
                }
            } else {
                JsonNode child = node.get(path[nextIndex - 1]);
                if (!ObjectUtil.isEmpty(child)) {
                    values.add(child.toString().replace("\"", ""));
                }
            }
            return;
        }
        // 判断是Node下是集合还是一个节点
        node = node.get(path[nextIndex - 1]);
        if (node.isArray()) {
            for (int i = 0; i < node.size(); i++) {
                getJsonValue(node.get(i), path, values, nextIndex + 1);
            }
        } else {
            getJsonValue(node, path, values, nextIndex + 1);
        }
    }
}
