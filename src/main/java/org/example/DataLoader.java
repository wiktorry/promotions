package org.example;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class DataLoader {
    private final ObjectMapper mapper = new ObjectMapper();

    public <T> List<T> loadJson(String path, TypeReference<List<T>> typeReference) {
        List<T> objects;
        try {
            File file = new File(path);
            objects = mapper.readValue(file, typeReference);
        } catch (IOException e) {
            System.out.println("Problem while reading file: " + e.getMessage());
            return null;
        }
        return objects;
    }
}
