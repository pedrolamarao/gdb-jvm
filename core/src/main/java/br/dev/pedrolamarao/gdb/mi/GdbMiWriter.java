package br.dev.pedrolamarao.gdb.mi;

import java.io.IOException;
import java.io.Writer;

/**
 * GDB/MI message writer.
 */

public abstract class GdbMiWriter
{
    GdbMiWriter () { super(); }

    public abstract GdbMiWriter context (int value);

    public abstract Writer write (Writer writer) throws IOException;

    /**
     * GDB/MI {@code quit} message writer.
     */

    public static final class GdbMiQuitWriter extends GdbMiWriter
    {
        Integer context = null;

        @Override
        public GdbMiQuitWriter context (int value)
        {
            this.context = value;
            return this;
        }

        @Override
        public Writer write (Writer writer) throws IOException
        {
            if (context != null) writer.write( Integer.toString(context) );
            writer.write("-gdb-exit\n");
            return writer;
        }
    }

    /**
     * GDB/MI {@code quit} message writer.
     *
     * @return new message writer
     */

    public static GdbMiQuitWriter quit ()
    {
        return new GdbMiQuitWriter();
    }
}
