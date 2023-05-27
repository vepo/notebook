package io.vepo.notebook.nbformat;

import java.util.List;

public record NotebookData(Metadata metadata, int nbformat, int nbformat_minor, List<Cell> cells) {
    
}
