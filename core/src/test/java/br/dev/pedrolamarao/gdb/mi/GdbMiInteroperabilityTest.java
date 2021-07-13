package br.dev.pedrolamarao.gdb.mi;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;

import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.concurrent.TimeUnit;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.assertTimeout;
import static org.junit.jupiter.api.Assertions.assertTrue;

@EnabledIfSystemProperty(named = "br.dev.pedrolamarao.gdb.test.command", matches = ".+")
public class GdbMiInteroperabilityTest
{
    static final String path = System.getProperty("br.dev.pedrolamarao.gdb.test.command");

    @Test
    @DisplayName("start, quit")
    public void startQuit () throws Exception
    {
        final var process = new ProcessBuilder()
            .command(path, "--interpreter=mi")
            .start();

        try
        {
            final var writer = new OutputStreamWriter(process.getOutputStream(), UTF_8);
            GdbMiWriter.gdbExit().context(123).write(writer).flush();

            final var reader = GdbMiReader.fromStream(process.getInputStream(), StandardCharsets.UTF_8);

            assertTimeout(
                Duration.ofSeconds(10),
                () ->
                {
                    while (true) {
                        final var message = reader.read();
                        if (message == null)
                            break;
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
}
