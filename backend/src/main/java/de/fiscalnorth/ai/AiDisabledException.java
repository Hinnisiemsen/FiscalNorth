package de.fiscalnorth.ai;

public class AiDisabledException extends RuntimeException {

    public AiDisabledException() {
        super("error.ai.disabled");
    }
}
