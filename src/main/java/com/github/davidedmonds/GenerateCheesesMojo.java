package com.github.davidedmonds;


import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.TypeSpec;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.stream.StreamSupport;
import javax.lang.model.element.Modifier;
import org.apache.commons.io.IOUtils;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;

@Mojo(name = "generateCheeses", defaultPhase = LifecyclePhase.GENERATE_SOURCES)
public class GenerateCheesesMojo extends AbstractMojo {
    @Parameter(defaultValue = "target/generated-sources/cheeses/", property = "outputDir", required = true)
    private File outputDirectory;

    @Parameter(defaultValue = "${project}")
    private MavenProject mavenProject;

    public void execute() throws MojoExecutionException {
        File f = outputDirectory;
        f.mkdirs();

        final String json;
        try {
            json = IOUtils.toString(getClass().getClassLoader().getResource("cheeses.json"), Charset.forName("UTF-8"));
            final Gson gson = new Gson();
            final JsonArray array = gson.fromJson(json, JsonArray.class);

            TypeSpec.Builder cheeseList = TypeSpec.classBuilder("CheeseList")
                    .addModifiers(Modifier.PUBLIC, Modifier.FINAL);

            StreamSupport.stream(Spliterators.spliteratorUnknownSize(array.iterator(), Spliterator.IMMUTABLE), false)
                    .map(cheese -> cheese.getAsJsonObject().get("itemLabel").getAsString())
                    .filter(cheese -> !cheese.matches("^Q\\d+"))
                    .sorted()
                    .forEach(cheese -> cheeseList.addField(
                            FieldSpec.builder(String.class, cheese
                                    .replace(" ", "_")
                                    .replace("'", "")
                                    .replace("-", "_")
                                    .replace("&", "")
                                    .toUpperCase())
                                    .addModifiers(Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL)
                                    .initializer("$S", cheese)
                                    .build())
                    );

            JavaFile cheeseFile = JavaFile.builder("com.github.davidedmonds.cheeses", cheeseList.build())
                    .build();

//            File cheeseDirectory = new File(outputDirectory, "com/github/davidedmonds/cheeses/");
//            cheeseDirectory.mkdirs();

            cheeseFile.writeTo(outputDirectory);


            mavenProject.addCompileSourceRoot(outputDirectory.getPath());
        } catch (IOException e) {
            throw new MojoExecutionException("It didn't work :(", e);
        }
    }
}
