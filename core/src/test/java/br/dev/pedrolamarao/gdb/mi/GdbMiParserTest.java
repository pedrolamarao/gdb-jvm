package br.dev.pedrolamarao.gdb.mi;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;

import static br.dev.pedrolamarao.gdb.mi.GdbMiReader.readFinishRecordMessage;
import static java.nio.charset.StandardCharsets.UTF_8;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assumptions.assumeTrue;

public class GdbMiParserTest
{
    @Test
    public void conversation () throws IOException
    {
        try (var stream = getClass().getResourceAsStream("/foo"))
        {
            assumeTrue(stream != null);

            final var reader = new InputStreamReader(stream, UTF_8);

            for (int i = 0, j = fooList.length; i != j; ++i)
            {
                final var message = GdbMiReader.readMessage(reader);
                assertThat("message " + i + ":", message, notNullValue());
                assertThat("message " + i + ":", message.type(), equalTo(fooList[i]));
            }
        }
    }

    @Test
    public void readMessage () throws IOException
    {
        var reader = new StringReader("123*event,foo=bar,meh={duh=\"hello world\"}\n");
        var read = GdbMiReader.fromReader(reader).read();
        assertThat(read, notNullValue());
        assertThat(read.type(), equalTo(GdbMiType.Execute));
        assertThat(read.context(), equalTo(123));
    }

    @Test
    public void readProperties () throws IOException
    {
        var reader = new StringReader("foo=bar");
        var read = GdbMiReader.readProperties(reader);
        assertThat(read, notNullValue());
        assertThat(read.next, equalTo(-1));
        assertThat(read.value.get("foo", String.class), equalTo("bar"));

        reader = new StringReader("foo=bar,long=\"hello world\",meh=duh");
        read = GdbMiReader.readProperties(reader);
        assertThat(read, notNullValue());
        assertThat(read.next, equalTo(-1));
        assertThat(read.value.get("foo", String.class), equalTo("bar"));
        assertThat(read.value.get("long", String.class), equalTo("hello world"));
        assertThat(read.value.get("meh", String.class), equalTo("duh"));

        reader = new StringReader("foo=bar,argh={meh=duh,long=\"hello world\"}");
        read = GdbMiReader.readProperties(reader);
        assertThat(read, notNullValue());
        assertThat(read.value.get("foo", String.class), equalTo("bar"));
        assertThat(
            read.value.get("argh", GdbMiProperties.class).get("meh", String.class),
            equalTo("duh")
        );
        assertThat(
            read.value.get("argh", GdbMiProperties.class).get("long", String.class),
            equalTo("hello world")
        );
    }

    @Test
    public void readQuotedString () throws IOException
    {
        var reader = new StringReader("\"No symbol table is loaded.  Use the \\\"file\\\" command.\"");
        var read = GdbMiReader.readFinishQuotedString(reader.read(), reader);
        assertThat(read.value, equalTo("No symbol table is loaded.  Use the \"file\" command."));
    }

    @Test
    public void readRecord () throws IOException
    {
        final var type = GdbMiType.Notify;
        var reader = new StringReader("event,foo=bar,long=\"hello world\",meh=duh");
        var read = readFinishRecordMessage(type, null, reader);
        assertThat(read, notNullValue());
        assertThat(read.content().type(), equalTo("event"));
        assertThat(read.content().properties().get("foo", String.class), equalTo("bar"));
        assertThat(read.content().properties().get("long", String.class), equalTo("hello world"));
        assertThat(read.content().properties().get("meh", String.class), equalTo("duh"));
    }

    @Test
    public void readString () throws IOException
    {
        var read = GdbMiReader.readString(new StringReader("abcd,"));
        assertThat(read.value, equalTo("abcd"));
        assertThat((char) read.next, equalTo(','));

        read = GdbMiReader.readString(new StringReader("\"ab 123-cd\","));
        assertThat(read.value, equalTo("ab 123-cd"));
        assertThat((char) read.next, equalTo(','));
    }

    @Test
    public void readStringMessage () throws IOException
    {
        var type = GdbMiType.Log;
        var reader = new StringReader("\"long text message\"\n");
        var read = GdbMiReader.readFinishStringMessage(type, null, reader);
        assertThat(read, notNullValue());
        assertThat(read.content(), equalTo("long text message"));
    }

    static final GdbMiType[] fooList =
    {
        GdbMiType.Notify,
        GdbMiType.Console,
        GdbMiType.Console,
        GdbMiType.Console,
        GdbMiType.Console,
        GdbMiType.Console,
        GdbMiType.Console,
        GdbMiType.Console,
        GdbMiType.Console,
        GdbMiType.Console,
        GdbMiType.Console,
        GdbMiType.Console,
        GdbMiType.Console,
        GdbMiType.Console,
        GdbMiType.Prompt,
        GdbMiType.Log,
        GdbMiType.Console,
        GdbMiType.Notify,
        GdbMiType.Result,
        GdbMiType.Prompt,
    };
}
