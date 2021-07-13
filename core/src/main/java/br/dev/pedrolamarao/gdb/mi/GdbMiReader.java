package br.dev.pedrolamarao.gdb.mi;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.Charset;
import java.util.ArrayList;
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
        final int next;
        final T value;

        public Read (int token, T foo)
        {
            this.next = token;
            this.value = foo;
        }

        @Override public String toString () { return String.format("next = %s, value = %s", next, value); }
    }

    public static GdbMiMessage readMessage (Reader reader) throws IOException
    {
        final Read<Integer> context = readContext(reader);
        final int token = context.next;
        if (token == -1) return null;

        switch (context.next)
        {
            case '~':
                return readFinishStringMessage(GdbMiType.Console, context.value, reader);
            case '@':
                return readFinishStringMessage(GdbMiType.Target, context.value, reader);
            case '&':
                return readFinishStringMessage(GdbMiType.Log, context.value, reader);
            case '*':
                return readFinishRecordMessage(GdbMiType.Execute, context.value, reader);
            case '=':
                return readFinishRecordMessage(GdbMiType.Notify, context.value, reader);
            case '+':
                return readFinishRecordMessage(GdbMiType.Status, context.value, reader);
            case '^':
                return readFinishRecordMessage(GdbMiType.Result, context.value, reader);
            case '(':
                return readFinishPrompt(token, reader);
            default:
                throw new RuntimeException("unexpected token in message-type: " + (char) token);
        }
    }

    static GdbMiMessage.StringMessage readFinishStringMessage (GdbMiType type, Integer context, Reader reader) throws IOException
    {
        final var string = readString(reader);
        int token = string.next;

        while (token != -1 && token != '\n') {
            token = reader.read();
        }

        return GdbMiMessage.string(type, context, string.value);
    }

    static GdbMiMessage.RecordMessage readFinishRecordMessage (GdbMiType type, Integer context, Reader reader) throws IOException
    {
        final var record = readRecord(reader);
        int token = record.next;

        while (token != -1 && token != '\n') {
            token = reader.read();
        }

        return GdbMiMessage.record(type, context, record.value);
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

        return GdbMiMessage.string(GdbMiType.Prompt, null, "");
    }

    public static Read<Integer> readContext (Reader reader) throws IOException
    {
        int token = reader.read();
        if (token == -1) return new Read<>(token, null);
        if (! Character.isDigit(token)) return new Read<>(token, null);

        final var builder = new StringBuilder();
        do {
            builder.append((char) token);
            token = reader.read();
        }
        while (Character.isDigit(token));

        return new Read<>(token, Integer.parseInt( builder.toString() ));
    }

    public static Read<GdbMiRecord> readRecord (Reader reader) throws IOException
    {
        final var type = readSimpleString(reader);
        int token = type.next;
        if (token == -1) return null;

        final Read<GdbMiProperties> properties =
            (token == ',') ? readProperties(reader) : new Read<>(0, new GdbMiProperties());
        token = properties.next;

        return new Read<>(token, new GdbMiRecord(type.value, properties.value));
    }

    public static Read<GdbMiProperties> readProperties (Reader reader) throws IOException
    {
        final var properties = new HashMap<String, Object>();

        int token;

        do
        {
            final var name = readSimpleString(reader);
            token = name.next;
            if (token == -1) throw new RuntimeException("unexpected end-of-stream in property-name");

            if (token != '=') raiseUnexpected(token, '=');

            final var value = readPropertyValue(reader);
            token = value.next;

            properties.put(name.value, value.value);
        }
        while (token == ',');

        return new Read<>(token, new GdbMiProperties(properties));
    }

    public static Read<GdbMiList> readPropertyValues (Reader reader) throws IOException
    {
        final var list = new ArrayList<>();

        int token;

        do
        {
            final var value = readPropertyValue(reader);
            token = value.next;
            list.add(value.value);
        }
        while (token == ',');

        return new Read<>(token, new GdbMiList(list));
    }

    public static Read<Object> readPropertyValue (Reader reader) throws IOException
    {
        int token = reader.read();
        if (token == -1) throw new RuntimeException("unexpected end-of-stream in property-value");

        switch (token)
        {
        case '{':
            final var properties = readProperties(reader);
            token = properties.next;
            if (token == -1) throw new RuntimeException("unexpected end-of-stream in property-value");
            if (token != '}') raiseUnexpected(token, '}');
            token = reader.read();
            return new Read<>(token, properties.value);
        case '[':
            final var list = readPropertyValues(reader);
            token = list.next;
            if (token == -1) throw new RuntimeException("unexpected end-of-stream in property-value");
            if (token != ']') raiseUnexpected(token, ']');
            token = reader.read();
            return new Read<>(token, list.value);
        case '"':
            final var quotedString = readFinishQuotedString(token, reader);
            return new Read<>(quotedString.next, quotedString.value);
        default:
            final var string = readFinishSimpleString(token, reader);
            return new Read<>(string.next, string.value);
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

        while (Character.isAlphabetic(token) || token == '-')
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
            if (token == '\\') {
                token = reader.read();
                if (token == -1) throw new RuntimeException("unexpected end-of-stream in escape-sequence");
            }

            builder.append((char) token);
            token = reader.read();
            if (token == -1) throw new RuntimeException("unexpected end-of-stream in quoted-string");
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
