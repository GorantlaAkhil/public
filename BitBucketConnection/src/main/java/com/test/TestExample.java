package com.test;


import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

public class TestExample {

    public static void main(String[] args) throws IOException {

        String baseName = "gitClone3";
        File tempDir = new File("C:\\Users\\Thota Narendra\\WorkDetails\\Test\\sample");
        FileUtils.forceDelete(tempDir);
    }
}
