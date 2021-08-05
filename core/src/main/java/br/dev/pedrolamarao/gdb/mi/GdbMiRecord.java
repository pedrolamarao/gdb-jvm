package br.dev.pedrolamarao.gdb.mi;

/**
 * GDB/MI record.
 */

public final class GdbMiRecord
{
    private final String type;

    private final GdbMiProperties properties;

    public GdbMiRecord (String type, GdbMiProperties properties)
    {
        this.type = type;
        this.properties = properties;
    }

    public String type ()
    {
        return type;
    }

    public GdbMiProperties properties () { return properties; }

    private static final String template = "%s:%s";

    @Override
    public String toString () { return String.format(template, type, properties); }
}
