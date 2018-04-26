package com.github.davidedmonds;


import org.apache.maven.plugin.testing.MojoRule;
import org.apache.maven.plugin.testing.WithoutMojo;

import org.junit.Rule;

import static org.junit.Assert.*;

import org.junit.Test;
import java.io.File;
import org.junit.rules.TemporaryFolder;

public class GenerateCheesesMojoTest {
    @Rule
    public MojoRule rule = new MojoRule() {
        @Override
        protected void before() throws Throwable {
        }

        @Override
        protected void after() {
        }
    };

    /**
     * @throws Exception if any
     */
    @Test
    public void createsOutputDirectory() throws Exception {
        File pom = new File("target/test-classes/project-to-test/");
        assertNotNull(pom);
        assertTrue(pom.exists());

        GenerateCheesesMojo generateCheesesMojo = (GenerateCheesesMojo) rule.lookupConfiguredMojo(pom, "generateCheeses");
        assertNotNull(generateCheesesMojo);
        generateCheesesMojo.execute();

        File outputDirectory = (File) rule.getVariableValueFromObject(generateCheesesMojo, "outputDirectory");
        assertNotNull(outputDirectory);
        assertTrue(outputDirectory.exists());
    }
}

