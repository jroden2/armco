package io.github.jroden2.armco.template;

import io.github.jroden2.armco.exception.ArmcoException;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

public class ClasspathPromptTemplateSource implements PromptTemplateSource {

    private final String resourcePath;

    public ClasspathPromptTemplateSource(String resourcePath) {
        if (resourcePath == null || resourcePath.isBlank()) {
            throw new IllegalArgumentException("resourcePath must not be blank");
        }
        this.resourcePath = resourcePath;
    }

    @Override
    public String load() {
        try (InputStream is = getClass().getResourceAsStream(resourcePath)) {
            if (is == null) {
                throw new ArmcoException("Classpath resource not found: " + resourcePath);
            }
            return new String(is.readAllBytes(), StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new ArmcoException("Failed to load prompt template from classpath: " + resourcePath, e);
        }
    }
}