package io.github.jroden2.armco.template;

public interface PromptTemplateSource {

    String load();

    static PromptTemplateSource fromClasspath(String resourcePath) {
        return new ClasspathPromptTemplateSource(resourcePath);
    }

    static PromptTemplateSource fromString(String template) {
        return new InlinePromptTemplateSource(template);
    }
}
