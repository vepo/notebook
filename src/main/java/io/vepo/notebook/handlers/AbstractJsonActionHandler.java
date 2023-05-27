package io.vepo.notebook.handlers;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Deque;
import java.util.Map;

import javax.imageio.IIOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import io.undertow.util.Headers;
import io.undertow.util.StatusCodes;

public abstract class AbstractJsonActionHandler<T> implements HttpHandler {
    private static final Logger logger = LoggerFactory.getLogger(AbstractJsonActionHandler.class);
    private final ObjectMapper mapper;
    private final Class<T> requestClass;

    public AbstractJsonActionHandler(Class<T> requestClass) {
        this.mapper = new ObjectMapper();
        this.requestClass = requestClass;
    }

    public abstract Response execute(T request, Map<String, Deque<String>> queryParameters);

    @SuppressWarnings("unchecked")
    private T readBody(byte[] data) throws StreamReadException, DatabindException, IOException {
        if (requestClass == NoBodyRequest.class) {
            return (T) new NoBodyRequest();
        } else {
            return mapper.readValue(data, requestClass);
        }
    }

    @Override
    public void handleRequest(HttpServerExchange exchange) throws Exception {
        exchange.getRequestReceiver()
                .receiveFullBytes((_exchange, bytes) -> {
                    logger.info("Read bytes={}", bytes.length);
                    try {
                        var response = execute(readBody(bytes), exchange.getQueryParameters());
                        exchange.setStatusCode(response.status());
                        exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/json");
                        exchange.getResponseSender().send(mapper.writeValueAsString(response), StandardCharsets.UTF_8);
                    } catch (IOException ioe) {
                        try {
                            logger.error("Error serializing request/response", ioe);
                            exchange.setStatusCode(StatusCodes.INTERNAL_SERVER_ERROR);
                            exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/json");
                            exchange.getResponseSender()
                                    .send(mapper.writeValueAsString(new InternalServerErrorResponse("Could not serialize request/response!")),
                                          StandardCharsets.UTF_8);
                        } catch (IOException ioe2) {
                            logger.error("Error serializing InternalServerErrorResponse", ioe2);
                            exchange.setStatusCode(StatusCodes.INTERNAL_SERVER_ERROR);
                            exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/json");
                            exchange.getResponseSender().send("{}", StandardCharsets.UTF_8);
                        }
                    }
                });
    }
}
