package br.dev.pedrolamarao.gdb;

import java.io.IOException;
import java.util.ArrayList;

public class GdbProcessBuilder
{
    private final ArrayList<String> command = new ArrayList();

    GdbProcessBuilder ()
    {
        command.add("gdb");
    }

    public GdbProcessBuilder command (String path)
    {
        command.set(0, path);
        return this;
    }

    public GdbProcessBuilder interpreter (String type)
    {
        command.add("--interpreter=" + type);
        return this;
    }

    public GdbProcess start () throws IOException
    {
        final var builder = new ProcessBuilder();
        builder.command(command);
        final var process = builder.start();
        return new GdbProcess(process);
    }
}
