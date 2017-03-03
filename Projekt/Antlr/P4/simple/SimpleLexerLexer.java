// Generated from SimpleLexer.g4 by ANTLR 4.5.3
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.misc.*;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class SimpleLexerLexer extends Lexer {
	static { RuntimeMetaData.checkVersion("4.5.3", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		T__0=1, T__1=2, T__2=3, ID=4, INT=5, FLOAT=6, STRING=7, COMMENT=8, WS=9, 
		NEWLINE=10, ESC=11;
	public static String[] modeNames = {
		"DEFAULT_MODE"
	};

	public static final String[] ruleNames = {
		"T__0", "T__1", "T__2", "ID", "INT", "FLOAT", "STRING", "COMMENT", "WS", 
		"NEWLINE", "DIGIT", "ESC"
	};

	private static final String[] _LITERAL_NAMES = {
		null, "'bhead'", "':'", "';'"
	};
	private static final String[] _SYMBOLIC_NAMES = {
		null, null, null, null, "ID", "INT", "FLOAT", "STRING", "COMMENT", "WS", 
		"NEWLINE", "ESC"
	};
	public static final Vocabulary VOCABULARY = new VocabularyImpl(_LITERAL_NAMES, _SYMBOLIC_NAMES);

	/**
	 * @deprecated Use {@link #VOCABULARY} instead.
	 */
	@Deprecated
	public static final String[] tokenNames;
	static {
		tokenNames = new String[_SYMBOLIC_NAMES.length];
		for (int i = 0; i < tokenNames.length; i++) {
			tokenNames[i] = VOCABULARY.getLiteralName(i);
			if (tokenNames[i] == null) {
				tokenNames[i] = VOCABULARY.getSymbolicName(i);
			}

			if (tokenNames[i] == null) {
				tokenNames[i] = "<INVALID>";
			}
		}
	}

	@Override
	@Deprecated
	public String[] getTokenNames() {
		return tokenNames;
	}

	@Override

	public Vocabulary getVocabulary() {
		return VOCABULARY;
	}


	public SimpleLexerLexer(CharStream input) {
		super(input);
		_interp = new LexerATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@Override
	public String getGrammarFileName() { return "SimpleLexer.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public String[] getModeNames() { return modeNames; }

	@Override
	public ATN getATN() { return _ATN; }

	public static final String _serializedATN =
		"\3\u0430\ud6d1\u8206\uad2d\u4417\uaef1\u8d80\uaadd\2\rf\b\1\4\2\t\2\4"+
		"\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13\t"+
		"\13\4\f\t\f\4\r\t\r\3\2\3\2\3\2\3\2\3\2\3\2\3\3\3\3\3\4\3\4\3\5\6\5\'"+
		"\n\5\r\5\16\5(\3\6\6\6,\n\6\r\6\16\6-\3\7\6\7\61\n\7\r\7\16\7\62\3\7\3"+
		"\7\6\7\67\n\7\r\7\16\78\3\b\3\b\3\b\7\b>\n\b\f\b\16\bA\13\b\3\b\3\b\3"+
		"\t\3\t\3\t\3\t\7\tI\n\t\f\t\16\tL\13\t\3\t\7\tO\n\t\f\t\16\tR\13\t\3\t"+
		"\3\t\3\n\6\nW\n\n\r\n\16\nX\3\n\3\n\3\13\5\13^\n\13\3\13\3\13\3\f\3\f"+
		"\3\r\3\r\3\r\4?J\2\16\3\3\5\4\7\5\t\6\13\7\r\b\17\t\21\n\23\13\25\f\27"+
		"\2\31\r\3\2\7\4\2C\\c|\4\2\f\f\17\17\4\2\13\13\"\"\3\2\62;\b\2$$^^ddp"+
		"pttvvn\2\3\3\2\2\2\2\5\3\2\2\2\2\7\3\2\2\2\2\t\3\2\2\2\2\13\3\2\2\2\2"+
		"\r\3\2\2\2\2\17\3\2\2\2\2\21\3\2\2\2\2\23\3\2\2\2\2\25\3\2\2\2\2\31\3"+
		"\2\2\2\3\33\3\2\2\2\5!\3\2\2\2\7#\3\2\2\2\t&\3\2\2\2\13+\3\2\2\2\r\60"+
		"\3\2\2\2\17:\3\2\2\2\21D\3\2\2\2\23V\3\2\2\2\25]\3\2\2\2\27a\3\2\2\2\31"+
		"c\3\2\2\2\33\34\7d\2\2\34\35\7j\2\2\35\36\7g\2\2\36\37\7c\2\2\37 \7f\2"+
		"\2 \4\3\2\2\2!\"\7<\2\2\"\6\3\2\2\2#$\7=\2\2$\b\3\2\2\2%\'\t\2\2\2&%\3"+
		"\2\2\2\'(\3\2\2\2(&\3\2\2\2()\3\2\2\2)\n\3\2\2\2*,\5\27\f\2+*\3\2\2\2"+
		",-\3\2\2\2-+\3\2\2\2-.\3\2\2\2.\f\3\2\2\2/\61\5\27\f\2\60/\3\2\2\2\61"+
		"\62\3\2\2\2\62\60\3\2\2\2\62\63\3\2\2\2\63\64\3\2\2\2\64\66\7\60\2\2\65"+
		"\67\5\27\f\2\66\65\3\2\2\2\678\3\2\2\28\66\3\2\2\289\3\2\2\29\16\3\2\2"+
		"\2:?\7$\2\2;>\5\31\r\2<>\13\2\2\2=;\3\2\2\2=<\3\2\2\2>A\3\2\2\2?@\3\2"+
		"\2\2?=\3\2\2\2@B\3\2\2\2A?\3\2\2\2BC\7$\2\2C\20\3\2\2\2DE\7\61\2\2EF\7"+
		"\61\2\2FJ\3\2\2\2GI\13\2\2\2HG\3\2\2\2IL\3\2\2\2JK\3\2\2\2JH\3\2\2\2K"+
		"P\3\2\2\2LJ\3\2\2\2MO\n\3\2\2NM\3\2\2\2OR\3\2\2\2PN\3\2\2\2PQ\3\2\2\2"+
		"QS\3\2\2\2RP\3\2\2\2ST\b\t\2\2T\22\3\2\2\2UW\t\4\2\2VU\3\2\2\2WX\3\2\2"+
		"\2XV\3\2\2\2XY\3\2\2\2YZ\3\2\2\2Z[\b\n\2\2[\24\3\2\2\2\\^\7\17\2\2]\\"+
		"\3\2\2\2]^\3\2\2\2^_\3\2\2\2_`\7\f\2\2`\26\3\2\2\2ab\t\5\2\2b\30\3\2\2"+
		"\2cd\7^\2\2de\t\6\2\2e\32\3\2\2\2\r\2(-\628=?JPX]\3\b\2\2";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}