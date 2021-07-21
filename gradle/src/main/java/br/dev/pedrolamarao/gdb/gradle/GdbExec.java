package br.dev.pedrolamarao.gdb.gradle;

import br.dev.pedrolamarao.gdb.Gdb;
import br.dev.pedrolamarao.gdb.GdbHandler;
import br.dev.pedrolamarao.gdb.mi.GdbMiMessage;
import lombok.var;
import org.gradle.api.Action;

import java.io.IOException;
import java.time.Duration;
import java.util.concurrent.TimeUnit;

public class GdbExec implements AutoCloseable
{
    private final Gdb gdb;

    private final Duration timeLimit;

    GdbExec (Gdb gdb, Duration timeLimit)
    {
        this.gdb = gdb;
        this.timeLimit = timeLimit;
    }

    public static GdbExec fromSpec (GdbExecSpec spec) throws IOException
    {
        final var builder = Gdb.builder();
        builder.command(spec.getCommand().get());
        spec.getHandlers().get().forEach(builder::handler);
        final var gdb = builder.start();
        return new GdbExec(gdb, spec.getTimeLimit().get());
    }

    public void close ()
    {
        gdb.close();
    }

    public int exitValue () { return gdb.exitValue(); }

    public boolean waitFor (long time, TimeUnit unit) throws InterruptedException
    {
        return gdb.waitFor(time, unit);
    }

    //

    public GdbMiMessage.RecordMessage breakInsertAtSymbol ( String symbol, Action<? super Gdb.GdbBreakInsertBuilder> configure ) throws Exception
    {
        final var builder = gdb.breakInsertAtSymbol(symbol);
        configure.execute(builder);
        final var result = builder.go().get(timeLimit.toMillis(), TimeUnit.MILLISECONDS);
        if (result.content().type().contentEquals("error")) {
            final var message = result.content().properties().get("msg", String.class);
            throw new RuntimeException("gdb: failure: " + message);
        }
        return result;
    }

    public GdbMiMessage.RecordMessage breakWatch ( String symbol, Action<? super Gdb.GdbBreakWatchBuilder> configure ) throws Exception
    {
        final var builder = gdb.breakWatch(symbol);
        configure.execute(builder);
        final var result = builder.go().get(timeLimit.toMillis(), TimeUnit.MILLISECONDS);
        if (result.content().type().contentEquals("error")) {
            final var message = result.content().properties().get("msg", String.class);
            throw new RuntimeException("gdb: failure: " + message);
        }
        return result;
    }

    public void handle (GdbHandler handler)
    {
        gdb.handle(handler);
    }

    public GdbMiMessage.RecordMessage execContinue ( Action<? super Gdb.GdbExecContinueBuilder> configure ) throws Exception
    {
        final var builder = gdb.execContinue();
        configure.execute(builder);
        final var result = builder.go().get(timeLimit.toMillis(), TimeUnit.MILLISECONDS);
        if (result.content().type().contentEquals("error")) {
            final var message = result.content().properties().get("msg", String.class);
            throw new RuntimeException("gdb: failure: " + message);
        }
        return result;
    }

    public GdbMiMessage.RecordMessage execRun ( Action<? super Gdb.GdbExecRunBuilder> configure ) throws Exception
    {
        final var builder = gdb.execRun();
        configure.execute(builder);
        final var result = builder.go().get(timeLimit.toMillis(), TimeUnit.MILLISECONDS);
        if (result.content().type().contentEquals("error")) {
            final var message = result.content().properties().get("msg", String.class);
            throw new RuntimeException("gdb: failure: " + message);
        }
        return result;
    }

    public GdbMiMessage.RecordMessage fileExecAndSymbols ( String path, Action<? super Gdb.GdbFileExecAndSymbolsBuilder> configure ) throws Exception
    {
        final var builder = gdb.fileExecAndSymbols(path);
        configure.execute(builder);
        final var result = builder.go().get(timeLimit.toMillis(), TimeUnit.MILLISECONDS);
        if (result.content().type().contentEquals("error")) {
            final var message = result.content().properties().get("msg", String.class);
            throw new RuntimeException("gdb: failure: " + message);
        }
        return result;
    }

    public GdbMiMessage.RecordMessage gdbExit ( Action<? super Gdb.GdbExitBuilder> configure ) throws Exception
    {
        final var builder = gdb.gdbExit();
        configure.execute(builder);
        final var result = builder.go().get(timeLimit.toMillis(), TimeUnit.MILLISECONDS);
        if (result.content().type().contentEquals("error")) {
            final var message = result.content().properties().get("msg", String.class);
            throw new RuntimeException("gdb: failure: " + message);
        }
        return result;
    }

    public GdbMiMessage.RecordMessage gdbSet ( String name, Object value, Action<? super Gdb.GdbSetBuilder> configure ) throws Exception
    {
        final var builder = gdb.gdbSet(name, value.toString());
        configure.execute(builder);
        final var result = builder.go().get(timeLimit.toMillis(), TimeUnit.MILLISECONDS);
        if (result.content().type().contentEquals("error")) {
            final var message = result.content().properties().get("msg", String.class);
            throw new RuntimeException("gdb: failure: " + message);
        }
        return result;
    }

    public GdbMiMessage.RecordMessage interpreterExec ( String interpreter, String... command ) throws Exception
    {
        final var builder = gdb.interpreterExec(interpreter, command);
        final var result = builder.go().get(timeLimit.toMillis(), TimeUnit.MILLISECONDS);
        if (result.content().type().contentEquals("error")) {
            final var message = result.content().properties().get("msg", String.class);
            throw new RuntimeException("gdb: failure: " + message);
        }
        return result;
    }

    public GdbMiMessage.RecordMessage targetSelectExec ( String path, Action<? super Gdb.GdbTargetSelectBuilder> configure ) throws Exception
    {
        final var builder = gdb.targetSelectExec(path);
        configure.execute(builder);
        final var result = builder.go().get(timeLimit.toMillis(), TimeUnit.MILLISECONDS);
        if (result.content().type().contentEquals("error")) {
            final var message = result.content().properties().get("msg", String.class);
            throw new RuntimeException("gdb: failure: " + message);
        }
        return result;
    }

    public GdbMiMessage.RecordMessage targetSelectTcp ( String host, String port, Action<? super Gdb.GdbTargetSelectBuilder> configure ) throws Exception
    {
        final var builder = gdb.targetSelectTcp(host, port);
        configure.execute(builder);
        final var result = builder.go().get(timeLimit.toMillis(), TimeUnit.MILLISECONDS);
        if (result.content().type().contentEquals("error")) {
            final var message = result.content().properties().get("msg", String.class);
            throw new RuntimeException("gdb: failure: " + message);
        }
        return result;
    }
}
