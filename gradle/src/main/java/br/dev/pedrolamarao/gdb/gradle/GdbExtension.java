package br.dev.pedrolamarao.gdb.gradle;

import org.gradle.api.Action;
import org.gradle.api.model.ObjectFactory;
import org.gradle.api.provider.Property;

import javax.inject.Inject;
import java.time.Duration;

public abstract class GdbExtension
{
    public abstract Property<String> getCommand ();

    public abstract Property<Duration> getTimeLimit ();

    @Inject
    protected abstract ObjectFactory getObjectFactory();

    public GdbExtension ()
    {
        getCommand().convention("gdb");
        getTimeLimit().convention(Duration.ofSeconds(1));
    }

    public GdbExecSpec spec (Action<? super GdbExecSpec> configure )
    {
        final GdbExecSpec spec = getObjectFactory().newInstance(GdbExecSpec.class);
        spec.getCommand().convention(getCommand());
        spec.getTimeLimit().convention(getTimeLimit());
        configure.execute(spec);
        return spec;
    }

    public GdbExec exec (Action<? super GdbExecSpec> configure) throws Exception
    {
        return GdbExec.fromSpec( spec(configure) );
    }

    public GdbExec exec (GdbExecSpec spec) throws Exception
    {
        return GdbExec.fromSpec(spec);
    }
}
