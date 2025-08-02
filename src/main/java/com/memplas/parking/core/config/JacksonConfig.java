package com.memplas.parking.core.config;

import com.fasterxml.jackson.annotation.JsonSetter;
import com.fasterxml.jackson.annotation.Nulls;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Configuration class for customizing the Jackson ObjectMapper used in the application.
 * <p>
 * This class provides a Spring-managed configuration to customize the behavior of Jackson's
 * ObjectMapper. It adjusts various serialization and deserialization settings to improve JSON
 * handling, particularly for date and time types, ensuring consistency and adherence to
 * application-specific formatting requirements.
 * <p>
 * Key Features: - Enables certain Jackson serialization features, such as - Pretty-printing of
 * output for better readability. - Ordering of map entries by their keys. - Disables the
 * serialization of dates as timestamps to improve date formatting output. - Includes the
 * JavaTimeModule for supporting Java 8 date/time types. - Adds a custom serializer for the Instant
 * class, serializing it in epoch seconds format.
 */
@Configuration
public class JacksonConfig {
//    @Bean
//    public ObjectMapper objectMapper(Jackson2ObjectMapperBuilder builder) {
//        ObjectMapper mapper = builder.createXmlMapper(false).build();
//        mapper.registerModule(new JavaTimeModule());
//        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
//        return mapper;
//    }

    @Bean
    public Jackson2ObjectMapperBuilderCustomizer jacksonCustomizer() {
        return builder ->
                builder
                        .featuresToEnable(
                                SerializationFeature.INDENT_OUTPUT,
                                SerializationFeature.ORDER_MAP_ENTRIES_BY_KEYS)
                        .featuresToDisable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
                        .modules(new JavaTimeModule(),
                                customInstantModule()).postConfigurer(
                                this::configureNullHandling); // override Instant serializer *after* JavaTimeModule
    }

    private SimpleModule customInstantModule() {
        SimpleModule module = new SimpleModule("CustomInstantModule");
        module.addSerializer(Instant.class, new InstantEpochSecondsSerializer());
        return module;
    }

    private void configureNullHandling(ObjectMapper mapper) {
        mapper.configOverride(String.class)
                .setSetterInfo(JsonSetter.Value.forValueNulls(Nulls.AS_EMPTY));

        mapper.configOverride(List.class)
                .setSetterInfo(JsonSetter.Value.forValueNulls(Nulls.AS_EMPTY));

        mapper.configOverride(Set.class)
                .setSetterInfo(JsonSetter.Value.forValueNulls(Nulls.AS_EMPTY));

        mapper.configOverride(Map.class)
                .setSetterInfo(JsonSetter.Value.forValueNulls(Nulls.AS_EMPTY));
    }

    public static class InstantEpochSecondsSerializer extends JsonSerializer<Instant> {
        @Override
        public void serialize(Instant value, JsonGenerator gen, SerializerProvider serializers)
                throws IOException {
            gen.writeNumber(value.getEpochSecond());
        }
    }

}
