package io.github.jroden2.armco.provider;

@FunctionalInterface
public interface LlmProvider {
    String complete(String prompt);
}
