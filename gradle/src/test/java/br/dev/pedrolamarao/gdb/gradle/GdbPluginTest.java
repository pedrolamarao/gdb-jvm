package br.dev.pedrolamarao.gdb.gradle;

import org.gradle.api.Project;
import org.gradle.testfixtures.ProjectBuilder;
import org.junit.jupiter.api.Test;

import java.time.Duration;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

public class GdbPluginTest
{
    @Test
    public void conventions ()
    {
        final Project project = ProjectBuilder.builder().build();
        project.getPlugins().apply(GdbPlugin.class);

        final GdbExtension extension = project.getExtensions().getByType(GdbExtension.class);
        assertThat(extension.getCommand().get(), equalTo("gdb"));
        assertThat(extension.getTimeLimit().get(), equalTo(Duration.ofSeconds(1)));

        final GdbExecSpec spec = extension.spec(x -> {});
        assertThat(spec.getCommand().get(), equalTo("gdb"));
        assertThat(spec.getTimeLimit().get(), equalTo(Duration.ofSeconds(1)));

        extension.getCommand().convention("i386-gdb");
        extension.getTimeLimit().convention(Duration.ofSeconds(2));

        assertThat(spec.getCommand().get(), equalTo("i386-gdb"));
        assertThat(spec.getTimeLimit().get(), equalTo(Duration.ofSeconds(2)));
    }

    @Test
    public void smoke ()
    {
        final Project project = ProjectBuilder.builder().build();
        project.getPlugins().apply(GdbPlugin.class);
        assertThat( project.getExtensions().findByName("gdb"), notNullValue() );

        final GdbExtension extension = project.getExtensions().getByType(GdbExtension.class);
        final GdbExecSpec spec = extension.spec(x -> { assertThat(x, notNullValue()); } );
        assertThat( spec, notNullValue() );
    }
}
