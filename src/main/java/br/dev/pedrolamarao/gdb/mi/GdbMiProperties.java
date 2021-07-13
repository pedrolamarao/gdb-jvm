package br.dev.pedrolamarao.gdb.mi;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * GDB/MI properties.
 */

public class GdbMiProperties
{
    private final Map<String, Object> properties;

    public GdbMiProperties ()
    {
        this.properties = Collections.emptyMap();
    }

    public GdbMiProperties (HashMap<String, Object> properties)
    {
        this.properties = properties;
    }

    public <T> T get (String name, Class<T> type)
    {
        return type.cast(properties.get(name));
    }
}
