package io.github.jroden2.armco.model;

public enum OutputFormat {
    JSON,
    XML,
    CSV;

    public String toPromptLabel() {
        return name().toLowerCase();
    }
}
