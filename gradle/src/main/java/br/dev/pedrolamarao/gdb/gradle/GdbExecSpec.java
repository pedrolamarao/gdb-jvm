package br.dev.pedrolamarao.gdb.gradle;

import br.dev.pedrolamarao.gdb.GdbHandler;
import org.gradle.api.provider.ListProperty;
import org.gradle.api.provider.Property;

import java.io.OutputStream;
import java.time.Duration;

public abstract class GdbExecSpec
{
    public abstract Property<String> getCommand ();

    public abstract Property<OutputStream> getDebugOutput ();

    public abstract ListProperty<GdbHandler> getHandlers ();

    public abstract Property<Duration> getTimeLimit ();
}
