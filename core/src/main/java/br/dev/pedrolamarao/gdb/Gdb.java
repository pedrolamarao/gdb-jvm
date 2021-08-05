package br.dev.pedrolamarao.gdb;

import br.dev.pedrolamarao.gdb.mi.GdbMiMessage;
import br.dev.pedrolamarao.gdb.mi.GdbMiWriter;
import lombok.var;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * GDB programmatic interface.
 */

public class Gdb implements AutoCloseable
{
    private final AtomicInteger counter = new AtomicInteger();

    private final ConcurrentHashMap<Integer, CompletableFuture<GdbMiMessage.RecordMessage>> contexts = new ConcurrentHashMap<>();

    private final ArrayList<GdbHandler> handlers = new ArrayList<>();

    private final GdbProcess process;

    private final Thread thread;

    // life cicle

    Gdb (GdbProcess process, Collection<GdbHandler> handlers)
    {
        this.handlers.addAll(handlers);
        this.process = process;
        this.thread = new Thread(this::read);

        thread.start();
    }

    /**
     * GDB instance builder.
     *
     * @return new builder
     */

    public static Builder builder ()
    {
        return new Builder();
    }

    /**
     * Close GDB instance.
     */

    public void close ()
    {
        process.destroyForcibly();
        thread.interrupt();
    }

    /**
     * Property: GDB instance exit value.
     *
     * @return value
     */

    public int exitValue ()
    {
        return process.exitValue();
    }

    /**
     * Wait for GDB instance to terminate.
     */

    public boolean waitFor (long time, TimeUnit unit) throws InterruptedException
    {
        return process.waitFor(time, unit);
    }

    // commands

    public abstract class GdbCommandBuilder
    {
        protected abstract GdbMiWriter writer ();

        GdbCommandBuilder () { }

        public Future<GdbMiMessage.RecordMessage> go () throws IOException
        {
            final var context = counter.incrementAndGet();
            final var future = new CompletableFuture<GdbMiMessage.RecordMessage>();
            contexts.put(context, future);
            process.write( writer().context(context) );
            return future;
        }
    }

    public final class GdbBreakInsertBuilder extends GdbCommandBuilder
    {
        private final GdbMiWriter.GdbMiBreakInsertWriter writer;

        GdbBreakInsertBuilder (GdbMiWriter.GdbMiBreakInsertWriter writer) { this.writer = writer; }

        public GdbBreakInsertBuilder pending () { writer.pending(); return this; }

        protected GdbMiWriter.GdbMiBreakInsertWriter writer () { return writer; }
    }

    /**
     * Command GDB to break at symbol.
     *
     * @param symbol  symbol to break upon
     * @return        command builder
     */
    public GdbBreakInsertBuilder breakInsertAtSymbol (String symbol)
    {
        return new GdbBreakInsertBuilder(GdbMiWriter.breakInsert().symbol(symbol));
    }

    public final class GdbBreakWatchBuilder extends GdbCommandBuilder
    {
        private final GdbMiWriter.GdbMiBreakWatchWriter writer;

        GdbBreakWatchBuilder (GdbMiWriter.GdbMiBreakWatchWriter writer) { this.writer = writer; }

        public GdbBreakWatchBuilder read () { writer.read(); return this; }

        protected GdbMiWriter writer () { return writer; }
    }

    /**
     * Command GDB to watch symbol.
     *
     * @param symbol  symbol to watch
     * @return        command builder
     */

    public GdbBreakWatchBuilder breakWatch (String symbol)
    {
        return new GdbBreakWatchBuilder(GdbMiWriter.breakWatch().symbol(symbol));
    }

    public final class GdbExecContinueBuilder extends GdbCommandBuilder
    {
        private final GdbMiWriter.GdbMiExecContinueWriter writer;

        GdbExecContinueBuilder () { writer = GdbMiWriter.execContinue(); }

        protected GdbMiWriter writer () { return writer; }
    }

    /**
     * Command GDB to continue execution.
     *
     * @return  command builder
     */

    public GdbExecContinueBuilder execContinue ()
    {
        return new GdbExecContinueBuilder();
    }

    public final class GdbExecRunBuilder extends GdbCommandBuilder
    {
        private final GdbMiWriter.GdbMiExecRunWriter writer;

        public GdbExecRunBuilder (GdbMiWriter.GdbMiExecRunWriter writer) { this.writer = writer; }

        public GdbExecRunBuilder stopAtMain () { writer.stop(); return this; }

        protected GdbMiWriter writer () { return writer; }
    }

    /**
     * Command GDB to start execution.
     *
     * @return  command builder
     */

    public GdbExecRunBuilder execRun ()
    {
        return new GdbExecRunBuilder( GdbMiWriter.execRun() );
    }

    public final class GdbFileExecAndSymbolsBuilder extends GdbCommandBuilder
    {
        private final GdbMiWriter writer;

        GdbFileExecAndSymbolsBuilder (GdbMiWriter writer) { this.writer = writer; }

        protected GdbMiWriter writer () { return writer; }
    }

    /**
     * Command GDB to load file executable and symbols.
     *
     * @param path  file to load
     * @return      command builder
     */

    public GdbFileExecAndSymbolsBuilder fileExecAndSymbols (String path)
    {
        return new GdbFileExecAndSymbolsBuilder( GdbMiWriter.fileExecAndSymbols().path(path) );
    }

    public final class GdbFileExecFileBuilder extends GdbCommandBuilder
    {
        private final GdbMiWriter writer;

        GdbFileExecFileBuilder (GdbMiWriter writer) { this.writer = writer; }

        protected GdbMiWriter writer () { return writer; }
    }

    /**
     * Command GDB to load executable file.
     */

    public GdbFileExecAndSymbolsBuilder fileExecFile (String path)
    {
        return new GdbFileExecAndSymbolsBuilder( GdbMiWriter.fileExecFile().path(path) );
    }

    public final class GdbExitBuilder extends GdbCommandBuilder
    {
        private final GdbMiWriter writer;

        GdbExitBuilder (GdbMiWriter writer) { this.writer = writer; }

        protected GdbMiWriter writer () { return writer; }
    }

    /**
     * Command GDB to exit.
     *
     * @return  command builder
     */

    public GdbExitBuilder gdbExit ()
    {
        return new GdbExitBuilder( GdbMiWriter.gdbExit() );
    }

    public final class GdbSetBuilder extends GdbCommandBuilder
    {
        private final GdbMiWriter writer;

        GdbSetBuilder (GdbMiWriter writer) { this.writer = writer; }

        protected GdbMiWriter writer () { return writer; }
    }

    /**
     * Command GDB to set variable.
     *
     * @param name   variable name
     * @param value  variable value
     * @return       command builder
     */

    public GdbSetBuilder gdbSet (String name, String value)
    {
        return new GdbSetBuilder( GdbMiWriter.gdbSet().pair(name, value) );
    }

    public final class GdbInterpreterExecBuilder extends GdbCommandBuilder
    {
        private final GdbMiWriter.GdbMiInterpreterExecWriter writer;

        public GdbInterpreterExecBuilder (GdbMiWriter.GdbMiInterpreterExecWriter writer) { this.writer = writer; }

        protected GdbMiWriter writer () { return writer; }
    }

    /**
     * Command GDB to use 'interpreter' to execute 'command'.
     */

    public GdbInterpreterExecBuilder interpreterExec (String interpreter, String... command)
    {
        return new GdbInterpreterExecBuilder( GdbMiWriter.interpreterExec().interpreter(interpreter).command(command) );
    }

    public final class GdbTargetSelectBuilder extends GdbCommandBuilder
    {
        private final GdbMiWriter.GdbMiTargetSelectWriter writer;

        public GdbTargetSelectBuilder (GdbMiWriter.GdbMiTargetSelectWriter writer) { this.writer = writer; }

        protected GdbMiWriter writer () { return writer; }
    }

    /**
     * Command GDB to select executable target.
     *
     * @param path  executable target
     * @return      command builder
     */

    public GdbTargetSelectBuilder targetSelectExec (String path)
    {
        return new GdbTargetSelectBuilder( GdbMiWriter.targetSelect().exec(path) );
    }

    /**
     * Command GDB to select remote TCP target.
     *
     * @param   host  target host
     * @param   port  target port
     * @return        command builder
     */

    public GdbTargetSelectBuilder targetSelectTcp (String host, String port)
    {
        return new GdbTargetSelectBuilder( GdbMiWriter.targetSelect().tcp(host, port) );
    }

    /**
     * Register GDB event handler.
     *
     * @param handler  GDB event handler
     */

    public void handle (GdbHandler handler)
    {
        handlers.add(handler);
    }

    // internal

    void read ()
    {
        try
        {
            while (true)
            {
                final var message = process.read();
                if (message == null) break;

                switch (message.type())
                {
                case Log:
                case Console:
                case Target:
                case Execute:
                case Notify:
                case Status:
                case Prompt:
                    handlers.forEach(handler -> handler.handle(this, message));
                    break;
                case Result:
                    final var record = (GdbMiMessage.RecordMessage) message;
                    final var context = message.context();
                    if (context == null) break;
                    final var future = contexts.get(context);
                    future.complete(record);
                    contexts.remove(context);
                    break;
                }
            }
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
    }

    /**
     * GDB instance builder.
     */

    public static final class Builder
    {
        private final ArrayList<GdbHandler> handlers = new ArrayList<>();

        private final GdbProcess.Builder process = GdbProcess.builder();

        Builder () { }

        /**
         * Property: GDB command path.
         *
         * @param value  path
         * @return       this builder
         */

        public Builder command (String value)
        {
            process.command(value);
            return this;
        }

        /**
         * Property: GDB async message handler.
         *
         * @param handler  handler
         * @return         this builder
         */

        public Builder handler (GdbHandler handler)
        {
            handlers.add(handler);
            return this;
        }

        /**
         * Start new GDB instance.
         *
         * @return new instance
         * @throws IOException if file not found etc.
         */

        public Gdb start () throws IOException
        {
            return new Gdb(process.start(), handlers);
        }
    }
}
