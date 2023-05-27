package io.vepo.notebook.handlers;

import static java.util.Objects.isNull;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Deque;
import java.util.Map;
import java.util.stream.Collectors;

public class FileListHandler extends AbstractJsonActionHandler<NoBodyRequest> {

    public FileListHandler() {
        super(NoBodyRequest.class);
    }

    @Override
    public Response execute(NoBodyRequest request, Map<String, Deque<String>> queryParameters) {
        try {
            var folder = queryParameters.get("folder");
            var currPath = isNull(folder) || folder.isEmpty() ? Paths.get(".") : Paths.get(".", folder.peekFirst());
            return new FileListResponse(Files.list(currPath)
                                             .map(f -> new FileInfo(f.getFileName().toString(),
                                                                    f.toFile().isDirectory(),
                                                                    f.toFile().lastModified()))
                                             .collect(Collectors.toList()));
        } catch (IOException e) {
            return new InternalServerErrorResponse("Could not access the current folder contents!");
        }
    }

}
