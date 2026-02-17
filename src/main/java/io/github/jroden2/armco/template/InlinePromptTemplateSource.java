package io.github.jroden2.armco.template;

public class InlinePromptTemplateSource implements PromptTemplateSource {

    private final String template;

    public InlinePromptTemplateSource(String template) {
        if (template == null || template.isBlank()) {
            throw new IllegalArgumentException("Inline prompt template must not be blank");
        }
        this.template = template;
    }

    @Override
    public String load() {
        return template;
    }
}
