// Generated from Decaf.g4 by ANTLR 4.5
package com.github.jdvalenzuelah.parser;
import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.misc.*;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class DecafLexer extends Lexer {
	static { RuntimeMetaData.checkVersion("4.5", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		T__0=1, T__1=2, CLASS=3, INT=4, RETURN=5, VOID=6, IF=7, ELSE=8, FOR=9, 
		BREAK=10, CONTINUE=11, CALLOUT=12, TRUE=13, FALSE=14, BOOLEAN=15, LCURLY=16, 
		RCURLY=17, LBRACE=18, RBRACE=19, LSQUARE=20, RSQUARE=21, ADD=22, SUB=23, 
		MUL=24, DIV=25, EQ=26, SEMI=27, COMMA=28, AND=29, LESS=30, GREATER=31, 
		LESSEQUAL=32, GREATEREQUAL=33, EQUALTO=34, NOTEQUAL=35, EXCLAMATION=36, 
		CHAR_LITERAL=37, HEXMARK=38, HEX_LITERAL=39, STRING=40, DECIMAL_LITERAL=41, 
		COMMENT=42, WS=43, ID=44, INT_LITERAL=45, BOOL_LITERAL=46;
	public static String[] modeNames = {
		"DEFAULT_MODE"
	};

	public static final String[] ruleNames = {
		"T__0", "T__1", "CLASS", "INT", "RETURN", "VOID", "IF", "ELSE", "FOR", 
		"BREAK", "CONTINUE", "CALLOUT", "TRUE", "FALSE", "BOOLEAN", "LCURLY", 
		"RCURLY", "LBRACE", "RBRACE", "LSQUARE", "RSQUARE", "ADD", "SUB", "MUL", 
		"DIV", "EQ", "SEMI", "COMMA", "AND", "LESS", "GREATER", "LESSEQUAL", "GREATEREQUAL", 
		"EQUALTO", "NOTEQUAL", "EXCLAMATION", "CHAR", "CHAR_LITERAL", "HEXMARK", 
		"HEXA", "HEXDIGIT", "HEX_LITERAL", "STRING", "ESC", "DIGIT", "DECIMAL_LITERAL", 
		"COMMENT", "WS", "ALPHA", "ALPHA_NUM", "ID", "INT_LITERAL", "BOOL_LITERAL"
	};

	private static final String[] _LITERAL_NAMES = {
		null, "'%'", "'||'", "'class'", "'int'", "'return'", "'void'", "'if'", 
		"'else'", "'for'", "'break'", "'continue'", "'callout'", "'True'", "'False'", 
		"'boolean'", "'{'", "'}'", "'('", "')'", "'['", "']'", "'+'", "'-'", "'*'", 
		"'/'", "'='", "';'", "','", "'&&'", "'<'", "'>'", "'<='", "'>='", "'=='", 
		"'!='", "'!'", null, "'0x'"
	};
	private static final String[] _SYMBOLIC_NAMES = {
		null, null, null, "CLASS", "INT", "RETURN", "VOID", "IF", "ELSE", "FOR", 
		"BREAK", "CONTINUE", "CALLOUT", "TRUE", "FALSE", "BOOLEAN", "LCURLY", 
		"RCURLY", "LBRACE", "RBRACE", "LSQUARE", "RSQUARE", "ADD", "SUB", "MUL", 
		"DIV", "EQ", "SEMI", "COMMA", "AND", "LESS", "GREATER", "LESSEQUAL", "GREATEREQUAL", 
		"EQUALTO", "NOTEQUAL", "EXCLAMATION", "CHAR_LITERAL", "HEXMARK", "HEX_LITERAL", 
		"STRING", "DECIMAL_LITERAL", "COMMENT", "WS", "ID", "INT_LITERAL", "BOOL_LITERAL"
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


	public DecafLexer(CharStream input) {
		super(input);
		_interp = new LexerATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@Override
	public String getGrammarFileName() { return "Decaf.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public String[] getModeNames() { return modeNames; }

	@Override
	public ATN getATN() { return _ATN; }

	public static final String _serializedATN =
		"\3\u0430\ud6d1\u8206\uad2d\u4417\uaef1\u8d80\uaadd\2\60\u014b\b\1\4\2"+
		"\t\2\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4"+
		"\13\t\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22"+
		"\t\22\4\23\t\23\4\24\t\24\4\25\t\25\4\26\t\26\4\27\t\27\4\30\t\30\4\31"+
		"\t\31\4\32\t\32\4\33\t\33\4\34\t\34\4\35\t\35\4\36\t\36\4\37\t\37\4 \t"+
		" \4!\t!\4\"\t\"\4#\t#\4$\t$\4%\t%\4&\t&\4\'\t\'\4(\t(\4)\t)\4*\t*\4+\t"+
		"+\4,\t,\4-\t-\4.\t.\4/\t/\4\60\t\60\4\61\t\61\4\62\t\62\4\63\t\63\4\64"+
		"\t\64\4\65\t\65\4\66\t\66\3\2\3\2\3\3\3\3\3\3\3\4\3\4\3\4\3\4\3\4\3\4"+
		"\3\5\3\5\3\5\3\5\3\6\3\6\3\6\3\6\3\6\3\6\3\6\3\7\3\7\3\7\3\7\3\7\3\b\3"+
		"\b\3\b\3\t\3\t\3\t\3\t\3\t\3\n\3\n\3\n\3\n\3\13\3\13\3\13\3\13\3\13\3"+
		"\13\3\f\3\f\3\f\3\f\3\f\3\f\3\f\3\f\3\f\3\r\3\r\3\r\3\r\3\r\3\r\3\r\3"+
		"\r\3\16\3\16\3\16\3\16\3\16\3\17\3\17\3\17\3\17\3\17\3\17\3\20\3\20\3"+
		"\20\3\20\3\20\3\20\3\20\3\20\3\21\3\21\3\22\3\22\3\23\3\23\3\24\3\24\3"+
		"\25\3\25\3\26\3\26\3\27\3\27\3\30\3\30\3\31\3\31\3\32\3\32\3\33\3\33\3"+
		"\34\3\34\3\35\3\35\3\36\3\36\3\36\3\37\3\37\3 \3 \3!\3!\3!\3\"\3\"\3\""+
		"\3#\3#\3#\3$\3$\3$\3%\3%\3&\3&\3&\3&\3&\3&\5&\u00f4\n&\3\'\3\'\3\'\3\'"+
		"\3(\3(\3(\3)\3)\3*\3*\5*\u0101\n*\3+\3+\6+\u0105\n+\r+\16+\u0106\3,\3"+
		",\3,\7,\u010c\n,\f,\16,\u010f\13,\3,\3,\3-\3-\3-\3-\5-\u0117\n-\3.\3."+
		"\3/\3/\7/\u011d\n/\f/\16/\u0120\13/\3\60\3\60\3\60\3\60\7\60\u0126\n\60"+
		"\f\60\16\60\u0129\13\60\3\60\3\60\3\60\3\60\3\61\6\61\u0130\n\61\r\61"+
		"\16\61\u0131\3\61\3\61\3\62\5\62\u0137\n\62\3\63\3\63\5\63\u013b\n\63"+
		"\3\64\3\64\7\64\u013f\n\64\f\64\16\64\u0142\13\64\3\65\3\65\5\65\u0146"+
		"\n\65\3\66\3\66\5\66\u014a\n\66\3\u010d\2\67\3\3\5\4\7\5\t\6\13\7\r\b"+
		"\17\t\21\n\23\13\25\f\27\r\31\16\33\17\35\20\37\21!\22#\23%\24\'\25)\26"+
		"+\27-\30/\31\61\32\63\33\65\34\67\359\36;\37= ?!A\"C#E$G%I&K\2M\'O(Q\2"+
		"S\2U)W*Y\2[\2]+_,a-c\2e\2g.i/k\60\3\2\n\6\2\"#%(*]_\u0080\3\2))\4\2\13"+
		"\f^^\4\2CHch\3\2\62;\3\2\f\f\5\2\13\f\17\17\"\"\5\2C\\aac|\u0152\2\3\3"+
		"\2\2\2\2\5\3\2\2\2\2\7\3\2\2\2\2\t\3\2\2\2\2\13\3\2\2\2\2\r\3\2\2\2\2"+
		"\17\3\2\2\2\2\21\3\2\2\2\2\23\3\2\2\2\2\25\3\2\2\2\2\27\3\2\2\2\2\31\3"+
		"\2\2\2\2\33\3\2\2\2\2\35\3\2\2\2\2\37\3\2\2\2\2!\3\2\2\2\2#\3\2\2\2\2"+
		"%\3\2\2\2\2\'\3\2\2\2\2)\3\2\2\2\2+\3\2\2\2\2-\3\2\2\2\2/\3\2\2\2\2\61"+
		"\3\2\2\2\2\63\3\2\2\2\2\65\3\2\2\2\2\67\3\2\2\2\29\3\2\2\2\2;\3\2\2\2"+
		"\2=\3\2\2\2\2?\3\2\2\2\2A\3\2\2\2\2C\3\2\2\2\2E\3\2\2\2\2G\3\2\2\2\2I"+
		"\3\2\2\2\2M\3\2\2\2\2O\3\2\2\2\2U\3\2\2\2\2W\3\2\2\2\2]\3\2\2\2\2_\3\2"+
		"\2\2\2a\3\2\2\2\2g\3\2\2\2\2i\3\2\2\2\2k\3\2\2\2\3m\3\2\2\2\5o\3\2\2\2"+
		"\7r\3\2\2\2\tx\3\2\2\2\13|\3\2\2\2\r\u0083\3\2\2\2\17\u0088\3\2\2\2\21"+
		"\u008b\3\2\2\2\23\u0090\3\2\2\2\25\u0094\3\2\2\2\27\u009a\3\2\2\2\31\u00a3"+
		"\3\2\2\2\33\u00ab\3\2\2\2\35\u00b0\3\2\2\2\37\u00b6\3\2\2\2!\u00be\3\2"+
		"\2\2#\u00c0\3\2\2\2%\u00c2\3\2\2\2\'\u00c4\3\2\2\2)\u00c6\3\2\2\2+\u00c8"+
		"\3\2\2\2-\u00ca\3\2\2\2/\u00cc\3\2\2\2\61\u00ce\3\2\2\2\63\u00d0\3\2\2"+
		"\2\65\u00d2\3\2\2\2\67\u00d4\3\2\2\29\u00d6\3\2\2\2;\u00d8\3\2\2\2=\u00db"+
		"\3\2\2\2?\u00dd\3\2\2\2A\u00df\3\2\2\2C\u00e2\3\2\2\2E\u00e5\3\2\2\2G"+
		"\u00e8\3\2\2\2I\u00eb\3\2\2\2K\u00f3\3\2\2\2M\u00f5\3\2\2\2O\u00f9\3\2"+
		"\2\2Q\u00fc\3\2\2\2S\u0100\3\2\2\2U\u0102\3\2\2\2W\u0108\3\2\2\2Y\u0116"+
		"\3\2\2\2[\u0118\3\2\2\2]\u011a\3\2\2\2_\u0121\3\2\2\2a\u012f\3\2\2\2c"+
		"\u0136\3\2\2\2e\u013a\3\2\2\2g\u013c\3\2\2\2i\u0145\3\2\2\2k\u0149\3\2"+
		"\2\2mn\7\'\2\2n\4\3\2\2\2op\7~\2\2pq\7~\2\2q\6\3\2\2\2rs\7e\2\2st\7n\2"+
		"\2tu\7c\2\2uv\7u\2\2vw\7u\2\2w\b\3\2\2\2xy\7k\2\2yz\7p\2\2z{\7v\2\2{\n"+
		"\3\2\2\2|}\7t\2\2}~\7g\2\2~\177\7v\2\2\177\u0080\7w\2\2\u0080\u0081\7"+
		"t\2\2\u0081\u0082\7p\2\2\u0082\f\3\2\2\2\u0083\u0084\7x\2\2\u0084\u0085"+
		"\7q\2\2\u0085\u0086\7k\2\2\u0086\u0087\7f\2\2\u0087\16\3\2\2\2\u0088\u0089"+
		"\7k\2\2\u0089\u008a\7h\2\2\u008a\20\3\2\2\2\u008b\u008c\7g\2\2\u008c\u008d"+
		"\7n\2\2\u008d\u008e\7u\2\2\u008e\u008f\7g\2\2\u008f\22\3\2\2\2\u0090\u0091"+
		"\7h\2\2\u0091\u0092\7q\2\2\u0092\u0093\7t\2\2\u0093\24\3\2\2\2\u0094\u0095"+
		"\7d\2\2\u0095\u0096\7t\2\2\u0096\u0097\7g\2\2\u0097\u0098\7c\2\2\u0098"+
		"\u0099\7m\2\2\u0099\26\3\2\2\2\u009a\u009b\7e\2\2\u009b\u009c\7q\2\2\u009c"+
		"\u009d\7p\2\2\u009d\u009e\7v\2\2\u009e\u009f\7k\2\2\u009f\u00a0\7p\2\2"+
		"\u00a0\u00a1\7w\2\2\u00a1\u00a2\7g\2\2\u00a2\30\3\2\2\2\u00a3\u00a4\7"+
		"e\2\2\u00a4\u00a5\7c\2\2\u00a5\u00a6\7n\2\2\u00a6\u00a7\7n\2\2\u00a7\u00a8"+
		"\7q\2\2\u00a8\u00a9\7w\2\2\u00a9\u00aa\7v\2\2\u00aa\32\3\2\2\2\u00ab\u00ac"+
		"\7V\2\2\u00ac\u00ad\7t\2\2\u00ad\u00ae\7w\2\2\u00ae\u00af\7g\2\2\u00af"+
		"\34\3\2\2\2\u00b0\u00b1\7H\2\2\u00b1\u00b2\7c\2\2\u00b2\u00b3\7n\2\2\u00b3"+
		"\u00b4\7u\2\2\u00b4\u00b5\7g\2\2\u00b5\36\3\2\2\2\u00b6\u00b7\7d\2\2\u00b7"+
		"\u00b8\7q\2\2\u00b8\u00b9\7q\2\2\u00b9\u00ba\7n\2\2\u00ba\u00bb\7g\2\2"+
		"\u00bb\u00bc\7c\2\2\u00bc\u00bd\7p\2\2\u00bd \3\2\2\2\u00be\u00bf\7}\2"+
		"\2\u00bf\"\3\2\2\2\u00c0\u00c1\7\177\2\2\u00c1$\3\2\2\2\u00c2\u00c3\7"+
		"*\2\2\u00c3&\3\2\2\2\u00c4\u00c5\7+\2\2\u00c5(\3\2\2\2\u00c6\u00c7\7]"+
		"\2\2\u00c7*\3\2\2\2\u00c8\u00c9\7_\2\2\u00c9,\3\2\2\2\u00ca\u00cb\7-\2"+
		"\2\u00cb.\3\2\2\2\u00cc\u00cd\7/\2\2\u00cd\60\3\2\2\2\u00ce\u00cf\7,\2"+
		"\2\u00cf\62\3\2\2\2\u00d0\u00d1\7\61\2\2\u00d1\64\3\2\2\2\u00d2\u00d3"+
		"\7?\2\2\u00d3\66\3\2\2\2\u00d4\u00d5\7=\2\2\u00d58\3\2\2\2\u00d6\u00d7"+
		"\7.\2\2\u00d7:\3\2\2\2\u00d8\u00d9\7(\2\2\u00d9\u00da\7(\2\2\u00da<\3"+
		"\2\2\2\u00db\u00dc\7>\2\2\u00dc>\3\2\2\2\u00dd\u00de\7@\2\2\u00de@\3\2"+
		"\2\2\u00df\u00e0\7>\2\2\u00e0\u00e1\7?\2\2\u00e1B\3\2\2\2\u00e2\u00e3"+
		"\7@\2\2\u00e3\u00e4\7?\2\2\u00e4D\3\2\2\2\u00e5\u00e6\7?\2\2\u00e6\u00e7"+
		"\7?\2\2\u00e7F\3\2\2\2\u00e8\u00e9\7#\2\2\u00e9\u00ea\7?\2\2\u00eaH\3"+
		"\2\2\2\u00eb\u00ec\7#\2\2\u00ecJ\3\2\2\2\u00ed\u00f4\t\2\2\2\u00ee\u00ef"+
		"\7^\2\2\u00ef\u00f4\t\3\2\2\u00f0\u00f1\7^\2\2\u00f1\u00f4\7$\2\2\u00f2"+
		"\u00f4\t\4\2\2\u00f3\u00ed\3\2\2\2\u00f3\u00ee\3\2\2\2\u00f3\u00f0\3\2"+
		"\2\2\u00f3\u00f2\3\2\2\2\u00f4L\3\2\2\2\u00f5\u00f6\7)\2\2\u00f6\u00f7"+
		"\5K&\2\u00f7\u00f8\7)\2\2\u00f8N\3\2\2\2\u00f9\u00fa\7\62\2\2\u00fa\u00fb"+
		"\7z\2\2\u00fbP\3\2\2\2\u00fc\u00fd\t\5\2\2\u00fdR\3\2\2\2\u00fe\u0101"+
		"\5[.\2\u00ff\u0101\5Q)\2\u0100\u00fe\3\2\2\2\u0100\u00ff\3\2\2\2\u0101"+
		"T\3\2\2\2\u0102\u0104\5O(\2\u0103\u0105\5S*\2\u0104\u0103\3\2\2\2\u0105"+
		"\u0106\3\2\2\2\u0106\u0104\3\2\2\2\u0106\u0107\3\2\2\2\u0107V\3\2\2\2"+
		"\u0108\u010d\7$\2\2\u0109\u010c\5Y-\2\u010a\u010c\13\2\2\2\u010b\u0109"+
		"\3\2\2\2\u010b\u010a\3\2\2\2\u010c\u010f\3\2\2\2\u010d\u010e\3\2\2\2\u010d"+
		"\u010b\3\2\2\2\u010e\u0110\3\2\2\2\u010f\u010d\3\2\2\2\u0110\u0111\7$"+
		"\2\2\u0111X\3\2\2\2\u0112\u0113\7^\2\2\u0113\u0117\7$\2\2\u0114\u0115"+
		"\7^\2\2\u0115\u0117\7^\2\2\u0116\u0112\3\2\2\2\u0116\u0114\3\2\2\2\u0117"+
		"Z\3\2\2\2\u0118\u0119\t\6\2\2\u0119\\\3\2\2\2\u011a\u011e\5[.\2\u011b"+
		"\u011d\5[.\2\u011c\u011b\3\2\2\2\u011d\u0120\3\2\2\2\u011e\u011c\3\2\2"+
		"\2\u011e\u011f\3\2\2\2\u011f^\3\2\2\2\u0120\u011e\3\2\2\2\u0121\u0122"+
		"\7\61\2\2\u0122\u0123\7\61\2\2\u0123\u0127\3\2\2\2\u0124\u0126\n\7\2\2"+
		"\u0125\u0124\3\2\2\2\u0126\u0129\3\2\2\2\u0127\u0125\3\2\2\2\u0127\u0128"+
		"\3\2\2\2\u0128\u012a\3\2\2\2\u0129\u0127\3\2\2\2\u012a\u012b\7\f\2\2\u012b"+
		"\u012c\3\2\2\2\u012c\u012d\b\60\2\2\u012d`\3\2\2\2\u012e\u0130\t\b\2\2"+
		"\u012f\u012e\3\2\2\2\u0130\u0131\3\2\2\2\u0131\u012f\3\2\2\2\u0131\u0132"+
		"\3\2\2\2\u0132\u0133\3\2\2\2\u0133\u0134\b\61\2\2\u0134b\3\2\2\2\u0135"+
		"\u0137\t\t\2\2\u0136\u0135\3\2\2\2\u0137d\3\2\2\2\u0138\u013b\5c\62\2"+
		"\u0139\u013b\5[.\2\u013a\u0138\3\2\2\2\u013a\u0139\3\2\2\2\u013bf\3\2"+
		"\2\2\u013c\u0140\5c\62\2\u013d\u013f\5e\63\2\u013e\u013d\3\2\2\2\u013f"+
		"\u0142\3\2\2\2\u0140\u013e\3\2\2\2\u0140\u0141\3\2\2\2\u0141h\3\2\2\2"+
		"\u0142\u0140\3\2\2\2\u0143\u0146\5]/\2\u0144\u0146\5U+\2\u0145\u0143\3"+
		"\2\2\2\u0145\u0144\3\2\2\2\u0146j\3\2\2\2\u0147\u014a\5\33\16\2\u0148"+
		"\u014a\5\35\17\2\u0149\u0147\3\2\2\2\u0149\u0148\3\2\2\2\u014al\3\2\2"+
		"\2\21\2\u00f3\u0100\u0106\u010b\u010d\u0116\u011e\u0127\u0131\u0136\u013a"+
		"\u0140\u0145\u0149\3\b\2\2";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}