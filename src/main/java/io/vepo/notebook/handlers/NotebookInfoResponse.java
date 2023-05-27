package io.vepo.notebook.handlers;

import io.undertow.util.StatusCodes;
import io.vepo.notebook.nbformat.NotebookData;

public record NotebookInfoResponse(NotebookData data) implements Response {

    @Override
    public int status() {
        return StatusCodes.OK;
    }

}
