package io.github.jroden2.armco.sanitiser;

public class Sanitiser {

    private static final int MAX_INPUT_LENGTH = 32_000;
    private static final String DELIMITER = "<<<INPUT_TEXT>>>";

    public String sanitise(String input) {
        if (input == null) return "";

        return input
                .substring(0, Math.min(input.length(), MAX_INPUT_LENGTH))
                .replaceAll("[\\x00-\\x08\\x0B\\x0C\\x0E-\\x1F\\x7F]", "")
                .replace(DELIMITER, "[DELIMITER_REMOVED]")
                .trim();
    }
}