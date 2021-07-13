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
}
