package io.github.jroden2.armco.template;

import io.github.jroden2.armco.model.ExtractionRequest;
import java.util.stream.Collectors;

public class PromptTemplate {

    private final PromptTemplateSource source;
    private volatile String loaded;

    public PromptTemplate(PromptTemplateSource source) {
        this.source = source;
    }

    public String fill(ExtractionRequest request, String sanitizedContent) {
        if (loaded == null) {
            synchronized (this) {
                if (loaded == null) {
                    loaded = source.load();
                }
            }
        }

        String fieldList = request.getFields().stream()
                .map(f -> "- " + f)
                .collect(Collectors.joining("\n"));

        return loaded
                .replace("{FIELDS}", fieldList)
                .replace("{FORMAT}", request.getOutputFormat().toPromptLabel())
                .replace("{SCHEMA}", request.getSchema())
                .replace("{CONTENT}", sanitizedContent);
    }
}