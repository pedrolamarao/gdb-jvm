package br.dev.pedrolamarao.gdb;

import br.dev.pedrolamarao.gdb.mi.GdbMiMessage;
import br.dev.pedrolamarao.gdb.mi.GdbMiWriter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Future;
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

    // operations

    /**
     * Call GDB command.
     *
     * @param command       command message
     * @return              command response
     * @throws IOException  if communication failure
     */

    public Future<GdbMiMessage.RecordMessage> call (GdbMiWriter command) throws IOException
    {
        final var context = counter.incrementAndGet();
        final var future = new CompletableFuture<GdbMiMessage.RecordMessage>();
        contexts.put(context, future);
        process.write( command.context(context) );
        return future;
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
