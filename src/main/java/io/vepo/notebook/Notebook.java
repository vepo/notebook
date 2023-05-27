package io.vepo.notebook;

import static io.undertow.Handlers.path;
import static io.undertow.Handlers.routing;
import static io.undertow.Undertow.builder;

import java.nio.ByteBuffer;
import java.util.concurrent.Callable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.undertow.server.HttpHandler;
import io.undertow.server.handlers.resource.ClassPathResourceManager;
import io.undertow.server.handlers.resource.ResourceHandler;
import io.undertow.util.Headers;
import io.undertow.util.StatusCodes;
import io.vepo.notebook.handlers.FileListHandler;
import io.vepo.notebook.handlers.FolderCreationHandler;
import io.vepo.notebook.handlers.NotebookCreationHandler;
import picocli.CommandLine;
import picocli.CommandLine.Command;

// https://zetcode.com/java/undertow/
@Command(name = "notebook", mixinStandardHelpOptions = true, version = "notebook 1.0", description = "Java Notebook")
public class Notebook implements Callable<Integer> {
    private static final Logger logger = LoggerFactory.getLogger(Notebook.class);

    public static void main(String[] args) {
        int exitCode = new CommandLine(new Notebook()).execute(args);
        System.exit(exitCode);
    }

    @Override
    public Integer call() throws Exception {
        logger.info("Initializing server...");
        var server = builder().addHttpListener(5555, "localhost")
                              .setHandler(path().addPrefixPath("/", resource(actions())))
                              .build();
        server.start();
        server.getWorker().awaitTermination();
        logger.info("Server shutdown!");
        return 0;
    }

    private static HttpHandler actions() {
        return routing().get("/api/folder/list", new FileListHandler())
                        .post("/api/folder/create", new FolderCreationHandler())
                        .post("/api/notebook/create", new NotebookCreationHandler())
                        .setFallbackHandler(exchange -> {
                            if (exchange.getRequestPath().startsWith("/api")) {
                                exchange.setStatusCode(StatusCodes.NOT_FOUND);
                                exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "application/json");
                                exchange.getResponseSender().send("{\"status\": 404}");
                            } else {
                                exchange.setStatusCode(StatusCodes.OK);
                                exchange.getResponseHeaders().put(Headers.CONTENT_TYPE, "text/html");
                                exchange.getResponseSender().send(ByteBuffer.wrap(Notebook.class.getClassLoader()
                                                                                                .getResourceAsStream("public/index.html")
                                                                                                .readAllBytes()));
                            }
                        });
    }

    private static HttpHandler resource(HttpHandler nextHandler) {
        return new ResourceHandler(new ClassPathResourceManager(Notebook.class.getClassLoader(), "public"),
                                   nextHandler).addWelcomeFiles("public/index.html");
    }
}
