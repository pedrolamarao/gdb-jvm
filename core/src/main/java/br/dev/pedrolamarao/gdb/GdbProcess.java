package br.dev.pedrolamarao.gdb;

import br.dev.pedrolamarao.gdb.mi.GdbMiMessage;
import br.dev.pedrolamarao.gdb.mi.GdbMiReader;
import br.dev.pedrolamarao.gdb.mi.GdbMiWriter;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

public class GdbProcess
{
    final Process process;

    final GdbMiReader reader;

    final Writer writer;

    GdbProcess (Process process)
    {
        this.process = process;
        this.reader = GdbMiReader.fromStream(process.getInputStream(), StandardCharsets.UTF_8);
        this.writer = new OutputStreamWriter(process.getOutputStream(), StandardCharsets.UTF_8);
    }

    public static GdbProcessBuilder builder ()
    {
        return new GdbProcessBuilder();
    }

    public void destroy ()
    {
        process.destroy();
    }

    public GdbProcess destroyForcibly ()
    {
        process.destroyForcibly();
        return this;
    }

    public int exitValue ()
    {
        return process.exitValue();
    }

    public GdbMiMessage read () throws IOException
    {
        return reader.read();
    }

    public boolean waitFor (long time, TimeUnit unit) throws InterruptedException
    {
        return process.waitFor(time, unit);
    }

    public GdbProcess write ( GdbMiWriter message ) throws IOException
    {
        message.write(this.writer).flush();
        return this;
    }
}
