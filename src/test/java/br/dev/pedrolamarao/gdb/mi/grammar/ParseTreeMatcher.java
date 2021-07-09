package br.dev.pedrolamarao.gdb.mi.grammar;

import org.antlr.v4.runtime.tree.ParseTree;
import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeDiagnosingMatcher;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Hamcrest {@link Matcher} for ANTLR4 {@link ParseTree}s.
 */

public class ParseTreeMatcher <T> extends TypeSafeDiagnosingMatcher<ParseTree>
{
    private final Class<? extends ParseTree> type;

    private final List<Matcher<? super T>> children;

    ParseTreeMatcher (Class<? extends ParseTree> type, List<Matcher<? super T>> children)
    {
        this.type = type;
        this.children = children;
    }

    public static ParseTreeMatcher<Void> matchesTree (Class<? extends ParseTree> type)
    {
        return new ParseTreeMatcher<>(type, Collections.emptyList());
    }

    @SafeVarargs
    public static <T> ParseTreeMatcher<T> matchesTree (Class<? extends ParseTree> type, Matcher<? super T>... children)
    {
        return new ParseTreeMatcher<T>(type, Arrays.asList(children));
    }

    public static <T> ParseTreeMatcher<T> matchesTree (Class<? extends ParseTree> type, List<Matcher<? super T>> children)
    {
        return new ParseTreeMatcher<T>(type, children);
    }

    @Override
    public boolean matchesSafely (ParseTree actual, Description description)
    {
        if (! type.isInstance(actual)) {
            description.appendValue(actual);
            description.appendText(" root is " + actual.getClass());
            return false;
        }

        if (actual.getChildCount() != children.size()) {
            description.appendValue(actual);
            description.appendText(" has " + actual.getChildCount() + " children");
            return false;
        }

        for (int i = 0, j = children.size(); i != j; ++i)
        {
            final var matcher = children.get(i);
            final var child = actual.getChild(i);

            if (! matcher.matches(child)) {
                description
                    .appendText("child " + i + ": expected ")
                    .appendDescriptionOf(matcher)
                    .appendText(" but ");
                matcher.describeMismatch(child, description);
                return false;
            }
        }

        return true;
    }

    @Override
    public void describeTo (Description description)
    {
        description.appendText("ParseTree root is " + type + " with " + children.size () + " children");
    }
}
