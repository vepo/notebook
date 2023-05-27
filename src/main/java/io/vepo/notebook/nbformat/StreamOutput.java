package io.vepo.notebook.nbformat;

public record StreamOutput(String output_type, String name, String text) implements Output {
    
}
