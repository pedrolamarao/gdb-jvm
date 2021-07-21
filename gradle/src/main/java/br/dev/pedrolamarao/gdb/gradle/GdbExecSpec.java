package br.dev.pedrolamarao.gdb.gradle;

import br.dev.pedrolamarao.gdb.GdbHandler;
import org.gradle.api.provider.ListProperty;
import org.gradle.api.provider.Property;

import java.time.Duration;

public abstract class GdbExecSpec
{
    public abstract Property<String> getCommand ();

    public abstract ListProperty<GdbHandler> getHandlers ();

    public abstract Property<Duration> getTimeLimit ();
}
