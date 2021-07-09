package br.dev.pedrolamarao.gdb.mi.grammar;

import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.hamcrest.Matcher;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.IOException;
import java.util.stream.Stream;

import static br.dev.pedrolamarao.gdb.mi.grammar.ParseTreeMatcher.matchesTree;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.instanceOf;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assumptions.assumeTrue;
import static org.junit.jupiter.params.provider.Arguments.arguments;

public class ParserTest 
{
	@ParameterizedTest(name="parse : {0}")
	@MethodSource("resources")
	public void parse (String resource, Matcher<Object> matchesTree) throws IOException
	{
		final var error = new ErrorListener();

		try (var stream = getClass().getResourceAsStream("/" + resource))
		{
			assumeTrue(stream != null);

			final var lexer = new GdbMiGrammarLexer(CharStreams.fromStream(stream));
			lexer.addErrorListener(error);

			final var parser = new GdbMiGrammarParser(new CommonTokenStream(lexer));
			parser.addErrorListener(error);

			final var tree = parser.output();
			assertFalse(error.get());
			assertThat(tree, matchesTree);
		}
	}

	@Test
	public void parseConversation () throws IOException
	{
		final var error = new ErrorListener();

		try (var stream = getClass().getResourceAsStream("/foo"))
		{
			assumeTrue(stream != null);

			final var lexer = new GdbMiGrammarLexer(CharStreams.fromStream(stream));
			lexer.addErrorListener(error);

			final var parser = new GdbMiGrammarParser(new CommonTokenStream(lexer));
			parser.addErrorListener(error);

			GdbMiGrammarParser.OutputContext tree;

			// 1

			tree = parser.output();
			assertFalse(error.get());
			assertThat(
				tree,
				matchesTree(
					GdbMiGrammarParser.OutputContext.class,
					matchesTree(
						GdbMiGrammarParser.OutOfBandContext.class,
						matchesTree(
							GdbMiGrammarParser.AsyncRecordContext.class,
							instanceOf(GdbMiGrammarParser.NotifyAsyncRecordContext.class)
						)
					),
					matchesTree(
						GdbMiGrammarParser.OutOfBandContext.class,
						matchesTree(
							GdbMiGrammarParser.StreamOutputContext.class,
							instanceOf(GdbMiGrammarParser.ConsoleStreamOutputContext.class)
						)
					),
					matchesTree(
						GdbMiGrammarParser.OutOfBandContext.class,
						matchesTree(
							GdbMiGrammarParser.StreamOutputContext.class,
							instanceOf(GdbMiGrammarParser.ConsoleStreamOutputContext.class)
						)
					),
					matchesTree(
						GdbMiGrammarParser.OutOfBandContext.class,
						matchesTree(
							GdbMiGrammarParser.StreamOutputContext.class,
							instanceOf(GdbMiGrammarParser.ConsoleStreamOutputContext.class)
						)
					),
					matchesTree(
						GdbMiGrammarParser.OutOfBandContext.class,
						matchesTree(
							GdbMiGrammarParser.StreamOutputContext.class,
							instanceOf(GdbMiGrammarParser.ConsoleStreamOutputContext.class)
						)
					),
					matchesTree(
						GdbMiGrammarParser.OutOfBandContext.class,
						matchesTree(
							GdbMiGrammarParser.StreamOutputContext.class,
							instanceOf(GdbMiGrammarParser.ConsoleStreamOutputContext.class)
						)
					),
					matchesTree(
						GdbMiGrammarParser.OutOfBandContext.class,
						matchesTree(
							GdbMiGrammarParser.StreamOutputContext.class,
							instanceOf(GdbMiGrammarParser.ConsoleStreamOutputContext.class)
						)
					),
					matchesTree(
						GdbMiGrammarParser.OutOfBandContext.class,
						matchesTree(
							GdbMiGrammarParser.StreamOutputContext.class,
							instanceOf(GdbMiGrammarParser.ConsoleStreamOutputContext.class)
						)
					),
					matchesTree(
						GdbMiGrammarParser.OutOfBandContext.class,
						matchesTree(
							GdbMiGrammarParser.StreamOutputContext.class,
							instanceOf(GdbMiGrammarParser.ConsoleStreamOutputContext.class)
						)
					),
					matchesTree(
						GdbMiGrammarParser.OutOfBandContext.class,
						matchesTree(
							GdbMiGrammarParser.StreamOutputContext.class,
							instanceOf(GdbMiGrammarParser.ConsoleStreamOutputContext.class)
						)
					),
					matchesTree(
						GdbMiGrammarParser.OutOfBandContext.class,
						matchesTree(
							GdbMiGrammarParser.StreamOutputContext.class,
							instanceOf(GdbMiGrammarParser.ConsoleStreamOutputContext.class)
						)
					),
					matchesTree(
						GdbMiGrammarParser.OutOfBandContext.class,
						matchesTree(
							GdbMiGrammarParser.StreamOutputContext.class,
							instanceOf(GdbMiGrammarParser.ConsoleStreamOutputContext.class)
						)
					),
					matchesTree(
						GdbMiGrammarParser.OutOfBandContext.class,
						matchesTree(
							GdbMiGrammarParser.StreamOutputContext.class,
							instanceOf(GdbMiGrammarParser.ConsoleStreamOutputContext.class)
						)
					),
					matchesTree(
						GdbMiGrammarParser.OutOfBandContext.class,
						matchesTree(
							GdbMiGrammarParser.StreamOutputContext.class,
							instanceOf(GdbMiGrammarParser.ConsoleStreamOutputContext.class)
						)
					),
					instanceOf(GdbMiGrammarParser.GdbContext.class)
				)
			);

			// 2

			tree = parser.output();
			assertFalse(error.get());
			assertThat(
				tree,
				matchesTree(
					GdbMiGrammarParser.OutputContext.class,
					matchesTree(
						GdbMiGrammarParser.OutOfBandContext.class,
						matchesTree(
							GdbMiGrammarParser.StreamOutputContext.class,
							instanceOf(GdbMiGrammarParser.LogStreamOutputContext.class)
						)
					),
					matchesTree(
						GdbMiGrammarParser.OutOfBandContext.class,
						matchesTree(
							GdbMiGrammarParser.StreamOutputContext.class,
							instanceOf(GdbMiGrammarParser.ConsoleStreamOutputContext.class)
						)
					),
					matchesTree(
						GdbMiGrammarParser.OutOfBandContext.class,
						matchesTree(
							GdbMiGrammarParser.AsyncRecordContext.class,
							instanceOf(GdbMiGrammarParser.NotifyAsyncRecordContext.class)
						)
					),
					instanceOf(GdbMiGrammarParser.ResultRecordContext.class),
					instanceOf(GdbMiGrammarParser.GdbContext.class)
				)
			);

			// 3

			tree = parser.output();
			assertFalse(error.get());
			assertThat(
				tree,
				matchesTree(
					GdbMiGrammarParser.OutputContext.class,
					matchesTree(
						GdbMiGrammarParser.OutOfBandContext.class,
						matchesTree(
							GdbMiGrammarParser.StreamOutputContext.class,
							instanceOf(GdbMiGrammarParser.LogStreamOutputContext.class)
						)
					),
					matchesTree(
						GdbMiGrammarParser.OutOfBandContext.class,
						matchesTree(
							GdbMiGrammarParser.StreamOutputContext.class,
							instanceOf(GdbMiGrammarParser.ConsoleStreamOutputContext.class)
						)
					),
					matchesTree(
						GdbMiGrammarParser.OutOfBandContext.class,
						matchesTree(
							GdbMiGrammarParser.AsyncRecordContext.class,
							instanceOf(GdbMiGrammarParser.NotifyAsyncRecordContext.class)
						)
					),
					matchesTree(
						GdbMiGrammarParser.OutOfBandContext.class,
						matchesTree(
							GdbMiGrammarParser.AsyncRecordContext.class,
							instanceOf(GdbMiGrammarParser.NotifyAsyncRecordContext.class)
						)
					),
					matchesTree(
						GdbMiGrammarParser.OutOfBandContext.class,
						matchesTree(
							GdbMiGrammarParser.StreamOutputContext.class,
							instanceOf(GdbMiGrammarParser.ConsoleStreamOutputContext.class)
						)
					),
					matchesTree(
						GdbMiGrammarParser.OutOfBandContext.class,
						matchesTree(
							GdbMiGrammarParser.AsyncRecordContext.class,
							instanceOf(GdbMiGrammarParser.ExecAsyncRecordContext.class)
						)
					),
					instanceOf(GdbMiGrammarParser.ResultRecordContext.class),
					instanceOf(GdbMiGrammarParser.GdbContext.class)
				)
			);

			// 4

			tree = parser.output();
			assertFalse(error.get());
			assertThat(
				tree,
				matchesTree(
					GdbMiGrammarParser.OutputContext.class,
					matchesTree(
						GdbMiGrammarParser.OutOfBandContext.class,
						matchesTree(
							GdbMiGrammarParser.StreamOutputContext.class,
							instanceOf(GdbMiGrammarParser.LogStreamOutputContext.class)
						)
					),
					matchesTree(
						GdbMiGrammarParser.OutOfBandContext.class,
						matchesTree(
							GdbMiGrammarParser.StreamOutputContext.class,
							instanceOf(GdbMiGrammarParser.ConsoleStreamOutputContext.class)
						)
					),
					matchesTree(
						GdbMiGrammarParser.OutOfBandContext.class,
						matchesTree(
							GdbMiGrammarParser.AsyncRecordContext.class,
							instanceOf(GdbMiGrammarParser.NotifyAsyncRecordContext.class)
						)
					),
					instanceOf(GdbMiGrammarParser.ResultRecordContext.class),
					instanceOf(GdbMiGrammarParser.GdbContext.class)
				)
			);

			// 5

			tree = parser.output();
			assertFalse(error.get());
			assertThat(
				tree,
				matchesTree(
					GdbMiGrammarParser.OutputContext.class,
					matchesTree(
						GdbMiGrammarParser.OutOfBandContext.class,
						matchesTree(
							GdbMiGrammarParser.StreamOutputContext.class,
							instanceOf(GdbMiGrammarParser.LogStreamOutputContext.class)
						)
					),
					matchesTree(
						GdbMiGrammarParser.OutOfBandContext.class,
						matchesTree(
							GdbMiGrammarParser.StreamOutputContext.class,
							instanceOf(GdbMiGrammarParser.ConsoleStreamOutputContext.class)
						)
					),
					instanceOf(GdbMiGrammarParser.ResultRecordContext.class),
					matchesTree(
						GdbMiGrammarParser.OutOfBandContext.class,
						matchesTree(
							GdbMiGrammarParser.AsyncRecordContext.class,
							instanceOf(GdbMiGrammarParser.ExecAsyncRecordContext.class)
						)
					),
					instanceOf(GdbMiGrammarParser.GdbContext.class)
				)
			);

			// 6

			tree = parser.output();
			assertFalse(error.get());
			assertThat(
				tree,
				matchesTree(
					GdbMiGrammarParser.OutputContext.class,
					matchesTree(
						GdbMiGrammarParser.OutOfBandContext.class,
						matchesTree(
							GdbMiGrammarParser.AsyncRecordContext.class,
							instanceOf(GdbMiGrammarParser.NotifyAsyncRecordContext.class)
						)
					),
					matchesTree(
						GdbMiGrammarParser.OutOfBandContext.class,
						matchesTree(
							GdbMiGrammarParser.StreamOutputContext.class,
							instanceOf(GdbMiGrammarParser.ConsoleStreamOutputContext.class)
						)
					),
					matchesTree(
						GdbMiGrammarParser.OutOfBandContext.class,
						matchesTree(
							GdbMiGrammarParser.StreamOutputContext.class,
							instanceOf(GdbMiGrammarParser.ConsoleStreamOutputContext.class)
						)
					),
					matchesTree(
						GdbMiGrammarParser.OutOfBandContext.class,
						matchesTree(
							GdbMiGrammarParser.AsyncRecordContext.class,
							instanceOf(GdbMiGrammarParser.ExecAsyncRecordContext.class)
						)
					),
					matchesTree(
						GdbMiGrammarParser.OutOfBandContext.class,
						matchesTree(
							GdbMiGrammarParser.AsyncRecordContext.class,
							instanceOf(GdbMiGrammarParser.NotifyAsyncRecordContext.class)
						)
					),
					instanceOf(GdbMiGrammarParser.GdbContext.class)
				)
			);

			// 7

			tree = parser.output();
			assertFalse(error.get());
			assertThat(
				tree,
				matchesTree(
					GdbMiGrammarParser.OutputContext.class,
					matchesTree(
						GdbMiGrammarParser.OutOfBandContext.class,
						matchesTree(
							GdbMiGrammarParser.StreamOutputContext.class,
							instanceOf(GdbMiGrammarParser.LogStreamOutputContext.class)
						)
					),
					matchesTree(
						GdbMiGrammarParser.OutOfBandContext.class,
						matchesTree(
							GdbMiGrammarParser.StreamOutputContext.class,
							instanceOf(GdbMiGrammarParser.ConsoleStreamOutputContext.class)
						)
					),
					matchesTree(
						GdbMiGrammarParser.OutOfBandContext.class,
						matchesTree(
							GdbMiGrammarParser.AsyncRecordContext.class,
							instanceOf(GdbMiGrammarParser.NotifyAsyncRecordContext.class)
						)
					),
					instanceOf(GdbMiGrammarParser.ResultRecordContext.class),
					instanceOf(GdbMiGrammarParser.GdbContext.class)
				)
			);

			// 8

			tree = parser.output();
			assertFalse(error.get());
			assertThat(
				tree,
				matchesTree(
					GdbMiGrammarParser.OutputContext.class,
					matchesTree(
						GdbMiGrammarParser.OutOfBandContext.class,
						matchesTree(
							GdbMiGrammarParser.StreamOutputContext.class,
							instanceOf(GdbMiGrammarParser.LogStreamOutputContext.class)
						)
					),
					matchesTree(
						GdbMiGrammarParser.OutOfBandContext.class,
						matchesTree(
							GdbMiGrammarParser.StreamOutputContext.class,
							instanceOf(GdbMiGrammarParser.ConsoleStreamOutputContext.class)
						)
					),
					instanceOf(GdbMiGrammarParser.ResultRecordContext.class),
					matchesTree(
						GdbMiGrammarParser.OutOfBandContext.class,
						matchesTree(
							GdbMiGrammarParser.AsyncRecordContext.class,
							instanceOf(GdbMiGrammarParser.ExecAsyncRecordContext.class)
						)
					),
					instanceOf(GdbMiGrammarParser.GdbContext.class)
				)
			);

			// 9

			tree = parser.output();
			assertFalse(error.get());
			assertThat(
				tree,
				matchesTree(
					GdbMiGrammarParser.OutputContext.class,
					matchesTree(
						GdbMiGrammarParser.OutOfBandContext.class,
						matchesTree(
							GdbMiGrammarParser.AsyncRecordContext.class,
							instanceOf(GdbMiGrammarParser.NotifyAsyncRecordContext.class)
						)
					),
					matchesTree(
						GdbMiGrammarParser.OutOfBandContext.class,
						matchesTree(
							GdbMiGrammarParser.StreamOutputContext.class,
							instanceOf(GdbMiGrammarParser.ConsoleStreamOutputContext.class)
						)
					),
					matchesTree(
						GdbMiGrammarParser.OutOfBandContext.class,
						matchesTree(
							GdbMiGrammarParser.StreamOutputContext.class,
							instanceOf(GdbMiGrammarParser.ConsoleStreamOutputContext.class)
						)
					),
					matchesTree(
						GdbMiGrammarParser.OutOfBandContext.class,
						matchesTree(
							GdbMiGrammarParser.StreamOutputContext.class,
							instanceOf(GdbMiGrammarParser.ConsoleStreamOutputContext.class)
						)
					),
					matchesTree(
						GdbMiGrammarParser.OutOfBandContext.class,
						matchesTree(
							GdbMiGrammarParser.StreamOutputContext.class,
							instanceOf(GdbMiGrammarParser.ConsoleStreamOutputContext.class)
						)
					),
					matchesTree(
						GdbMiGrammarParser.OutOfBandContext.class,
						matchesTree(
							GdbMiGrammarParser.StreamOutputContext.class,
							instanceOf(GdbMiGrammarParser.ConsoleStreamOutputContext.class)
						)
					),
					matchesTree(
						GdbMiGrammarParser.OutOfBandContext.class,
						matchesTree(
							GdbMiGrammarParser.StreamOutputContext.class,
							instanceOf(GdbMiGrammarParser.ConsoleStreamOutputContext.class)
						)
					),
					matchesTree(
						GdbMiGrammarParser.OutOfBandContext.class,
						matchesTree(
							GdbMiGrammarParser.StreamOutputContext.class,
							instanceOf(GdbMiGrammarParser.ConsoleStreamOutputContext.class)
						)
					),
					matchesTree(
						GdbMiGrammarParser.OutOfBandContext.class,
						matchesTree(
							GdbMiGrammarParser.AsyncRecordContext.class,
							instanceOf(GdbMiGrammarParser.ExecAsyncRecordContext.class)
						)
					),
					instanceOf(GdbMiGrammarParser.GdbContext.class)
				)
			);

			// 10

			tree = parser.output();
			assertFalse(error.get());
			assertThat(
				tree,
				matchesTree(
					GdbMiGrammarParser.OutputContext.class,
					matchesTree(
						GdbMiGrammarParser.OutOfBandContext.class,
						matchesTree(
							GdbMiGrammarParser.StreamOutputContext.class,
							instanceOf(GdbMiGrammarParser.LogStreamOutputContext.class)
						)
					),
					matchesTree(
						GdbMiGrammarParser.OutOfBandContext.class,
						matchesTree(
							GdbMiGrammarParser.StreamOutputContext.class,
							instanceOf(GdbMiGrammarParser.ConsoleStreamOutputContext.class)
						)
					),
					matchesTree(
						GdbMiGrammarParser.OutOfBandContext.class,
						matchesTree(
							GdbMiGrammarParser.AsyncRecordContext.class,
							instanceOf(GdbMiGrammarParser.NotifyAsyncRecordContext.class)
						)
					),
					matchesTree(
						GdbMiGrammarParser.OutOfBandContext.class,
						matchesTree(
							GdbMiGrammarParser.StreamOutputContext.class,
							instanceOf(GdbMiGrammarParser.ConsoleStreamOutputContext.class)
						)
					),
					instanceOf(GdbMiGrammarParser.ResultRecordContext.class),
					instanceOf(GdbMiGrammarParser.GdbContext.class)
				)
			);
		}
	}

	@SuppressWarnings("unused")
	public static Stream<Arguments> resources ()
	{
		return Stream.of(
			arguments(
				"banner",
				matchesTree(
					GdbMiGrammarParser.OutputContext.class,
					matchesTree(
						GdbMiGrammarParser.OutOfBandContext.class,
						matchesTree(
							GdbMiGrammarParser.AsyncRecordContext.class,
							instanceOf(GdbMiGrammarParser.NotifyAsyncRecordContext.class)
						)
					),
					matchesTree(
						GdbMiGrammarParser.OutOfBandContext.class,
						matchesTree(
							GdbMiGrammarParser.StreamOutputContext.class,
							instanceOf(GdbMiGrammarParser.ConsoleStreamOutputContext.class)
						)
					),
					matchesTree(
						GdbMiGrammarParser.OutOfBandContext.class,
						matchesTree(
							GdbMiGrammarParser.StreamOutputContext.class,
							instanceOf(GdbMiGrammarParser.ConsoleStreamOutputContext.class)
						)
					),
					matchesTree(
						GdbMiGrammarParser.OutOfBandContext.class,
						matchesTree(
							GdbMiGrammarParser.StreamOutputContext.class,
							instanceOf(GdbMiGrammarParser.ConsoleStreamOutputContext.class)
						)
					),
					matchesTree(
						GdbMiGrammarParser.OutOfBandContext.class,
						matchesTree(
							GdbMiGrammarParser.StreamOutputContext.class,
							instanceOf(GdbMiGrammarParser.ConsoleStreamOutputContext.class)
						)
					),
					matchesTree(
						GdbMiGrammarParser.OutOfBandContext.class,
						matchesTree(
							GdbMiGrammarParser.StreamOutputContext.class,
							instanceOf(GdbMiGrammarParser.ConsoleStreamOutputContext.class)
						)
					),
					matchesTree(
						GdbMiGrammarParser.OutOfBandContext.class,
						matchesTree(
							GdbMiGrammarParser.StreamOutputContext.class,
							instanceOf(GdbMiGrammarParser.ConsoleStreamOutputContext.class)
						)
					),
					matchesTree(
						GdbMiGrammarParser.OutOfBandContext.class,
						matchesTree(
							GdbMiGrammarParser.StreamOutputContext.class,
							instanceOf(GdbMiGrammarParser.ConsoleStreamOutputContext.class)
						)
					),
					matchesTree(
						GdbMiGrammarParser.OutOfBandContext.class,
						matchesTree(
							GdbMiGrammarParser.StreamOutputContext.class,
							instanceOf(GdbMiGrammarParser.ConsoleStreamOutputContext.class)
						)
					),
					matchesTree(
						GdbMiGrammarParser.OutOfBandContext.class,
						matchesTree(
							GdbMiGrammarParser.StreamOutputContext.class,
							instanceOf(GdbMiGrammarParser.ConsoleStreamOutputContext.class)
						)
					),
					matchesTree(
						GdbMiGrammarParser.OutOfBandContext.class,
						matchesTree(
							GdbMiGrammarParser.StreamOutputContext.class,
							instanceOf(GdbMiGrammarParser.ConsoleStreamOutputContext.class)
						)
					),
					matchesTree(
						GdbMiGrammarParser.OutOfBandContext.class,
						matchesTree(
							GdbMiGrammarParser.StreamOutputContext.class,
							instanceOf(GdbMiGrammarParser.ConsoleStreamOutputContext.class)
						)
					),
					matchesTree(
						GdbMiGrammarParser.OutOfBandContext.class,
						matchesTree(
							GdbMiGrammarParser.StreamOutputContext.class,
							instanceOf(GdbMiGrammarParser.ConsoleStreamOutputContext.class)
						)
					),
					instanceOf(GdbMiGrammarParser.GdbContext.class)
				)
			),
			arguments(
				"file",
				matchesTree(
					GdbMiGrammarParser.OutputContext.class,
					matchesTree(
						GdbMiGrammarParser.OutOfBandContext.class,
						matchesTree(
							GdbMiGrammarParser.StreamOutputContext.class,
							instanceOf(GdbMiGrammarParser.LogStreamOutputContext.class)
						)
					),
					matchesTree(
						GdbMiGrammarParser.OutOfBandContext.class,
						matchesTree(
							GdbMiGrammarParser.StreamOutputContext.class,
							instanceOf(GdbMiGrammarParser.ConsoleStreamOutputContext.class)
						)
					),
					instanceOf(GdbMiGrammarParser.ResultRecordContext.class),
					instanceOf(GdbMiGrammarParser.GdbContext.class)
				)
			),
			arguments(
				"file_error",
				matchesTree(
					GdbMiGrammarParser.OutputContext.class,
					matchesTree(
						GdbMiGrammarParser.OutOfBandContext.class,
						matchesTree(
							GdbMiGrammarParser.StreamOutputContext.class,
							instanceOf(GdbMiGrammarParser.LogStreamOutputContext.class)
						)
					),
					matchesTree(
						GdbMiGrammarParser.OutOfBandContext.class,
						matchesTree(
							GdbMiGrammarParser.StreamOutputContext.class,
							instanceOf(GdbMiGrammarParser.LogStreamOutputContext.class)
						)
					),
					instanceOf(GdbMiGrammarParser.ResultRecordContext.class),
					instanceOf(GdbMiGrammarParser.GdbContext.class)
				)
			),
			arguments(
				"kill",
				matchesTree(
					GdbMiGrammarParser.OutputContext.class,
					matchesTree(
						GdbMiGrammarParser.OutOfBandContext.class,
						matchesTree(
							GdbMiGrammarParser.AsyncRecordContext.class,
							instanceOf(GdbMiGrammarParser.NotifyAsyncRecordContext.class)
						)
					),
					matchesTree(
						GdbMiGrammarParser.OutOfBandContext.class,
						matchesTree(
							GdbMiGrammarParser.StreamOutputContext.class,
							instanceOf(GdbMiGrammarParser.ConsoleStreamOutputContext.class)
						)
					),
					instanceOf(GdbMiGrammarParser.ResultRecordContext.class),
					instanceOf(GdbMiGrammarParser.GdbContext.class)
				)
			),
			arguments(
				"target_remote",
				matchesTree(
					GdbMiGrammarParser.OutputContext.class,
					matchesTree(
						GdbMiGrammarParser.OutOfBandContext.class,
						matchesTree(
							GdbMiGrammarParser.StreamOutputContext.class,
							instanceOf(GdbMiGrammarParser.LogStreamOutputContext.class)
						)
					),
					matchesTree(
						GdbMiGrammarParser.OutOfBandContext.class,
						matchesTree(
							GdbMiGrammarParser.StreamOutputContext.class,
							instanceOf(GdbMiGrammarParser.ConsoleStreamOutputContext.class)
						)
					),
					matchesTree(
						GdbMiGrammarParser.OutOfBandContext.class,
						matchesTree(
							GdbMiGrammarParser.AsyncRecordContext.class,
							instanceOf(GdbMiGrammarParser.NotifyAsyncRecordContext.class)
						)
					),
					matchesTree(
						GdbMiGrammarParser.OutOfBandContext.class,
						matchesTree(
							GdbMiGrammarParser.AsyncRecordContext.class,
							instanceOf(GdbMiGrammarParser.NotifyAsyncRecordContext.class)
						)
					),
					matchesTree(
						GdbMiGrammarParser.OutOfBandContext.class,
						matchesTree(
							GdbMiGrammarParser.StreamOutputContext.class,
							instanceOf(GdbMiGrammarParser.ConsoleStreamOutputContext.class)
						)
					),
					matchesTree(
						GdbMiGrammarParser.OutOfBandContext.class,
						matchesTree(
							GdbMiGrammarParser.AsyncRecordContext.class,
							instanceOf(GdbMiGrammarParser.ExecAsyncRecordContext.class)
						)
					),
					instanceOf(GdbMiGrammarParser.ResultRecordContext.class),
					instanceOf(GdbMiGrammarParser.GdbContext.class)
				)
			),
			arguments(
				"target_remote_warning",
				matchesTree(
					GdbMiGrammarParser.OutputContext.class,
					matchesTree(
						GdbMiGrammarParser.OutOfBandContext.class,
						matchesTree(
							GdbMiGrammarParser.StreamOutputContext.class,
							instanceOf(GdbMiGrammarParser.LogStreamOutputContext.class)
						)
					),
					matchesTree(
						GdbMiGrammarParser.OutOfBandContext.class,
						matchesTree(
							GdbMiGrammarParser.StreamOutputContext.class,
							instanceOf(GdbMiGrammarParser.ConsoleStreamOutputContext.class)
						)
					),
					matchesTree(
						GdbMiGrammarParser.OutOfBandContext.class,
						matchesTree(
							GdbMiGrammarParser.AsyncRecordContext.class,
							instanceOf(GdbMiGrammarParser.NotifyAsyncRecordContext.class)
						)
					),
					matchesTree(
						GdbMiGrammarParser.OutOfBandContext.class,
						matchesTree(
							GdbMiGrammarParser.StreamOutputContext.class,
							instanceOf(GdbMiGrammarParser.LogStreamOutputContext.class)
						)
					),
					matchesTree(
						GdbMiGrammarParser.OutOfBandContext.class,
						matchesTree(
							GdbMiGrammarParser.StreamOutputContext.class,
							instanceOf(GdbMiGrammarParser.LogStreamOutputContext.class)
						)
					),
					matchesTree(
						GdbMiGrammarParser.OutOfBandContext.class,
						matchesTree(
							GdbMiGrammarParser.AsyncRecordContext.class,
							instanceOf(GdbMiGrammarParser.NotifyAsyncRecordContext.class)
						)
					),
					matchesTree(
						GdbMiGrammarParser.OutOfBandContext.class,
						matchesTree(
							GdbMiGrammarParser.StreamOutputContext.class,
							instanceOf(GdbMiGrammarParser.ConsoleStreamOutputContext.class)
						)
					),
					matchesTree(
						GdbMiGrammarParser.OutOfBandContext.class,
						matchesTree(
							GdbMiGrammarParser.AsyncRecordContext.class,
							instanceOf(GdbMiGrammarParser.ExecAsyncRecordContext.class)
						)
					),
					instanceOf(GdbMiGrammarParser.ResultRecordContext.class),
					instanceOf(GdbMiGrammarParser.GdbContext.class)
				)
			),
			arguments(
				"thbreak",
				matchesTree(
					GdbMiGrammarParser.OutputContext.class,
					matchesTree(
						GdbMiGrammarParser.OutOfBandContext.class,
						matchesTree(
							GdbMiGrammarParser.StreamOutputContext.class,
							instanceOf(GdbMiGrammarParser.ConsoleStreamOutputContext.class)
						)
					),
					matchesTree(
						GdbMiGrammarParser.OutOfBandContext.class,
						matchesTree(
							GdbMiGrammarParser.AsyncRecordContext.class,
							instanceOf(GdbMiGrammarParser.NotifyAsyncRecordContext.class)
						)
					),
					instanceOf(GdbMiGrammarParser.ResultRecordContext.class),
					instanceOf(GdbMiGrammarParser.GdbContext.class)
				)
			)
		);
	}
}
