package br.dev.pedrolamarao.gdb.gradle;

import lombok.var;
import org.gradle.api.Project;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@EnabledIfSystemProperty(named="br.dev.pedrolamarao.gdb.test.command", matches=".+")
public class GdbPluginInteroperabilityTest
{
    static final String command = System.getProperty("br.dev.pedrolamarao.gdb.test.command", null);

    GdbExtension extension;

    Project project;

    @BeforeEach
    public void configure ()
    {
        project = ProjectBuilder.builder().build();
        project.getPlugins().apply(GdbPlugin.class);
        extension = project.getExtensions().getByType(GdbExtension.class);
    }

    @Test
    public void gdbExit () throws Exception
    {
        final var exec = extension.exec( spec -> spec.getCommand().set(command) );
        final var result = exec.gdbExit( spec -> {} );
        assertThat(result.content().type(), equalTo("exit"));
    }

    @Test
    public void gdbSet () throws Exception
    {
        final var exec = extension.exec( spec -> spec.getCommand().set(command) );
        final var result = exec.gdbSet("foo", 16, spec -> {} );
        assertThat(result.content().type(), equalTo("done"));
    }

    @Test
    public void interpreterExec () throws Exception
    {
        final var exec = extension.exec( spec -> spec.getCommand().set(command) );
        final var result = exec.interpreterExec("console", "help" );
        assertThat(result.content().type(), equalTo("done"));
    }
}
