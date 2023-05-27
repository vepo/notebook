package io.vepo.notebook.handlers;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Deque;
import java.util.Map;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.vepo.notebook.nbformat.NotebookData;

public class NotebookCreationHandler extends AbstractJsonActionHandler<CreateNotebookRequest> {

    private final ObjectMapper mapper;

    public NotebookCreationHandler() {
        super(CreateNotebookRequest.class);
        this.mapper = new ObjectMapper();
    }

    @Override
    public Response execute(CreateNotebookRequest request, Map<String, Deque<String>> queryParameters) {
        try {
            var currPath = Paths.get(".", request.folder().split("/"));
            var data = new NotebookData(null, 4, 5, new ArrayList<>());
            mapper.writeValue(currPath.resolve(request.name() + ".ipynb").toFile(), data);
            return new FileListResponse(Files.list(currPath)
                                             .map(f -> new FileInfo(f.getFileName().toString(),
                                                                    f.toFile().isDirectory(),
                                                                    f.toFile().lastModified()))
                                             .collect(Collectors.toList()));
        } catch (IOException e) {
            return new InternalServerErrorResponse("Could not write notebook file!");
        }
    }

}
