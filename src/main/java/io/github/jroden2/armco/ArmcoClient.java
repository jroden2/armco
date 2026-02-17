package io.github.jroden2.armco;

import io.github.jroden2.armco.model.ExtractionRequest;
import io.github.jroden2.armco.provider.LlmProvider;
import io.github.jroden2.armco.sanitiser.Sanitiser;
import io.github.jroden2.armco.template.PromptTemplate;
import io.github.jroden2.armco.template.PromptTemplateSource;

public class ArmcoClient {

    private final LlmProvider llmProvider;
    private final Sanitiser sanitiser;
    private final PromptTemplate promptTemplate;

    public ArmcoClient(LlmProvider llmProvider, PromptTemplateSource templateSource) {
        this.llmProvider = llmProvider;
        this.sanitiser = new Sanitiser();
        this.promptTemplate = new PromptTemplate(templateSource);
    }

    public String extract(ExtractionRequest request) {
        String sanitized = sanitiser.sanitise(request.getRawContent());
        String prompt = promptTemplate.fill(request, sanitized);
        return llmProvider.complete(prompt);
    }
}
