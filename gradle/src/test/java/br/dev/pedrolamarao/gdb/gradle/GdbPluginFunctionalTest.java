package br.dev.pedrolamarao.gdb.gradle;

import lombok.var;
import org.gradle.testkit.runner.GradleRunner;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import static java.util.Arrays.asList;

public class GdbPluginFunctionalTest
{
    @TempDir
    File projectDir;

    private File settingsFile;

    private File buildFile;

    @BeforeEach
    public void prepare ()
    {
        settingsFile = new File(projectDir, "settings.gradle");
        buildFile = new File(projectDir, "build.gradle");
    }

    @Test
    public void extension () throws IOException
    {
        Files.write(settingsFile.toPath(), asList("rootProject.name = 'smoke'"));
        Files.write(buildFile.toPath(), asList(
            "plugins { id 'br.dev.pedrolamarao.gdb.gradle' }",
            "gdb.command = 'i386-gdb'"
        ));

        final var result = GradleRunner.create()
            .withProjectDir(projectDir)
            .withPluginClasspath()
            .build();
    }

    @EnabledIfSystemProperty(named="br.dev.pedrolamarao.gdb.test.command", matches = ".+")
    @Test
    public void interoperability () throws IOException
    {
        final String command = System.getProperty("br.dev.pedrolamarao.gdb.test.command", null);

        Files.write(settingsFile.toPath(), asList("rootProject.name = 'smoke'"));
        Files.write(buildFile.toPath(), asList(
            "plugins { id 'br.dev.pedrolamarao.gdb.gradle' }",
            "gdb.command = '" + command + "'",
            "def exec = gdb.exec { }",
            "exec.gdbExit { }"
        ));

        final var result = GradleRunner.create()
            .withProjectDir(projectDir)
            .withPluginClasspath()
            .build();
    }

    @Test
    public void smoke () throws IOException
    {
        Files.write(settingsFile.toPath(), asList("rootProject.name = 'smoke'"));
        Files.write(buildFile.toPath(), asList(
           "plugins { id 'br.dev.pedrolamarao.gdb.gradle' }"
        ));

        final var result = GradleRunner.create()
            .withProjectDir(projectDir)
            .withPluginClasspath()
            .build();
    }
}
