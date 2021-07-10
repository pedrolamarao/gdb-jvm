package br.dev.pedrolamarao.gdb.mi;

import java.util.Collections;
import java.util.Map;

public abstract class GdbMiMessage
{
    GdbMiMessage ()
    {

    }

    public abstract GdbMiType type ();

    public static final class StringMessage extends GdbMiMessage
    {
        final GdbMiType type;

        final String content;

        StringMessage (GdbMiType type, String content)
        {
            this.type = type;
            this.content = content;
        }

        public String content ()
        {
            return content;
        }

        @Override
        public GdbMiType type ()
        {
            return type;
        }
    }

    public static StringMessage string (GdbMiType type, String content)
    {
        return new StringMessage(type, content);
    }

    public static final class RecordMessage extends GdbMiMessage
    {
        final GdbMiType type;

        final String class_;

        final Map<String, String> properties;

        RecordMessage (GdbMiType type, String class_, Map<String, String> properties)
        {
            this.type = type;
            this.class_ = class_;
            this.properties = properties;
        }

        public String class_ ()
        {
            return class_;
        }

        public Map<String, String> properties () { return Collections.unmodifiableMap(properties); }

        @Override
        public GdbMiType type ()
        {
            return type;
        }
    }

    public static RecordMessage record (GdbMiType type, String class_, Map<String, String> properties)
    {
        return new RecordMessage(type, class_, properties);
    }
}
