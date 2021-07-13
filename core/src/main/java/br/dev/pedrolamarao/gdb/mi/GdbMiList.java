package br.dev.pedrolamarao.gdb.mi;

import java.util.List;

public class GdbMiList
{
    private final List<Object> list;

    public GdbMiList (List<Object> list)
    {
        this.list = list;
    }

    public <T> T get (int index, Class<T> type)
    {
        return type.cast(list.get(index));
    }
}
