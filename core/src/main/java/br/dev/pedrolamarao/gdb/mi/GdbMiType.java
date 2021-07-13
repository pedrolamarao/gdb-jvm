package br.dev.pedrolamarao.gdb.mi;

/**
 * GDB/MI message type.
 */

public enum GdbMiType
{
    Result,
    Log,
    Console,
    Target,
    Execute,
    Notify,
    Status,
    Prompt;
}
