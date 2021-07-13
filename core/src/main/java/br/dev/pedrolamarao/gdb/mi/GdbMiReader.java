package br.dev.pedrolamarao.gdb.mi;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.util.HashMap;


/**
 * GDB/MI message reader.
 */

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

        @Override public String toString () { return String.format("token = %s, value = %s", token, value); }
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
                throw new RuntimeException("unexpected token in message-type: " + (char) token);
        }
    }

    static GdbMiMessage.StringMessage readFinishStringMessage (GdbMiType type, Reader reader) throws IOException
    {
        final var string = readString(reader);
        int token = string.token;

        while (token != -1 && token != '\n') {
            token = reader.read();
        }

        return GdbMiMessage.string(type, string.value);
    }

    static GdbMiMessage.RecordMessage readFinishRecordMessage (GdbMiType type, Reader reader) throws IOException
    {
        final var record = readRecord(reader);
        int token = record.token;

        while (token != -1 && token != '\n') {
            token = reader.read();
        }

        return GdbMiMessage.record(type, record.value);
    }

    public static GdbMiMessage.StringMessage readFinishPrompt (int token, Reader reader) throws IOException
    {
        final var prompt = "(gdb)".toCharArray();

        for (int i = 0, j = prompt.length; i != j; ++i) {
            if (token == -1) throw new RuntimeException("unexpected end-of-stream in prompt");
            if (token != prompt[i]) raiseUnexpected(token, prompt[i]);
            token = reader.read();
        }

        while (token != -1 && token != '\n') {
            token = reader.read();
        }

        return GdbMiMessage.string(GdbMiType.Prompt, "");
    }

    public static Read<GdbMiRecord> readRecord (Reader reader) throws IOException
    {
        final var type = readSimpleString(reader);
        int token = type.token;
        if (token == -1) return null;

        final Read<GdbMiProperties> properties =
            (token == ',') ? readProperties(reader) : new Read<>(0, new GdbMiProperties());
        token = properties.token;

        return new Read<>(token, new GdbMiRecord(type.value, properties.value));
    }

    public static Read<GdbMiProperties> readProperties (Reader reader) throws IOException
    {
        final var properties = new HashMap<String, Object>();

        int token = -1;

        do
        {
            final var name = readSimpleString(reader);
            token = name.token;
            if (token == -1) throw new RuntimeException("unexpected end-of-stream in property-name");

            if (token != '=') raiseUnexpected(token, '=');

            final var value = readPropertyValue(reader);
            token = value.token;

            properties.put(name.value, value.value);
        }
        while (token == ',');

        return new Read<>(token, new GdbMiProperties(properties));
    }

    public static Read<Object> readPropertyValue (Reader reader) throws IOException
    {
        int token = reader.read();
        if (token == -1) throw new RuntimeException("unexpected end-of-stream in property-value");

        switch (token)
        {
        case '{':
            final var properties = readProperties(reader);
            token = properties.token;
            if (token == -1) throw new RuntimeException("unexpected end-of-stream in property-value");
            if (token != '}') raiseUnexpected(token, '}');
            token = reader.read();
            return new Read<>(token, properties.value);
        case '"':
            final var quotedString = readFinishQuotedString(token, reader);
            return new Read<>(quotedString.token, quotedString.value);
        default:
            final var string = readFinishSimpleString(token, reader);
            return new Read<>(string.token, string.value);
        }
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
        if (token == -1) throw new RuntimeException("unexpected end-of-string in quoted-string");

        while (token != '"')
        {
            builder.append((char) token);
            token = reader.read();
            if (token == -1) return new Read<>(token, null);
            if (token == '\n') throw new RuntimeException("unexpected new-line in quoted-string");
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
