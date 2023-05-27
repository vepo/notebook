package io.vepo.notebook.handlers;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Deque;
import java.util.Map;
import java.util.stream.Collectors;

public class FolderCreationHandler extends AbstractJsonActionHandler<CreateFolderRequest> {

    public FolderCreationHandler() {
        super(CreateFolderRequest.class);
    }

    @Override
    public Response execute(CreateFolderRequest request, Map<String, Deque<String>> queryParameters) {
        try {
            var folders = request.folder().split("/");
            var currPath = Paths.get(".", folders);
            currPath.resolve(request.name()).toFile().mkdirs();
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
