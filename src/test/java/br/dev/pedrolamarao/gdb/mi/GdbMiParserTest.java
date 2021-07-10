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
    public void readRecord () throws IOException
    {
        final var type = GdbMiType.Notify;

        var read =
            readFinishRecordMessage(type, new StringReader("event,foo=bar,long=\"hello world\",meh=duh"));
        assertThat(read, notNullValue());
        assertThat(read.class_(), equalTo("event"));
        assertThat(read.properties().size(), equalTo(3));
        assertThat(read.properties().get("foo"), equalTo("bar"));
        assertThat(read.properties().get("long"), equalTo("hello world"));
        assertThat(read.properties().get("meh"), equalTo("duh"));
    }

    @Test
    public void readProperties () throws IOException
    {
        var read = GdbMiReader.readProperties(new StringReader("foo=bar"));
        assertThat(read, notNullValue());
        assertThat(read.token, equalTo(-1));
        assertThat(read.value.size(), equalTo(1));
        assertThat(read.value.get("foo"), equalTo("bar"));

        read = GdbMiReader.readProperties(new StringReader("foo=bar,long=\"hello world\",meh=duh"));
        assertThat(read, notNullValue());
        assertThat(read.token, equalTo(-1));
        assertThat(read.value.size(), equalTo(3));
        assertThat(read.value.get("foo"), equalTo("bar"));
        assertThat(read.value.get("long"), equalTo("hello world"));
        assertThat(read.value.get("meh"), equalTo("duh"));
    }

    @Test
    public void readString () throws IOException
    {
        var read = GdbMiReader.readString(new StringReader("abcd,"));
        assertThat(read.value, equalTo("abcd"));
        assertThat((char) read.token, equalTo(','));

        read = GdbMiReader.readString(new StringReader("\"ab 123-cd\","));
        assertThat(read.value, equalTo("ab 123-cd"));
        assertThat((char) read.token, equalTo(','));
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
