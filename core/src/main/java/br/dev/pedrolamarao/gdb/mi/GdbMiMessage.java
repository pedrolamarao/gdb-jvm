package br.dev.pedrolamarao.gdb.mi;

/**
 * GDB/MI message.
 */

public abstract class GdbMiMessage
{
    GdbMiMessage ()
    {

    }

    /**
     * Property: message context.
     *
     * @return value
     */

    public abstract Integer context ();

    /**
     * Property: message type.
     *
     * @return value
     */

    public abstract GdbMiType type ();

    /**
     * GDB/MI string message.
     */

    public static final class StringMessage extends GdbMiMessage
    {
        final String content;

        final Integer context;

        final GdbMiType type;

        StringMessage (String content, Integer context, GdbMiType type)
        {
            this.content = content;
            this.context = context;
            this.type = type;
        }

        public String content ()
        {
            return content;
        }

        @Override
        public Integer context () { return context; }

        @Override
        public GdbMiType type ()
        {
            return type;
        }
    }

    /**
     * Build string message.
     *
     * @param type     message type
     * @param content  message content
     * @return         new message
     */

    public static StringMessage string (GdbMiType type, Integer context, String content)
    {
        return new StringMessage(content, context, type);
    }

    /**
     * GDB/MI record message.
     */

    public static final class RecordMessage extends GdbMiMessage
    {
        final GdbMiRecord content;

        final Integer context;

        final GdbMiType type;

        RecordMessage (GdbMiRecord content, Integer context, GdbMiType type)
        {
            this.content = content;
            this.context = context;
            this.type = type;
        }

        public GdbMiRecord content ()
        {
            return content;
        }

        @Override
        public Integer context () { return context; }

        @Override
        public GdbMiType type ()
        {
            return type;
        }
    }

    /**
     * Build record message.
     *
     * @param type     message type
     * @param content  message content
     * @return         new message
     */
    public static RecordMessage record (GdbMiType type, Integer context, GdbMiRecord content)
    {
        return new RecordMessage(content, context, type);
    }
}
