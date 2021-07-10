package br.dev.pedrolamarao.gdb.mi;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public final class GdbMiRecord
{
    private final String type;

    private final HashMap<String, String> properties;

    public GdbMiRecord (String type, HashMap<String, String> properties)
    {
        this.type = type;
        this.properties = properties;
    }

    public String type ()
    {
        return type;
    }

    public Map<String, String> properties ()
    {
        return Collections.unmodifiableMap(properties);
    }
}
