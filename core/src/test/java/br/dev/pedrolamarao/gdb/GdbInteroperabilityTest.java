package br.dev.pedrolamarao.gdb;

import br.dev.pedrolamarao.gdb.mi.GdbMiMessage;
import br.dev.pedrolamarao.gdb.mi.GdbMiType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@EnabledIfSystemProperty(named = "br.dev.pedrolamarao.gdb.test.command", matches = ".+")
public class GdbInteroperabilityTest
{
    static final String path = System.getProperty("br.dev.pedrolamarao.gdb.test.command", null);

    static final String target = System.getProperty("br.dev.pedrolamarao.gdb.test.target", null);

    @Test
    public void smoke () throws Exception
    {
        final var builder = Gdb.builder().command(path);

        final var handler0 = new GdbHandler()
        {
            public final CompletableFuture<GdbMiMessage> future = new CompletableFuture<>();

            @Override public void handle (Gdb gdb, GdbMiMessage message)
            {
                System.err.println(message);
                if (message.type() == GdbMiType.Prompt)
                    future.complete(message);
            }
        };

        try (var gdb = builder.handler(handler0).start())
        {
            handler0.future.get(1000, TimeUnit.MILLISECONDS);
        }
    }

    @Test
    public void gdbExit () throws Exception
    {
        final var builder = Gdb.builder().command(path);

        try (var gdb = builder.start())
        {
            gdb.xxxGdbExit().get(1000, TimeUnit.MILLISECONDS);
        }
    }
}
