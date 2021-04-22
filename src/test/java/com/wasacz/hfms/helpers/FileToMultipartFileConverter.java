package com.wasacz.hfms.helpers;

import org.springframework.mock.web.MockMultipartFile;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

public class FileToMultipartFileConverter {

    public static MockMultipartFile convertFileToMultiPart(String filePath) {
        try {
            File file = new File(filePath);
            FileInputStream input = new FileInputStream(file);
            return new MockMultipartFile("file", file.getName(), "text/plain", input.readAllBytes());
        } catch (IOException e) {
            throw new IllegalArgumentException("Something goes wrong while read the file " + e.getMessage());
        }
    }
}
