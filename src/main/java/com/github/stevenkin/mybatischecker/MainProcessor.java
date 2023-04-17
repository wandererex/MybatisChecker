package com.github.stevenkin.mybatischecker;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.TypeElement;
import javax.tools.JavaFileManager;
import javax.tools.StandardLocation;
import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Set;

@SupportedAnnotationTypes("com.github.stevenkin.mybatischecker.MybatisChecker")
@SupportedSourceVersion(SourceVersion.RELEASE_8)
public class MainProcessor extends AbstractProcessor {

    @Override
    public synchronized void init(ProcessingEnvironment env) {
        try {
            Class<? extends ProcessingEnvironment> processingEnvironmentClass = env.getClass();
            Class<?> javacFilerClass = Class.forName("com.sun.tools.javac.processing.JavacFiler");
            Class<?> javacFileManagerClass = Class.forName("com.sun.tools.javac.file.JavacFileManager");
            Field fileManagerfield = javacFilerClass.getDeclaredField("fileManager");
            Field filerField = processingEnvironmentClass.getDeclaredField("filer");
            fileManagerfield.setAccessible(true);
            filerField.setAccessible(true);
            Object filer = filerField.get(env);
            Object fileManager = fileManagerfield.get(filer);
            Method getLocation = javacFileManagerClass.getDeclaredMethod("getLocation", JavaFileManager.Location.class);
            Iterable<? extends File> result = (Iterable<? extends File>) getLocation.invoke(fileManager, StandardLocation.SOURCE_PATH);
            for (File file : result) {
                if (file.exists()) {
                    String path = file.getAbsolutePath();
                    if (path.contains("target/generated-sources")) {
                        continue;
                    }
                    System.out.println("MainProcessor " + file.getAbsoluteFile());
                    //env.getMessager().printMessage(Diagnostic.Kind.NOTE, "MainProcessor " + file.getAbsoluteFile());
                }
            }

        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        return true;
    }
}
