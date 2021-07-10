package br.dev.pedrolamarao.gdb.mi;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;


public class GdbMiReader
{
    private final Reader reader;

    GdbMiReader (Reader reader)
    {
        this.reader = reader;
    }

    public static GdbMiReader fromReader (Reader reader)
    {
        return new GdbMiReader(reader);
    }

    public static GdbMiReader fromStream (InputStream stream, Charset charset)
    {
        return new GdbMiReader( new InputStreamReader(stream, charset) );
    }

    public GdbMiMessage read () throws IOException
    {
        return readMessage(reader);
    }

    static final class Read<T>
    {
        final int token;

        final T value;

        public Read (int token, T foo)
        {
            this.token = token;
            this.value = foo;
        }
    }

    public static GdbMiMessage readMessage (Reader reader) throws IOException
    {
        final var token = reader.read();
        if (token == -1) return null;

        switch (token)
        {
            case '~':
                return readFinishStringMessage(GdbMiType.Console, reader);
            case '@':
                return readFinishStringMessage(GdbMiType.Target, reader);
            case '&':
                return readFinishStringMessage(GdbMiType.Log, reader);
            case '*':
                return readFinishRecordMessage(GdbMiType.Execute, reader);
            case '=':
                return readFinishRecordMessage(GdbMiType.Notify, reader);
            case '+':
                return readFinishRecordMessage(GdbMiType.Status, reader);
            case '^':
                return readFinishRecordMessage(GdbMiType.Result, reader);
            case '(':
                return readFinishPrompt(token, reader);
            default:
                throw new RuntimeException("unexpected token");
        }
    }

    static GdbMiMessage.StringMessage readFinishStringMessage (GdbMiType type, Reader reader) throws IOException
    {
        var token = reader.read();
        if (token == -1) return null;
        if (token != '"') throw new RuntimeException("unexpected token");

        final var string = readString(reader);

        while (token != -1 && token != '\n') {
            token = reader.read();
        }

        return GdbMiMessage.string(type, string.toString());
    }

    static GdbMiMessage.RecordMessage readFinishRecordMessage (GdbMiType type, Reader reader) throws IOException
    {
        int token = -1;

        final var class_ = readSimpleString(reader);
        token = class_.token;
        if (token == -1) return null;

        final Read<Map<String, String>> properties =
            (token == ',') ? readProperties(reader) : new Read<>(0, Collections.emptyMap());
        token = properties.token;

        while (token != -1 && token != '\n') {
            token = reader.read();
        }

        return GdbMiMessage.record(type, class_.value, properties.value);
    }

    public static GdbMiMessage.StringMessage readFinishPrompt (int token, Reader reader) throws IOException
    {
        final var prompt = "(gdb)".toCharArray();

        for (int i = 0, j = prompt.length; i != j; ++i) {
            if (token == -1) throw new RuntimeException("unexpected stream: end-of-stream inside prompt");
            if (token != prompt[i]) raiseUnexpected(token, prompt[i]);
            token = reader.read();
        }

        while (token != -1 && token != '\n') {
            token = reader.read();
        }

        return GdbMiMessage.string(GdbMiType.Prompt, "");
    }

    public static Read<Map<String, String>> readProperties (Reader reader) throws IOException
    {
        final var properties = new HashMap<String, String>();

        int token = -1;

        do
        {
            final var name = readSimpleString(reader);
            token = name.token;
            if (token == -1) throw new RuntimeException("unexpected stream: end-of-stream in property-name");

            if (token != '=') raiseUnexpected(token, '=');

            final var value = readString(reader);
            token = value.token;

            properties.put(name.value, value.value);
        }
        while (token == ',');

        return new Read<>(token, properties);
    }

    public static Read<String> readString (Reader reader) throws IOException
    {
        int token = reader.read();
        if (token == -1) return new Read<>(token, "");

        if (token == '"')
            return readFinishQuotedString(token, reader);
        else
            return readFinishSimpleString(token, reader);
    }

    public static Read<String> readSimpleString (Reader reader) throws IOException
    {
        return readFinishSimpleString(reader.read(), reader);
    }

    public static Read<String> readFinishSimpleString (int token, Reader reader) throws IOException
    {
        final var builder = new StringBuilder();

        while (Character.isAlphabetic(token))
        {
            builder.append((char) token);
            token = reader.read();
        }

        return new Read<>(token, builder.toString());
    }

    public static Read<String> readFinishQuotedString (int token, Reader reader) throws IOException
    {
        final var builder = new StringBuilder();

        token = reader.read();
        if (token == -1) throw new RuntimeException("unexpected stream: end-of-string inside quoted-string");

        while (token != '"')
        {
            builder.append((char) token);
            token = reader.read();
            if (token == -1) return new Read<>(token, null);
            if (token == '\n') throw new RuntimeException("unexpected token: new-line inside quoted-string");
        }

        token = reader.read();

        return new Read<>(token, builder.toString());
    }

    public static void raiseUnexpected (int actual, int expected)
    {
        throw new RuntimeException(
            "unexpected token: expected '" + (char) expected + "'"
                + " but got '" + (char) actual + "'"
        );
    }
}
