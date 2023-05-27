package io.vepo.notebook.handlers;

import io.undertow.util.StatusCodes;

public record InternalServerErrorResponse(String message) implements Response {

    @Override
    public int status() {
        return StatusCodes.INTERNAL_SERVER_ERROR;
    }

}
