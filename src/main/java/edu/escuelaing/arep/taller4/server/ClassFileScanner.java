package edu.escuelaing.arep.taller4.server;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ClassFileScanner {
    private static final String BASE_DIR = "/arep/bin/classes";
    private static List<String> classPaths = new ArrayList<>();


    public static void listClasses(){
        File baseDirectory = new File(BASE_DIR);
        classPaths = new ArrayList<>();
        
        if (baseDirectory.exists() && baseDirectory.isDirectory()) {
            scanDirectory(baseDirectory, baseDirectory.getAbsolutePath(), classPaths);
        }
    }

    public static List<String> getClassPaths() {
        return classPaths;
    }

    private static void scanDirectory(File directory, String basePath, List<String> classPaths) {
        for (File file : directory.listFiles()) {
            if (file.isDirectory()) {
                scanDirectory(file, basePath, classPaths);
            } else if (file.getName().endsWith(".class")) {
                String classPath = file.getAbsolutePath()
                        .replace(basePath + File.separator, "")
                        .replace(File.separator, ".")
                        .replace(".class", "");
                classPaths.add(classPath);
            }
        }
    }
}