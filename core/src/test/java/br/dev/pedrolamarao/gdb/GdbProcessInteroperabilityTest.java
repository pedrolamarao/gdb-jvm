package br.dev.pedrolamarao.gdb;

import br.dev.pedrolamarao.gdb.mi.GdbMiWriter;
import lombok.var;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertTimeout;
import static org.junit.jupiter.api.Assertions.assertTrue;

@EnabledIfSystemProperty(named = "br.dev.pedrolamarao.gdb.test.command", matches = ".+")
public class GdbProcessInteroperabilityTest
{
    static final String path = System.getProperty("br.dev.pedrolamarao.gdb.test.command");

    @Test
    @DisplayName("command: start, quit")
    public void commandStartQuiet () throws Exception
    {
        final var process = GdbProcess.builder()
            .command(path)
            .start();

        try
        {
            process.write( GdbMiWriter.gdbExit().context(123) );

            assertTimeout(
                Duration.ofSeconds(10),
                () ->
                {
                    while (true) {
                        final var message = process.read();
                        if (message == null)
                            break;
                        System.err.println(message);
                    }
                }
            );

            assertTrue( process.waitFor(1000, TimeUnit.MILLISECONDS) );

            assertThat( process.exitValue(), equalTo(0) );
        }
        finally
        {
            process.destroyForcibly();
        }
    }

    @Test
    public void destroy () throws Exception
    {
        final var process = GdbProcess.builder()
            .command(path)
            .start();

        process.destroy();

        assertTimeout(
            Duration.ofSeconds(1),
            () ->
            {
                while (true) {
                    final var message = process.read();
                    if (message == null) break;
                }
            }
        );
    }

    @Test
    public void destroyForcibly () throws Exception
    {
        final var process = GdbProcess.builder()
            .command(path)
            .start();

        process.destroyForcibly();

        assertTimeout(
            Duration.ofSeconds(1),
            () ->
            {
                while (true) {
                    final var message = process.read();
                    if (message == null) break;
                }
            }
        );
    }
}
