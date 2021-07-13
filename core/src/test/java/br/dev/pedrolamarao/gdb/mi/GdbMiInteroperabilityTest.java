package br.dev.pedrolamarao.gdb.mi;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

import static java.nio.charset.StandardCharsets.UTF_8;
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
        final var builder = new ProcessBuilder();
        builder.command(path, "--interpreter=mi");

        final var process = builder.start();

        try
        {
            GdbMiWriter.quit().write(process.getOutputStream(), UTF_8);

            final var reader = GdbMiReader.fromStream(process.getInputStream(), UTF_8);

            assertTimeout(
                Duration.ofSeconds(10),
                () ->
                {
                    while (true) {
                        final var message = reader.read();
                        if (message == null)
                            break;
                        System.err.println(message);
                    }
                }
            );

            assertTrue( process.waitFor(1000, TimeUnit.MILLISECONDS) );
        }
        finally
        {
            process.destroyForcibly();
        }
    }
}
