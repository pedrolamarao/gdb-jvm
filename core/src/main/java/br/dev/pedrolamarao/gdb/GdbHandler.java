package br.dev.pedrolamarao.gdb;

import br.dev.pedrolamarao.gdb.mi.GdbMiMessage;

public interface GdbHandler
{
    void handle (Gdb gdb, GdbMiMessage message);
}
