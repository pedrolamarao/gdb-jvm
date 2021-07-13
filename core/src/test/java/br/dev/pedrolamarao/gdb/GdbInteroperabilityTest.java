package br.dev.pedrolamarao.gdb;

import br.dev.pedrolamarao.gdb.mi.GdbMiMessage;
import br.dev.pedrolamarao.gdb.mi.GdbMiType;
import br.dev.pedrolamarao.gdb.mi.GdbMiWriter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

@EnabledIfSystemProperty(named = "br.dev.pedrolamarao.gdb.test.command", matches = ".+")
public class GdbInteroperabilityTest
{
    static final String path = System.getProperty("br.dev.pedrolamarao.gdb.test.command", null);

    static final String target = System.getProperty("br.dev.pedrolamarao.gdb.test.target", null);

    @Test
    public void breakInsert () throws Exception
    {
        try (var gdb = Gdb.builder().command(path).start())
        {
            final var message = GdbMiWriter.breakInsert().symbol("main").pending();
            final var response = gdb.call(message).get(1000, TimeUnit.MILLISECONDS);
            assertThat(response.content().type(), equalTo("done"));
        }
    }

    @Test
    public void breakWatch () throws Exception
    {
        assumeTrue(Files.exists(Path.of(target)));

        try (var gdb = Gdb.builder().command(path).start())
        {
            final var message0 = GdbMiWriter.fileExecAndSymbols().path(target);
            final var response0 = gdb.call(message0).get(1000, TimeUnit.MILLISECONDS);
            assertThat(response0.content().type(), equalTo("done"));

            final var message1 = GdbMiWriter.execRun().stop();
            final var response1 = gdb.call(message1).get(1000, TimeUnit.MILLISECONDS);
            assertThat(response1.content().type(), equalTo("running"));

            final var handler = new GdbHandler() {
                final CompletableFuture<GdbMiMessage> future = new CompletableFuture<>();
                @Override public void handle (Gdb gdb, GdbMiMessage event)
                {
                    if (event.type() != GdbMiType.Execute) return;
                    final var record = (GdbMiMessage.RecordMessage) event;
                    if (! record.content().type().contentEquals("stopped")) return;
                    future.complete(event);
                }
            };

            gdb.handle(handler);

            final var message2 = GdbMiWriter.breakWatch().read().symbol("argc");
            final var response2 = gdb.call(message2).get(1000, TimeUnit.MILLISECONDS);
            assertThat(response2.content().type(), equalTo("done"));

            final var message3 = GdbMiWriter.execContinue();
            final var response3 = gdb.call(message3).get(1000, TimeUnit.MILLISECONDS);
            assertThat(response3.content().type(), equalTo("running"));

            handler.future.get(1000, TimeUnit.MILLISECONDS);
        }
    }

    @Test
    public void execContinue () throws Exception
    {
        assumeTrue(Files.exists(Path.of(target)));

        try (var gdb = Gdb.builder().command(path).start())
        {
            final var message0 = GdbMiWriter.fileExecAndSymbols().path(target);
            final var response0 = gdb.call(message0).get(1000, TimeUnit.MILLISECONDS);
            assertThat(response0.content().type(), equalTo("done"));

            final var message1 = GdbMiWriter.execRun().stop();
            final var response1 = gdb.call(message1).get(1000, TimeUnit.MILLISECONDS);
            assertThat(response1.content().type(), equalTo("running"));

            final var message2 = GdbMiWriter.execContinue();
            final var response2 = gdb.call(message2).get(1000, TimeUnit.MILLISECONDS);
            assertThat(response2.content().type(), equalTo("running"));
        }
    }

    @Test
    public void execRun () throws Exception
    {
        assumeTrue(Files.exists(Path.of(target)));

        try (var gdb = Gdb.builder().command(path).start())
        {
            final var message0 = GdbMiWriter.targetSelect().exec(target);
            final var response0 = gdb.call(message0).get(1000, TimeUnit.MILLISECONDS);
            assertThat(response0.content().type(), equalTo("connected"));

            final var message1 = GdbMiWriter.execRun();
            final var response1 = gdb.call(message1).get(1000, TimeUnit.MILLISECONDS);
            assertThat(response1.content().type(), equalTo("running"));
        }
    }

    @Test
    public void fileExecAndSymbols () throws Exception
    {
        try (var gdb = Gdb.builder().command(path).start())
        {
            final var message = GdbMiWriter.fileExecAndSymbols().path(target);
            final var response = gdb.call(message).get(1000, TimeUnit.MILLISECONDS);
            assertThat(response.content().type(), equalTo("done"));
        }
    }

    @Test
    public void gdbExit () throws Exception
    {
        try (var gdb = Gdb.builder().command(path).start())
        {
            final var message = GdbMiWriter.gdbExit();
            final var response = gdb.call(message).get(1000, TimeUnit.MILLISECONDS);
            assertThat(response.content().type(), equalTo("exit"));
        }
    }

    @Test
    public void gdbSet () throws Exception
    {
        try (var gdb = Gdb.builder().command(path).start())
        {
            final var message = GdbMiWriter.gdbSet().pair("foo", "16");
            final var response = gdb.call(message).get(1000, TimeUnit.MILLISECONDS);
            assertThat(response.content().type(), equalTo("done"));
        }
    }

    @Test
    public void smoke () throws Exception
    {
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

        try (var ignored = Gdb.builder().command(path).handler(handler0).start())
        {
            handler0.future.get(1000, TimeUnit.MILLISECONDS);
        }
    }

    @Test
    public void targetSelect () throws Exception
    {
        assumeTrue(Files.exists(Path.of(target)));

        try (var gdb = Gdb.builder().command(path).start())
        {
            final var message = GdbMiWriter.targetSelect().exec(target);
            final var response = gdb.call(message).get(1000, TimeUnit.MILLISECONDS);
            assertThat(response.content().type(), equalTo("connected"));
        }
    }
}
