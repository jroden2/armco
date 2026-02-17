# armco

LLM-agnostic guardrails for structured content extraction. Define your fields, schema, and output
format. armco handles prompt construction, input sanitization, and LLM communication. Swap models
without changing your application code.

## Requirements

- Java 17+
- Maven 3.8+

## Installation
```xml
<dependency>
    <groupId>io.github.jroden2</groupId>
    <artifactId>armco</artifactId>
    <version>1.0.0</version>
</dependency>
```

## How It Works

armco is built around three concepts:

**LlmProvider** is a single-method interface you implement to connect any LLM. The library has no
SDK dependency and no opinion on which model you use.

**PromptTemplateSource** defines where your prompt template comes from, either hardcoded inline in
your source giving you git history on prompt changes, or loaded from a classpath file.

**ExtractionRequest** defines what to extract, in what format, and the expected output schema,
built using a fluent builder.

## Prompt Templates

Your prompt template supports four placeholders:

| Placeholder | Description |
|---|---|
| `{FIELDS}` | The fields to extract, rendered as a bullet list |
| `{FORMAT}` | The output format, e.g. `json` |
| `{SCHEMA}` | The expected output structure |
| `{CONTENT}` | The sanitized input content |

A default template is included at `/prompts/metadata_extraction.txt` on the classpath.
You can use this directly or provide your own.

## Input Sanitization

armco automatically sanitizes input content before it reaches the prompt. This includes truncating
input to 32,000 characters, stripping non-printable control characters, and removing the internal
delimiter string if it appears in the input. Your primary injection protection comes from the
structural delimiter wrapping in the prompt itself. Sanitization reinforces this rather than
relying on keyword blocklists.

## Template Sources

| Source | Method |
|---|---|
| Classpath file | `PromptTemplateSource.fromClasspath(path)` |
| Inline string | `PromptTemplateSource.fromString(template)` |

If your team prefers prompt changes to be visible in code review alongside the Java that uses them,
use `fromString` with a text block. If you prefer prompts to live separately from application code
and be swappable without recompiling, use `fromClasspath`.
```java
// Classpath file
GuardrailsClient client = new GuardrailsClient(
    provider,
    PromptTemplateSource.fromClasspath("/prompts/metadata_extraction.txt")
);

// Inline string
GuardrailsClient client = new GuardrailsClient(
    provider,
    PromptTemplateSource.fromString("""
        You are a metadata extraction engine.
        ...
        {FIELDS}
        {FORMAT}
        {SCHEMA}
        {CONTENT}
        """)
);
```
---

## Quick Start

The example below uses the OpenAI Java SDK, but any HTTP client or LLM SDK works. armco only
requires that you implement `LlmProvider`, which is a single method returning a String.

### 1. Add your LLM SDK dependency
```xml
<dependency>
    <groupId>com.openai</groupId>
    <artifactId>openai-java</artifactId>
    <version>2.4.0</version>
</dependency>
```

### 2. Implement LlmProvider
```java
OpenAIClient openAiClient = OpenAIOkHttpClient.builder()
    .apiKey(System.getenv("OPENAI_API_KEY"))
    .build();

LlmProvider provider = prompt -> {
    var response = openAiClient.chat().completions().create(
        ChatCompletionCreateParams.builder()
            .model(ChatModel.GPT_4_1)
            .addUserMessage(prompt)
            .build()
    );
    return response.choices().get(0).message().content().orElseThrow();
};
```

### 3. Build the client
```java
GuardrailsClient client = new GuardrailsClient(
    provider,
    PromptTemplateSource.fromClasspath("/prompts/metadata_extraction.txt")
);
```

### 4. Build and send the extraction request
```java
String schema = """
    {
      "title": string | null,
      "description": string | null,
      "keywords": string[],
      "human_context": string | null
    }
    """;

ExtractionRequest request = ExtractionRequest.builder(rawContent)
    .fields("title", "description", "keywords", "human_context")
    .outputFormat(OutputFormat.JSON)
    .schema(schema)
    .build();

String response = client.extract(request);
```

### 5. Parse the response

armco returns the raw LLM response as a String. Parsing is left to the caller since you control
the schema and output format.
```java
ObjectMapper objectMapper = new ObjectMapper();
MetadataResult result = objectMapper.readValue(response, MetadataResult.class);
```

### Spring Boot wiring
```java
@Configuration
public class ArmcoConfig {

    @Bean
    public LlmProvider llmProvider(@Value("${openai.api-key}") String apiKey) {
        OpenAIClient client = OpenAIOkHttpClient.builder().apiKey(apiKey).build();
        return prompt -> client.chat().completions().create(
            ChatCompletionCreateParams.builder()
                .model(ChatModel.GPT_4_1)
                .addUserMessage(prompt)
                .build()
        ).choices().get(0).message().content().orElseThrow();
    }

    @Bean
    public GuardrailsClient guardrailsClient(LlmProvider llmProvider) {
        return new GuardrailsClient(
            llmProvider,
            PromptTemplateSource.fromClasspath("/prompts/metadata_extraction.txt")
        );
    }
}
```

---

## License

MIT License — see [LICENSE](LICENSE) for details.
