package br.dev.pedrolamarao.gdb.mi;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.nio.charset.Charset;

public abstract class GdbMiWriter
{
    GdbMiWriter () { super(); }

    public abstract Writer write (Writer writer) throws IOException;

    public OutputStream write (OutputStream stream, Charset charset) throws IOException
    {
        final var writer = new OutputStreamWriter(stream, charset);
        write(writer);
        writer.flush();
        return stream;
    }

    public static final class GdbMiQuitWriter extends GdbMiWriter
    {

        @Override
        public Writer write (Writer writer) throws IOException
        {
            writer.write("quit\n");
            return writer;
        }
    }

    public static GdbMiQuitWriter quit ()
    {
        return new GdbMiQuitWriter();
    }
}
