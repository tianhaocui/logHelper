package com.logHelper.handler;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.BeanDescription;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializationConfig;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.BeanPropertyWriter;
import com.fasterxml.jackson.databind.ser.BeanSerializerModifier;
import com.logHelper.annotation.Hidden;
import com.logHelper.util.HiddenBeanUtil;

import java.io.IOException;
import java.util.List;

/**
 * Description:
 * Author: cth
 * Created Date: 2024-12-05
 */
public class HiddenFieldHandler extends SimpleModule {

    public HiddenFieldHandler() {
        setSerializerModifier(new HiddenFieldSerializerModifier());
    }

    static class HiddenFieldSerializerModifier extends BeanSerializerModifier {
        @Override
        public List<BeanPropertyWriter> changeProperties(SerializationConfig config, BeanDescription beanDesc, List<BeanPropertyWriter> beanProperties) {
            for (BeanPropertyWriter writer : beanProperties) {
                Hidden hidden = writer.getAnnotation(Hidden.class);
                if (hidden != null) {
                    writer.assignSerializer(new HiddenFieldSerializer(hidden));
                }
            }
            return beanProperties;
        }
    }

    static class HiddenFieldSerializer extends JsonSerializer<Object> {
        private Hidden hidden;

        public Hidden getHidden() {
            return hidden;
        }

        public void setHidden(Hidden hidden) {
            this.hidden = hidden;
        }

        public HiddenFieldSerializer(Hidden hidden) {
            this.hidden = hidden;
        }

        @Override
        public void serialize(Object o, JsonGenerator gen, SerializerProvider serializerProvider) throws IOException {
            if (null ==o)
                return;
            String replace = HiddenBeanUtil.replace(o.toString(), hidden.dataType(), hidden.regexp());
            gen.writeString(replace); // 脱敏后的值，可根据需求修改
        }
    }
}