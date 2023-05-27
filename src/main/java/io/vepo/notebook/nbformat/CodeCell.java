package io.vepo.notebook.nbformat;

import java.util.List;
import java.util.Map;

public record CodeCell(String cell_type,
        Integer execution_count,
        Map<String, Object> metadata,
        String source,
        List<Output> outputs) implements Cell {

}
