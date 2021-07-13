package br.dev.pedrolamarao.gdb;

import br.dev.pedrolamarao.gdb.mi.GdbMiMessage;
import br.dev.pedrolamarao.gdb.mi.GdbMiWriter;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

public class Gdb implements AutoCloseable
{
    private final AtomicInteger counter = new AtomicInteger();

    private final HashMap<Integer, CompletableFuture<GdbMiMessage>> contexts = new HashMap<>();

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

    public static Builder builder ()
    {
        return new Builder();
    }

    public void close ()
    {
        process.destroyForcibly();
        thread.interrupt();
    }

    // operations

    public Future<GdbMiMessage> xxxGdbExit () throws IOException
    {
        final var context = counter.incrementAndGet();
        final var future = new CompletableFuture<GdbMiMessage>();
        contexts.put(context, future);
        process.write( GdbMiWriter.quit().context(context) );
        return future;
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
                    final var context = message.context();
                    final var future = contexts.get(context);
                    future.complete(message);
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

    public static final class Builder
    {
        private final ArrayList<GdbHandler> handlers = new ArrayList<>();

        private final GdbProcess.Builder process = GdbProcess.builder();

        Builder () { }

        public Builder command (String value)
        {
            process.command(value);
            return this;
        }

        public Builder handler (GdbHandler handler)
        {
            handlers.add(handler);
            return this;
        }

        public Gdb start () throws IOException
        {
            return new Gdb(process.start(), handlers);
        }
    }
}
