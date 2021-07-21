package br.dev.pedrolamarao.gdb.mi;

import lombok.var;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Objects;

/**
 * GDB/MI message writer.
 */

public abstract class GdbMiWriter
{
    GdbMiWriter () { super(); }

    /**
     * Property: message context.
     *
     * @param value  value
     * @return       this writer
     */

    public abstract GdbMiWriter context (int value);

    /**
     * Write message.
     *
     * @param writer       text writer
     * @return             text writer
     * @throws IOException if communication failure
     */
    public abstract Writer write (Writer writer) throws IOException;

    public static final class GdbMiSimpleWriter extends GdbMiWriter
    {
        private String context = "";

        private final String message;

        GdbMiSimpleWriter (String message) { this.message = message; }

        @Override
        public GdbMiSimpleWriter context (int value)
        {
            context = Integer.toString(value, 10);
            return this;
        }

        @Override
        public Writer write (Writer writer) throws IOException
        {
            writer.write(String.format("%s-%s\n", context, message));
            return writer;
        }
    }

    /**
     * GDB/MI {@code break-insert} message writer.
     */

    public static final class GdbMiBreakInsertWriter extends GdbMiWriter
    {
        private String context = "";

        private final ArrayList<String> options = new ArrayList<>();

        private String location = null;

        @Override
        public GdbMiBreakInsertWriter context (int value)
        {
            context = Integer.toString(value, 10);
            return this;
        }

        public GdbMiBreakInsertWriter hardware ()
        {
            options.add("-h");
            return this;
        }

        public GdbMiBreakInsertWriter pending ()
        {
            options.add("-f");
            return this;
        }

        public GdbMiBreakInsertWriter symbol (String value)
        {
            location = value;
            return this;
        }

        @Override
        public Writer write (Writer writer) throws IOException
        {
            Objects.requireNonNull(writer);
            final var opt = String.join(" ", options);
            final var message = String.format("%s-break-insert %s %s\n", context, opt, location);
            writer.write(message);
            return writer;
        }
    }

    /**
     * GDB/MI {@code break-watch} message writer.
     *
     * @return new message writer
     */

    public static GdbMiBreakInsertWriter breakInsert ()
    {
        return new GdbMiBreakInsertWriter();
    }

    /**
     * GDB/MI {@code break-watch} message writer.
     */

    public static final class GdbMiBreakWatchWriter extends GdbMiWriter
    {
        private String context = "";

        private String option = "";

        private String symbol = null;

        @Override
        public GdbMiBreakWatchWriter context (int value)
        {
            context = Integer.toString(value, 10);
            return this;
        }
        public GdbMiBreakWatchWriter read ()
        {
            option = "-r";
            return this;
        }

        public GdbMiBreakWatchWriter symbol (String value)
        {
            symbol = value;
            return this;
        }

        @Override
        public Writer write (Writer writer) throws IOException
        {
            Objects.requireNonNull(writer);
            final var message = String.format("%s-break-watch %s %s\n", context, option, symbol);
            writer.write(message);
            return writer;
        }
    }

    /**
     * GDB/MI {@code break-watch} message writer.
     *
     * @return new message writer
     */

    public static GdbMiBreakWatchWriter breakWatch ()
    {
        return new GdbMiBreakWatchWriter();
    }

    /**
     * GDB/MI {@code file-exec-and-symbols} message writer.
     */

    public static final class GdbMiFileExecAndSymbolsWriter extends GdbMiWriter
    {
        private String context = "";

        private String path = null;

        @Override
        public GdbMiFileExecAndSymbolsWriter context (int value)
        {
            context = Integer.toString(value, 10);
            return this;
        }

        public GdbMiFileExecAndSymbolsWriter path (String value)
        {
            Objects.requireNonNull(value);
            path = value;
            return this;
        }

        @Override
        public Writer write (Writer writer) throws IOException
        {
            Objects.requireNonNull(writer);
            final var message = String.format("%s-file-exec-and-symbols %s\n", context, path);
            writer.write(message);
            return writer;
        }
    }

    /**
     * GDB/MI {@code file-exec-and-symbols} message writer.
     *
     * @return new message writer
     */

    public static GdbMiFileExecAndSymbolsWriter fileExecAndSymbols ()
    {
        return new GdbMiFileExecAndSymbolsWriter();
    }

    /**
     * GDB/MI {@code gdb-exit} message writer.
     *
     * @return new message writer
     */

    public static GdbMiSimpleWriter gdbExit ()
    {
        return new GdbMiSimpleWriter("gdb-exit");
    }

    /**
     * GDB/MI {@code exec-continue} message writer.
     */

    public static final class GdbMiExecContinueWriter extends GdbMiWriter
    {
        private String context = "";

        private final ArrayList<String> options = new ArrayList<>();

        @Override
        public GdbMiExecContinueWriter context (int value)
        {
            context = Integer.toString(value, 10);
            return this;
        }

        public GdbMiExecContinueWriter reverse ()
        {
            options.add("--reverse");
            return this;
        }

        @Override
        public Writer write (Writer writer) throws IOException
        {
            Objects.requireNonNull(writer);
            final var message = String.format("%s-exec-continue %s\n", context, String.join(" ", options));
            writer.write(message);
            return writer;
        }
    }

    /**
     * GDB/MI {@code exec-continue} message writer.
     *
     * @return new message writer
     */

    public static GdbMiExecContinueWriter execContinue ()
    {
        return new GdbMiExecContinueWriter();
    }

    /**
     * GDB/MI {@code exec-run} message writer.
     */

    public static final class GdbMiExecRunWriter extends GdbMiWriter
    {
        private String context = "";

        private final ArrayList<String> options = new ArrayList<>();

        @Override
        public GdbMiExecRunWriter context (int value)
        {
            context = Integer.toString(value, 10);
            return this;
        }

        public GdbMiExecRunWriter stop ()
        {
            options.add("--start");
            return this;
        }

        @Override
        public Writer write (Writer writer) throws IOException
        {
            Objects.requireNonNull(writer);
            final var message = String.format("%s-exec-run %s\n", context, String.join(" ", options));
            writer.write(message);
            return writer;
        }
    }

    /**
     * GDB/MI {@code exec-run} message writer.
     *
     * @return new message writer
     */

    public static GdbMiExecRunWriter execRun ()
    {
        return new GdbMiExecRunWriter();
    }

    /**
     * GDB/MI {@code file-exec-file} message writer.
     */

    public static final class GdbMiFileExecFileWriter extends GdbMiWriter
    {
        private String context = "";

        private String path = "";

        GdbMiFileExecFileWriter () {}

        @Override
        public GdbMiFileExecFileWriter context (int value)
        {
            this.context = Integer.toString(value, 10);
            return this;
        }

        public GdbMiFileExecFileWriter path (String value)
        {
            this.path = value;
            return this;
        }

        @Override
        public Writer write (Writer writer) throws IOException
        {
            Objects.requireNonNull(writer);
            final var message = String.format("%s-file-exec-file %s\n", context, path);
            writer.write(message);
            return writer;
        }
    }

    public static GdbMiFileExecFileWriter fileExecFile () { return new GdbMiFileExecFileWriter(); }

    /**
     * GDB/MI {@code gdb-set} message writer.
     */

    public static final class GdbMiGdbSetWriter extends GdbMiWriter
    {
        private String context = "";

        private String args = null;

        @Override
        public GdbMiGdbSetWriter context (int value)
        {
            this.context = Integer.toString(value, 10);
            return this;
        }

        public GdbMiGdbSetWriter pair (String name, String value)
        {
            Objects.requireNonNull(name);
            Objects.requireNonNull(value);
            args = name + ' ' + value;
            return this;
        }

        @Override
        public Writer write (Writer writer) throws IOException
        {
            Objects.requireNonNull(writer);
            final var message = String.format("%s-gdb-set %s\n", context, args);
            writer.write(message);
            return writer;
        }
    }

    /**
     * GDB/MI {@code gdb-set} message writer.
     *
     * @return new message writer
     */

    public static GdbMiGdbSetWriter gdbSet ()
    {
        return new GdbMiGdbSetWriter();
    }

    public static final class GdbMiInterpreterExecWriter extends GdbMiWriter
    {
        private String[] command = { };

        private String context = "";

        private String interpreter = "console";

        GdbMiInterpreterExecWriter () { }

        public GdbMiInterpreterExecWriter command (String... value)
        {
            this.command = value;
            return this;
        }

        @Override
        public GdbMiInterpreterExecWriter context (int value)
        {
            this.context = Integer.toString(value, 10);
            return this;
        }

        public GdbMiInterpreterExecWriter interpreter (String value)
        {
            this.interpreter = value;
            return this;
        }

        @Override
        public Writer write (Writer writer) throws IOException
        {
            Objects.requireNonNull(writer);
            final var message = String.format("%s-interpreter-exec %s \"%s\"\n", context, interpreter, String.join(" ", command));
            writer.write(message);
            return writer;
        }
    }

    /**
     * GDB/MI {@code interpreter-exec} message writer.
     *
     * @param   interpreter  interpreter name
     * @param   command      interpreter command
     * @return               new message writer
     */

    public static GdbMiInterpreterExecWriter interpreterExec ()
    {
        return new GdbMiInterpreterExecWriter();
    }

    /**
     * GDB/MI {@code interpreter-exec} message writer.
     *
     * @param   interpreter  interpreter name
     * @param   command      interpreter command
     * @return               new message writer
     */

    public static GdbMiInterpreterExecWriter interpreterExec (String interpreter, String... command)
    {
        return new GdbMiInterpreterExecWriter().interpreter(interpreter).command(command);
    }

    /**
     * GDB/MI {@code target-select} message writer.
     */

    public static final class GdbMiTargetSelectWriter extends GdbMiWriter
    {
        private String context = "";

        private String args = "";

        @Override
        public GdbMiTargetSelectWriter context (int value)
        {
            this.context = Integer.toString(value, 10);
            return this;
        }

        public GdbMiTargetSelectWriter exec (String value)
        {
            Objects.requireNonNull(value);
            args = "exec " + value;
            return this;
        }

        public GdbMiTargetSelectWriter tcp (String host, String port)
        {
            Objects.requireNonNull(host);
            Objects.requireNonNull(port);
            args = "remote tcp:" + host + ':' + port;
            return this;
        }

        @Override
        public Writer write (Writer writer) throws IOException
        {
            Objects.requireNonNull(writer);
            final var message = String.format("%s-target-select %s\n", context, args);
            writer.write(message);
            return writer;
        }
    }

    /**
     * GDB/MI {@code target-select} message writer.
     *
     * @return new message writer
     */

    public static GdbMiTargetSelectWriter targetSelect ()
    {
        return new GdbMiTargetSelectWriter();
    }
}
