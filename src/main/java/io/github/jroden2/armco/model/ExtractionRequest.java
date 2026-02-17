package io.github.jroden2.armco.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ExtractionRequest {

    private final String rawContent;
    private final List<String> fields;
    private final OutputFormat outputFormat;
    private final String schema;

    private ExtractionRequest(Builder builder) {
        this.rawContent = builder.rawContent;
        this.fields = Collections.unmodifiableList(builder.fields);
        this.outputFormat = builder.outputFormat;
        this.schema = builder.schema;
    }

    public String getRawContent() { return rawContent; }
    public List<String> getFields() { return fields; }
    public OutputFormat getOutputFormat() { return outputFormat; }
    public String getSchema() { return schema; }

    public static Builder builder(String rawContent) {
        return new Builder(rawContent);
    }

    public static class Builder {

        private final String rawContent;
        private List<String> fields = new ArrayList<>();
        private OutputFormat outputFormat = OutputFormat.JSON;
        private String schema;

        private Builder(String rawContent) {
            this.rawContent = rawContent;
        }

        public Builder fields(String... fields) {
            this.fields = Arrays.asList(fields);
            return this;
        }

        public Builder fields(List<String> fields) {
            this.fields = new ArrayList<>(fields);
            return this;
        }

        public Builder outputFormat(OutputFormat outputFormat) {
            this.outputFormat = outputFormat;
            return this;
        }

        public Builder schema(String schema) {
            this.schema = schema;
            return this;
        }

        public ExtractionRequest build() {
            if (rawContent == null || rawContent.isBlank()) {
                throw new IllegalArgumentException("rawContent must not be blank");
            }
            if (fields.isEmpty()) {
                throw new IllegalArgumentException("At least one field must be specified");
            }
            if (schema == null || schema.isBlank()) {
                throw new IllegalArgumentException("schema must not be blank");
            }
            return new ExtractionRequest(this);
        }
    }
}
