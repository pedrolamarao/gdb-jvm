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

    /**
     * Build string message.
     *
     * @param type     message type
     * @param content  message content
     * @return         new message
     */

    public static StringMessage string (GdbMiType type, String content)
    {
        return new StringMessage(type, content);
    }

    /**
     * GDB/MI record message.
     */

    public static final class RecordMessage extends GdbMiMessage
    {
        final GdbMiType type;

        final GdbMiRecord content;

        RecordMessage (GdbMiType type, GdbMiRecord content)
        {
            this.type = type;
            this.content = content;
        }

        public GdbMiRecord content ()
        {
            return content;
        }

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
    public static RecordMessage record (GdbMiType type, GdbMiRecord content)
    {
        return new RecordMessage(type, content);
    }
}
