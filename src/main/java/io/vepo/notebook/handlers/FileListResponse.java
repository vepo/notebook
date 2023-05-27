package io.vepo.notebook.handlers;

import java.util.List;

import io.undertow.util.StatusCodes;

public record FileListResponse(List<FileInfo> contents) implements Response {

    @Override
    public int status() {
        return StatusCodes.OK;
    }
    
}
