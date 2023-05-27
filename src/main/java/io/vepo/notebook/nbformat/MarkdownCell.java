package io.vepo.notebook.nbformat;

import java.util.Map;

public record MarkdownCell(String cell_type, Map<String, Object> metadata, String source) implements Cell {
    
}
