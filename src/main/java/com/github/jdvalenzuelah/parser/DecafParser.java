// Generated from Decaf.g4 by ANTLR 4.5
package com.github.jdvalenzuelah.parser;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.misc.*;
import org.antlr.v4.runtime.tree.*;
import java.util.List;
import java.util.Iterator;
import java.util.ArrayList;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class DecafParser extends Parser {
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
	public static final int
		RULE_program = 0, RULE_field_name = 1, RULE_field_decl = 2, RULE_method_decl = 3, 
		RULE_block = 4, RULE_var_decl = 5, RULE_datatype = 6, RULE_statement = 7, 
		RULE_assign_op = 8, RULE_method_call = 9, RULE_method_name = 10, RULE_location = 11, 
		RULE_expr = 12, RULE_callout_arg = 13, RULE_bin_op = 14, RULE_arith_op = 15, 
		RULE_rel_op = 16, RULE_eq_op = 17, RULE_cond_op = 18, RULE_literal = 19;
	public static final String[] ruleNames = {
		"program", "field_name", "field_decl", "method_decl", "block", "var_decl", 
		"datatype", "statement", "assign_op", "method_call", "method_name", "location", 
		"expr", "callout_arg", "bin_op", "arith_op", "rel_op", "eq_op", "cond_op", 
		"literal"
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

	@Override
	public String getGrammarFileName() { return "Decaf.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public ATN getATN() { return _ATN; }

	public DecafParser(TokenStream input) {
		super(input);
		_interp = new ParserATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}
	public static class ProgramContext extends ParserRuleContext {
		public TerminalNode CLASS() { return getToken(DecafParser.CLASS, 0); }
		public TerminalNode ID() { return getToken(DecafParser.ID, 0); }
		public TerminalNode LCURLY() { return getToken(DecafParser.LCURLY, 0); }
		public TerminalNode RCURLY() { return getToken(DecafParser.RCURLY, 0); }
		public TerminalNode EOF() { return getToken(DecafParser.EOF, 0); }
		public List<Field_declContext> field_decl() {
			return getRuleContexts(Field_declContext.class);
		}
		public Field_declContext field_decl(int i) {
			return getRuleContext(Field_declContext.class,i);
		}
		public List<Method_declContext> method_decl() {
			return getRuleContexts(Method_declContext.class);
		}
		public Method_declContext method_decl(int i) {
			return getRuleContext(Method_declContext.class,i);
		}
		public ProgramContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_program; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof DecafListener ) ((DecafListener)listener).enterProgram(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof DecafListener ) ((DecafListener)listener).exitProgram(this);
		}
	}

	public final ProgramContext program() throws RecognitionException {
		ProgramContext _localctx = new ProgramContext(_ctx, getState());
		enterRule(_localctx, 0, RULE_program);
		int _la;
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(40);
			match(CLASS);
			setState(41);
			match(ID);
			setState(42);
			match(LCURLY);
			setState(46);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,0,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					{
					{
					setState(43);
					field_decl();
					}
					} 
				}
				setState(48);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,0,_ctx);
			}
			setState(52);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << INT) | (1L << VOID) | (1L << BOOLEAN))) != 0)) {
				{
				{
				setState(49);
				method_decl();
				}
				}
				setState(54);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(55);
			match(RCURLY);
			setState(56);
			match(EOF);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Field_nameContext extends ParserRuleContext {
		public TerminalNode ID() { return getToken(DecafParser.ID, 0); }
		public TerminalNode LSQUARE() { return getToken(DecafParser.LSQUARE, 0); }
		public TerminalNode INT_LITERAL() { return getToken(DecafParser.INT_LITERAL, 0); }
		public TerminalNode RSQUARE() { return getToken(DecafParser.RSQUARE, 0); }
		public Field_nameContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_field_name; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof DecafListener ) ((DecafListener)listener).enterField_name(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof DecafListener ) ((DecafListener)listener).exitField_name(this);
		}
	}

	public final Field_nameContext field_name() throws RecognitionException {
		Field_nameContext _localctx = new Field_nameContext(_ctx, getState());
		enterRule(_localctx, 2, RULE_field_name);
		try {
			setState(63);
			switch ( getInterpreter().adaptivePredict(_input,2,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(58);
				match(ID);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(59);
				match(ID);
				setState(60);
				match(LSQUARE);
				setState(61);
				match(INT_LITERAL);
				setState(62);
				match(RSQUARE);
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Field_declContext extends ParserRuleContext {
		public DatatypeContext datatype() {
			return getRuleContext(DatatypeContext.class,0);
		}
		public List<Field_nameContext> field_name() {
			return getRuleContexts(Field_nameContext.class);
		}
		public Field_nameContext field_name(int i) {
			return getRuleContext(Field_nameContext.class,i);
		}
		public TerminalNode SEMI() { return getToken(DecafParser.SEMI, 0); }
		public List<TerminalNode> COMMA() { return getTokens(DecafParser.COMMA); }
		public TerminalNode COMMA(int i) {
			return getToken(DecafParser.COMMA, i);
		}
		public Field_declContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_field_decl; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof DecafListener ) ((DecafListener)listener).enterField_decl(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof DecafListener ) ((DecafListener)listener).exitField_decl(this);
		}
	}

	public final Field_declContext field_decl() throws RecognitionException {
		Field_declContext _localctx = new Field_declContext(_ctx, getState());
		enterRule(_localctx, 4, RULE_field_decl);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(65);
			datatype();
			setState(66);
			field_name();
			setState(71);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==COMMA) {
				{
				{
				setState(67);
				match(COMMA);
				setState(68);
				field_name();
				}
				}
				setState(73);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(74);
			match(SEMI);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Method_declContext extends ParserRuleContext {
		public List<TerminalNode> ID() { return getTokens(DecafParser.ID); }
		public TerminalNode ID(int i) {
			return getToken(DecafParser.ID, i);
		}
		public TerminalNode LBRACE() { return getToken(DecafParser.LBRACE, 0); }
		public TerminalNode RBRACE() { return getToken(DecafParser.RBRACE, 0); }
		public BlockContext block() {
			return getRuleContext(BlockContext.class,0);
		}
		public List<DatatypeContext> datatype() {
			return getRuleContexts(DatatypeContext.class);
		}
		public DatatypeContext datatype(int i) {
			return getRuleContext(DatatypeContext.class,i);
		}
		public TerminalNode VOID() { return getToken(DecafParser.VOID, 0); }
		public List<TerminalNode> COMMA() { return getTokens(DecafParser.COMMA); }
		public TerminalNode COMMA(int i) {
			return getToken(DecafParser.COMMA, i);
		}
		public Method_declContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_method_decl; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof DecafListener ) ((DecafListener)listener).enterMethod_decl(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof DecafListener ) ((DecafListener)listener).exitMethod_decl(this);
		}
	}

	public final Method_declContext method_decl() throws RecognitionException {
		Method_declContext _localctx = new Method_declContext(_ctx, getState());
		enterRule(_localctx, 6, RULE_method_decl);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(78);
			switch (_input.LA(1)) {
			case INT:
			case BOOLEAN:
				{
				setState(76);
				datatype();
				}
				break;
			case VOID:
				{
				setState(77);
				match(VOID);
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
			setState(80);
			match(ID);
			setState(81);
			match(LBRACE);
			setState(94);
			_la = _input.LA(1);
			if (_la==INT || _la==BOOLEAN) {
				{
				{
				setState(82);
				datatype();
				setState(83);
				match(ID);
				}
				setState(91);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==COMMA) {
					{
					{
					setState(85);
					match(COMMA);
					setState(86);
					datatype();
					setState(87);
					match(ID);
					}
					}
					setState(93);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				}
			}

			setState(96);
			match(RBRACE);
			setState(97);
			block();
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class BlockContext extends ParserRuleContext {
		public TerminalNode LCURLY() { return getToken(DecafParser.LCURLY, 0); }
		public TerminalNode RCURLY() { return getToken(DecafParser.RCURLY, 0); }
		public List<Var_declContext> var_decl() {
			return getRuleContexts(Var_declContext.class);
		}
		public Var_declContext var_decl(int i) {
			return getRuleContext(Var_declContext.class,i);
		}
		public List<StatementContext> statement() {
			return getRuleContexts(StatementContext.class);
		}
		public StatementContext statement(int i) {
			return getRuleContext(StatementContext.class,i);
		}
		public BlockContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_block; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof DecafListener ) ((DecafListener)listener).enterBlock(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof DecafListener ) ((DecafListener)listener).exitBlock(this);
		}
	}

	public final BlockContext block() throws RecognitionException {
		BlockContext _localctx = new BlockContext(_ctx, getState());
		enterRule(_localctx, 8, RULE_block);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(99);
			match(LCURLY);
			setState(103);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==INT || _la==BOOLEAN) {
				{
				{
				setState(100);
				var_decl();
				}
				}
				setState(105);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(109);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << RETURN) | (1L << IF) | (1L << FOR) | (1L << BREAK) | (1L << CONTINUE) | (1L << CALLOUT) | (1L << LCURLY) | (1L << ID))) != 0)) {
				{
				{
				setState(106);
				statement();
				}
				}
				setState(111);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(112);
			match(RCURLY);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Var_declContext extends ParserRuleContext {
		public DatatypeContext datatype() {
			return getRuleContext(DatatypeContext.class,0);
		}
		public List<TerminalNode> ID() { return getTokens(DecafParser.ID); }
		public TerminalNode ID(int i) {
			return getToken(DecafParser.ID, i);
		}
		public TerminalNode SEMI() { return getToken(DecafParser.SEMI, 0); }
		public List<TerminalNode> COMMA() { return getTokens(DecafParser.COMMA); }
		public TerminalNode COMMA(int i) {
			return getToken(DecafParser.COMMA, i);
		}
		public Var_declContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_var_decl; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof DecafListener ) ((DecafListener)listener).enterVar_decl(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof DecafListener ) ((DecafListener)listener).exitVar_decl(this);
		}
	}

	public final Var_declContext var_decl() throws RecognitionException {
		Var_declContext _localctx = new Var_declContext(_ctx, getState());
		enterRule(_localctx, 10, RULE_var_decl);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(114);
			datatype();
			setState(115);
			match(ID);
			setState(120);
			_errHandler.sync(this);
			_la = _input.LA(1);
			while (_la==COMMA) {
				{
				{
				setState(116);
				match(COMMA);
				setState(117);
				match(ID);
				}
				}
				setState(122);
				_errHandler.sync(this);
				_la = _input.LA(1);
			}
			setState(123);
			match(SEMI);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class DatatypeContext extends ParserRuleContext {
		public TerminalNode INT() { return getToken(DecafParser.INT, 0); }
		public TerminalNode BOOLEAN() { return getToken(DecafParser.BOOLEAN, 0); }
		public DatatypeContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_datatype; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof DecafListener ) ((DecafListener)listener).enterDatatype(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof DecafListener ) ((DecafListener)listener).exitDatatype(this);
		}
	}

	public final DatatypeContext datatype() throws RecognitionException {
		DatatypeContext _localctx = new DatatypeContext(_ctx, getState());
		enterRule(_localctx, 12, RULE_datatype);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(125);
			_la = _input.LA(1);
			if ( !(_la==INT || _la==BOOLEAN) ) {
			_errHandler.recoverInline(this);
			} else {
				consume();
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class StatementContext extends ParserRuleContext {
		public LocationContext location() {
			return getRuleContext(LocationContext.class,0);
		}
		public Assign_opContext assign_op() {
			return getRuleContext(Assign_opContext.class,0);
		}
		public List<ExprContext> expr() {
			return getRuleContexts(ExprContext.class);
		}
		public ExprContext expr(int i) {
			return getRuleContext(ExprContext.class,i);
		}
		public TerminalNode SEMI() { return getToken(DecafParser.SEMI, 0); }
		public Method_callContext method_call() {
			return getRuleContext(Method_callContext.class,0);
		}
		public TerminalNode IF() { return getToken(DecafParser.IF, 0); }
		public TerminalNode LBRACE() { return getToken(DecafParser.LBRACE, 0); }
		public TerminalNode RBRACE() { return getToken(DecafParser.RBRACE, 0); }
		public List<BlockContext> block() {
			return getRuleContexts(BlockContext.class);
		}
		public BlockContext block(int i) {
			return getRuleContext(BlockContext.class,i);
		}
		public TerminalNode ELSE() { return getToken(DecafParser.ELSE, 0); }
		public TerminalNode FOR() { return getToken(DecafParser.FOR, 0); }
		public TerminalNode ID() { return getToken(DecafParser.ID, 0); }
		public TerminalNode EQ() { return getToken(DecafParser.EQ, 0); }
		public TerminalNode COMMA() { return getToken(DecafParser.COMMA, 0); }
		public TerminalNode RETURN() { return getToken(DecafParser.RETURN, 0); }
		public TerminalNode BREAK() { return getToken(DecafParser.BREAK, 0); }
		public TerminalNode CONTINUE() { return getToken(DecafParser.CONTINUE, 0); }
		public StatementContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_statement; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof DecafListener ) ((DecafListener)listener).enterStatement(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof DecafListener ) ((DecafListener)listener).exitStatement(this);
		}
	}

	public final StatementContext statement() throws RecognitionException {
		StatementContext _localctx = new StatementContext(_ctx, getState());
		enterRule(_localctx, 14, RULE_statement);
		int _la;
		try {
			setState(162);
			switch ( getInterpreter().adaptivePredict(_input,12,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(127);
				location();
				setState(128);
				assign_op();
				setState(129);
				expr(0);
				setState(130);
				match(SEMI);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(132);
				method_call();
				setState(133);
				match(SEMI);
				}
				break;
			case 3:
				enterOuterAlt(_localctx, 3);
				{
				setState(135);
				match(IF);
				setState(136);
				match(LBRACE);
				setState(137);
				expr(0);
				setState(138);
				match(RBRACE);
				setState(139);
				block();
				setState(142);
				_la = _input.LA(1);
				if (_la==ELSE) {
					{
					setState(140);
					match(ELSE);
					setState(141);
					block();
					}
				}

				}
				break;
			case 4:
				enterOuterAlt(_localctx, 4);
				{
				setState(144);
				match(FOR);
				setState(145);
				match(ID);
				setState(146);
				match(EQ);
				setState(147);
				expr(0);
				setState(148);
				match(COMMA);
				setState(149);
				expr(0);
				setState(150);
				block();
				}
				break;
			case 5:
				enterOuterAlt(_localctx, 5);
				{
				setState(152);
				match(RETURN);
				setState(154);
				_la = _input.LA(1);
				if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << CALLOUT) | (1L << LBRACE) | (1L << SUB) | (1L << EXCLAMATION) | (1L << CHAR_LITERAL) | (1L << ID) | (1L << INT_LITERAL) | (1L << BOOL_LITERAL))) != 0)) {
					{
					setState(153);
					expr(0);
					}
				}

				setState(156);
				match(SEMI);
				}
				break;
			case 6:
				enterOuterAlt(_localctx, 6);
				{
				setState(157);
				match(BREAK);
				setState(158);
				match(SEMI);
				}
				break;
			case 7:
				enterOuterAlt(_localctx, 7);
				{
				setState(159);
				match(CONTINUE);
				setState(160);
				match(SEMI);
				}
				break;
			case 8:
				enterOuterAlt(_localctx, 8);
				{
				setState(161);
				block();
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Assign_opContext extends ParserRuleContext {
		public TerminalNode EQ() { return getToken(DecafParser.EQ, 0); }
		public TerminalNode ADD() { return getToken(DecafParser.ADD, 0); }
		public TerminalNode SUB() { return getToken(DecafParser.SUB, 0); }
		public Assign_opContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_assign_op; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof DecafListener ) ((DecafListener)listener).enterAssign_op(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof DecafListener ) ((DecafListener)listener).exitAssign_op(this);
		}
	}

	public final Assign_opContext assign_op() throws RecognitionException {
		Assign_opContext _localctx = new Assign_opContext(_ctx, getState());
		enterRule(_localctx, 16, RULE_assign_op);
		try {
			setState(169);
			switch (_input.LA(1)) {
			case EQ:
				enterOuterAlt(_localctx, 1);
				{
				setState(164);
				match(EQ);
				}
				break;
			case ADD:
				enterOuterAlt(_localctx, 2);
				{
				setState(165);
				match(ADD);
				setState(166);
				match(EQ);
				}
				break;
			case SUB:
				enterOuterAlt(_localctx, 3);
				{
				setState(167);
				match(SUB);
				setState(168);
				match(EQ);
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Method_callContext extends ParserRuleContext {
		public Method_nameContext method_name() {
			return getRuleContext(Method_nameContext.class,0);
		}
		public TerminalNode LBRACE() { return getToken(DecafParser.LBRACE, 0); }
		public TerminalNode RBRACE() { return getToken(DecafParser.RBRACE, 0); }
		public List<ExprContext> expr() {
			return getRuleContexts(ExprContext.class);
		}
		public ExprContext expr(int i) {
			return getRuleContext(ExprContext.class,i);
		}
		public List<TerminalNode> COMMA() { return getTokens(DecafParser.COMMA); }
		public TerminalNode COMMA(int i) {
			return getToken(DecafParser.COMMA, i);
		}
		public TerminalNode CALLOUT() { return getToken(DecafParser.CALLOUT, 0); }
		public TerminalNode STRING() { return getToken(DecafParser.STRING, 0); }
		public List<Callout_argContext> callout_arg() {
			return getRuleContexts(Callout_argContext.class);
		}
		public Callout_argContext callout_arg(int i) {
			return getRuleContext(Callout_argContext.class,i);
		}
		public Method_callContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_method_call; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof DecafListener ) ((DecafListener)listener).enterMethod_call(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof DecafListener ) ((DecafListener)listener).exitMethod_call(this);
		}
	}

	public final Method_callContext method_call() throws RecognitionException {
		Method_callContext _localctx = new Method_callContext(_ctx, getState());
		enterRule(_localctx, 18, RULE_method_call);
		int _la;
		try {
			setState(199);
			switch (_input.LA(1)) {
			case ID:
				enterOuterAlt(_localctx, 1);
				{
				setState(171);
				method_name();
				setState(172);
				match(LBRACE);
				setState(181);
				_la = _input.LA(1);
				if ((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << CALLOUT) | (1L << LBRACE) | (1L << SUB) | (1L << EXCLAMATION) | (1L << CHAR_LITERAL) | (1L << ID) | (1L << INT_LITERAL) | (1L << BOOL_LITERAL))) != 0)) {
					{
					setState(173);
					expr(0);
					setState(178);
					_errHandler.sync(this);
					_la = _input.LA(1);
					while (_la==COMMA) {
						{
						{
						setState(174);
						match(COMMA);
						setState(175);
						expr(0);
						}
						}
						setState(180);
						_errHandler.sync(this);
						_la = _input.LA(1);
					}
					}
				}

				setState(183);
				match(RBRACE);
				}
				break;
			case CALLOUT:
				enterOuterAlt(_localctx, 2);
				{
				setState(185);
				match(CALLOUT);
				setState(186);
				match(LBRACE);
				setState(187);
				match(STRING);
				{
				setState(188);
				match(COMMA);
				setState(189);
				callout_arg();
				setState(194);
				_errHandler.sync(this);
				_la = _input.LA(1);
				while (_la==COMMA) {
					{
					{
					setState(190);
					match(COMMA);
					setState(191);
					callout_arg();
					}
					}
					setState(196);
					_errHandler.sync(this);
					_la = _input.LA(1);
				}
				}
				setState(197);
				match(RBRACE);
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Method_nameContext extends ParserRuleContext {
		public TerminalNode ID() { return getToken(DecafParser.ID, 0); }
		public Method_nameContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_method_name; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof DecafListener ) ((DecafListener)listener).enterMethod_name(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof DecafListener ) ((DecafListener)listener).exitMethod_name(this);
		}
	}

	public final Method_nameContext method_name() throws RecognitionException {
		Method_nameContext _localctx = new Method_nameContext(_ctx, getState());
		enterRule(_localctx, 20, RULE_method_name);
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(201);
			match(ID);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class LocationContext extends ParserRuleContext {
		public TerminalNode ID() { return getToken(DecafParser.ID, 0); }
		public TerminalNode LSQUARE() { return getToken(DecafParser.LSQUARE, 0); }
		public ExprContext expr() {
			return getRuleContext(ExprContext.class,0);
		}
		public TerminalNode RSQUARE() { return getToken(DecafParser.RSQUARE, 0); }
		public LocationContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_location; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof DecafListener ) ((DecafListener)listener).enterLocation(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof DecafListener ) ((DecafListener)listener).exitLocation(this);
		}
	}

	public final LocationContext location() throws RecognitionException {
		LocationContext _localctx = new LocationContext(_ctx, getState());
		enterRule(_localctx, 22, RULE_location);
		try {
			setState(209);
			switch ( getInterpreter().adaptivePredict(_input,18,_ctx) ) {
			case 1:
				enterOuterAlt(_localctx, 1);
				{
				setState(203);
				match(ID);
				}
				break;
			case 2:
				enterOuterAlt(_localctx, 2);
				{
				setState(204);
				match(ID);
				setState(205);
				match(LSQUARE);
				setState(206);
				expr(0);
				setState(207);
				match(RSQUARE);
				}
				break;
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class ExprContext extends ParserRuleContext {
		public TerminalNode SUB() { return getToken(DecafParser.SUB, 0); }
		public List<ExprContext> expr() {
			return getRuleContexts(ExprContext.class);
		}
		public ExprContext expr(int i) {
			return getRuleContext(ExprContext.class,i);
		}
		public TerminalNode EXCLAMATION() { return getToken(DecafParser.EXCLAMATION, 0); }
		public LocationContext location() {
			return getRuleContext(LocationContext.class,0);
		}
		public Method_callContext method_call() {
			return getRuleContext(Method_callContext.class,0);
		}
		public LiteralContext literal() {
			return getRuleContext(LiteralContext.class,0);
		}
		public TerminalNode LBRACE() { return getToken(DecafParser.LBRACE, 0); }
		public TerminalNode RBRACE() { return getToken(DecafParser.RBRACE, 0); }
		public Bin_opContext bin_op() {
			return getRuleContext(Bin_opContext.class,0);
		}
		public ExprContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_expr; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof DecafListener ) ((DecafListener)listener).enterExpr(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof DecafListener ) ((DecafListener)listener).exitExpr(this);
		}
	}

	public final ExprContext expr() throws RecognitionException {
		return expr(0);
	}

	private ExprContext expr(int _p) throws RecognitionException {
		ParserRuleContext _parentctx = _ctx;
		int _parentState = getState();
		ExprContext _localctx = new ExprContext(_ctx, _parentState);
		ExprContext _prevctx = _localctx;
		int _startState = 24;
		enterRecursionRule(_localctx, 24, RULE_expr, _p);
		try {
			int _alt;
			enterOuterAlt(_localctx, 1);
			{
			setState(223);
			switch ( getInterpreter().adaptivePredict(_input,19,_ctx) ) {
			case 1:
				{
				setState(212);
				match(SUB);
				setState(213);
				expr(3);
				}
				break;
			case 2:
				{
				setState(214);
				match(EXCLAMATION);
				setState(215);
				expr(2);
				}
				break;
			case 3:
				{
				setState(216);
				location();
				}
				break;
			case 4:
				{
				setState(217);
				method_call();
				}
				break;
			case 5:
				{
				setState(218);
				literal();
				}
				break;
			case 6:
				{
				setState(219);
				match(LBRACE);
				setState(220);
				expr(0);
				setState(221);
				match(RBRACE);
				}
				break;
			}
			_ctx.stop = _input.LT(-1);
			setState(231);
			_errHandler.sync(this);
			_alt = getInterpreter().adaptivePredict(_input,20,_ctx);
			while ( _alt!=2 && _alt!=org.antlr.v4.runtime.atn.ATN.INVALID_ALT_NUMBER ) {
				if ( _alt==1 ) {
					if ( _parseListeners!=null ) triggerExitRuleEvent();
					_prevctx = _localctx;
					{
					{
					_localctx = new ExprContext(_parentctx, _parentState);
					pushNewRecursionContext(_localctx, _startState, RULE_expr);
					setState(225);
					if (!(precpred(_ctx, 4))) throw new FailedPredicateException(this, "precpred(_ctx, 4)");
					setState(226);
					bin_op();
					setState(227);
					expr(5);
					}
					} 
				}
				setState(233);
				_errHandler.sync(this);
				_alt = getInterpreter().adaptivePredict(_input,20,_ctx);
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			unrollRecursionContexts(_parentctx);
		}
		return _localctx;
	}

	public static class Callout_argContext extends ParserRuleContext {
		public ExprContext expr() {
			return getRuleContext(ExprContext.class,0);
		}
		public TerminalNode STRING() { return getToken(DecafParser.STRING, 0); }
		public Callout_argContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_callout_arg; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof DecafListener ) ((DecafListener)listener).enterCallout_arg(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof DecafListener ) ((DecafListener)listener).exitCallout_arg(this);
		}
	}

	public final Callout_argContext callout_arg() throws RecognitionException {
		Callout_argContext _localctx = new Callout_argContext(_ctx, getState());
		enterRule(_localctx, 26, RULE_callout_arg);
		try {
			setState(236);
			switch (_input.LA(1)) {
			case CALLOUT:
			case LBRACE:
			case SUB:
			case EXCLAMATION:
			case CHAR_LITERAL:
			case ID:
			case INT_LITERAL:
			case BOOL_LITERAL:
				enterOuterAlt(_localctx, 1);
				{
				setState(234);
				expr(0);
				}
				break;
			case STRING:
				enterOuterAlt(_localctx, 2);
				{
				setState(235);
				match(STRING);
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Bin_opContext extends ParserRuleContext {
		public Arith_opContext arith_op() {
			return getRuleContext(Arith_opContext.class,0);
		}
		public Rel_opContext rel_op() {
			return getRuleContext(Rel_opContext.class,0);
		}
		public Eq_opContext eq_op() {
			return getRuleContext(Eq_opContext.class,0);
		}
		public Cond_opContext cond_op() {
			return getRuleContext(Cond_opContext.class,0);
		}
		public Bin_opContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_bin_op; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof DecafListener ) ((DecafListener)listener).enterBin_op(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof DecafListener ) ((DecafListener)listener).exitBin_op(this);
		}
	}

	public final Bin_opContext bin_op() throws RecognitionException {
		Bin_opContext _localctx = new Bin_opContext(_ctx, getState());
		enterRule(_localctx, 28, RULE_bin_op);
		try {
			setState(242);
			switch (_input.LA(1)) {
			case T__0:
			case ADD:
			case SUB:
			case MUL:
			case DIV:
				enterOuterAlt(_localctx, 1);
				{
				setState(238);
				arith_op();
				}
				break;
			case LESS:
			case GREATER:
			case LESSEQUAL:
			case GREATEREQUAL:
				enterOuterAlt(_localctx, 2);
				{
				setState(239);
				rel_op();
				}
				break;
			case EQUALTO:
			case NOTEQUAL:
				enterOuterAlt(_localctx, 3);
				{
				setState(240);
				eq_op();
				}
				break;
			case T__1:
			case AND:
				enterOuterAlt(_localctx, 4);
				{
				setState(241);
				cond_op();
				}
				break;
			default:
				throw new NoViableAltException(this);
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Arith_opContext extends ParserRuleContext {
		public TerminalNode ADD() { return getToken(DecafParser.ADD, 0); }
		public TerminalNode SUB() { return getToken(DecafParser.SUB, 0); }
		public TerminalNode MUL() { return getToken(DecafParser.MUL, 0); }
		public TerminalNode DIV() { return getToken(DecafParser.DIV, 0); }
		public Arith_opContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_arith_op; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof DecafListener ) ((DecafListener)listener).enterArith_op(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof DecafListener ) ((DecafListener)listener).exitArith_op(this);
		}
	}

	public final Arith_opContext arith_op() throws RecognitionException {
		Arith_opContext _localctx = new Arith_opContext(_ctx, getState());
		enterRule(_localctx, 30, RULE_arith_op);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(244);
			_la = _input.LA(1);
			if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << T__0) | (1L << ADD) | (1L << SUB) | (1L << MUL) | (1L << DIV))) != 0)) ) {
			_errHandler.recoverInline(this);
			} else {
				consume();
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Rel_opContext extends ParserRuleContext {
		public TerminalNode LESS() { return getToken(DecafParser.LESS, 0); }
		public TerminalNode GREATER() { return getToken(DecafParser.GREATER, 0); }
		public TerminalNode LESSEQUAL() { return getToken(DecafParser.LESSEQUAL, 0); }
		public TerminalNode GREATEREQUAL() { return getToken(DecafParser.GREATEREQUAL, 0); }
		public Rel_opContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_rel_op; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof DecafListener ) ((DecafListener)listener).enterRel_op(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof DecafListener ) ((DecafListener)listener).exitRel_op(this);
		}
	}

	public final Rel_opContext rel_op() throws RecognitionException {
		Rel_opContext _localctx = new Rel_opContext(_ctx, getState());
		enterRule(_localctx, 32, RULE_rel_op);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(246);
			_la = _input.LA(1);
			if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << LESS) | (1L << GREATER) | (1L << LESSEQUAL) | (1L << GREATEREQUAL))) != 0)) ) {
			_errHandler.recoverInline(this);
			} else {
				consume();
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Eq_opContext extends ParserRuleContext {
		public TerminalNode EQUALTO() { return getToken(DecafParser.EQUALTO, 0); }
		public TerminalNode NOTEQUAL() { return getToken(DecafParser.NOTEQUAL, 0); }
		public Eq_opContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_eq_op; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof DecafListener ) ((DecafListener)listener).enterEq_op(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof DecafListener ) ((DecafListener)listener).exitEq_op(this);
		}
	}

	public final Eq_opContext eq_op() throws RecognitionException {
		Eq_opContext _localctx = new Eq_opContext(_ctx, getState());
		enterRule(_localctx, 34, RULE_eq_op);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(248);
			_la = _input.LA(1);
			if ( !(_la==EQUALTO || _la==NOTEQUAL) ) {
			_errHandler.recoverInline(this);
			} else {
				consume();
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class Cond_opContext extends ParserRuleContext {
		public TerminalNode AND() { return getToken(DecafParser.AND, 0); }
		public Cond_opContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_cond_op; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof DecafListener ) ((DecafListener)listener).enterCond_op(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof DecafListener ) ((DecafListener)listener).exitCond_op(this);
		}
	}

	public final Cond_opContext cond_op() throws RecognitionException {
		Cond_opContext _localctx = new Cond_opContext(_ctx, getState());
		enterRule(_localctx, 36, RULE_cond_op);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(250);
			_la = _input.LA(1);
			if ( !(_la==T__1 || _la==AND) ) {
			_errHandler.recoverInline(this);
			} else {
				consume();
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public static class LiteralContext extends ParserRuleContext {
		public TerminalNode INT_LITERAL() { return getToken(DecafParser.INT_LITERAL, 0); }
		public TerminalNode CHAR_LITERAL() { return getToken(DecafParser.CHAR_LITERAL, 0); }
		public TerminalNode BOOL_LITERAL() { return getToken(DecafParser.BOOL_LITERAL, 0); }
		public LiteralContext(ParserRuleContext parent, int invokingState) {
			super(parent, invokingState);
		}
		@Override public int getRuleIndex() { return RULE_literal; }
		@Override
		public void enterRule(ParseTreeListener listener) {
			if ( listener instanceof DecafListener ) ((DecafListener)listener).enterLiteral(this);
		}
		@Override
		public void exitRule(ParseTreeListener listener) {
			if ( listener instanceof DecafListener ) ((DecafListener)listener).exitLiteral(this);
		}
	}

	public final LiteralContext literal() throws RecognitionException {
		LiteralContext _localctx = new LiteralContext(_ctx, getState());
		enterRule(_localctx, 38, RULE_literal);
		int _la;
		try {
			enterOuterAlt(_localctx, 1);
			{
			setState(252);
			_la = _input.LA(1);
			if ( !((((_la) & ~0x3f) == 0 && ((1L << _la) & ((1L << CHAR_LITERAL) | (1L << INT_LITERAL) | (1L << BOOL_LITERAL))) != 0)) ) {
			_errHandler.recoverInline(this);
			} else {
				consume();
			}
			}
		}
		catch (RecognitionException re) {
			_localctx.exception = re;
			_errHandler.reportError(this, re);
			_errHandler.recover(this, re);
		}
		finally {
			exitRule();
		}
		return _localctx;
	}

	public boolean sempred(RuleContext _localctx, int ruleIndex, int predIndex) {
		switch (ruleIndex) {
		case 12:
			return expr_sempred((ExprContext)_localctx, predIndex);
		}
		return true;
	}
	private boolean expr_sempred(ExprContext _localctx, int predIndex) {
		switch (predIndex) {
		case 0:
			return precpred(_ctx, 4);
		}
		return true;
	}

	public static final String _serializedATN =
		"\3\u0430\ud6d1\u8206\uad2d\u4417\uaef1\u8d80\uaadd\3\60\u0101\4\2\t\2"+
		"\4\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\4\13"+
		"\t\13\4\f\t\f\4\r\t\r\4\16\t\16\4\17\t\17\4\20\t\20\4\21\t\21\4\22\t\22"+
		"\4\23\t\23\4\24\t\24\4\25\t\25\3\2\3\2\3\2\3\2\7\2/\n\2\f\2\16\2\62\13"+
		"\2\3\2\7\2\65\n\2\f\2\16\28\13\2\3\2\3\2\3\2\3\3\3\3\3\3\3\3\3\3\5\3B"+
		"\n\3\3\4\3\4\3\4\3\4\7\4H\n\4\f\4\16\4K\13\4\3\4\3\4\3\5\3\5\5\5Q\n\5"+
		"\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5\3\5\7\5\\\n\5\f\5\16\5_\13\5\5\5a\n\5"+
		"\3\5\3\5\3\5\3\6\3\6\7\6h\n\6\f\6\16\6k\13\6\3\6\7\6n\n\6\f\6\16\6q\13"+
		"\6\3\6\3\6\3\7\3\7\3\7\3\7\7\7y\n\7\f\7\16\7|\13\7\3\7\3\7\3\b\3\b\3\t"+
		"\3\t\3\t\3\t\3\t\3\t\3\t\3\t\3\t\3\t\3\t\3\t\3\t\3\t\3\t\5\t\u0091\n\t"+
		"\3\t\3\t\3\t\3\t\3\t\3\t\3\t\3\t\3\t\3\t\5\t\u009d\n\t\3\t\3\t\3\t\3\t"+
		"\3\t\3\t\5\t\u00a5\n\t\3\n\3\n\3\n\3\n\3\n\5\n\u00ac\n\n\3\13\3\13\3\13"+
		"\3\13\3\13\7\13\u00b3\n\13\f\13\16\13\u00b6\13\13\5\13\u00b8\n\13\3\13"+
		"\3\13\3\13\3\13\3\13\3\13\3\13\3\13\3\13\7\13\u00c3\n\13\f\13\16\13\u00c6"+
		"\13\13\3\13\3\13\5\13\u00ca\n\13\3\f\3\f\3\r\3\r\3\r\3\r\3\r\3\r\5\r\u00d4"+
		"\n\r\3\16\3\16\3\16\3\16\3\16\3\16\3\16\3\16\3\16\3\16\3\16\3\16\5\16"+
		"\u00e2\n\16\3\16\3\16\3\16\3\16\7\16\u00e8\n\16\f\16\16\16\u00eb\13\16"+
		"\3\17\3\17\5\17\u00ef\n\17\3\20\3\20\3\20\3\20\5\20\u00f5\n\20\3\21\3"+
		"\21\3\22\3\22\3\23\3\23\3\24\3\24\3\25\3\25\3\25\2\3\32\26\2\4\6\b\n\f"+
		"\16\20\22\24\26\30\32\34\36 \"$&(\2\b\4\2\6\6\21\21\4\2\3\3\30\33\3\2"+
		" #\3\2$%\4\2\4\4\37\37\4\2\'\'/\60\u0110\2*\3\2\2\2\4A\3\2\2\2\6C\3\2"+
		"\2\2\bP\3\2\2\2\ne\3\2\2\2\ft\3\2\2\2\16\177\3\2\2\2\20\u00a4\3\2\2\2"+
		"\22\u00ab\3\2\2\2\24\u00c9\3\2\2\2\26\u00cb\3\2\2\2\30\u00d3\3\2\2\2\32"+
		"\u00e1\3\2\2\2\34\u00ee\3\2\2\2\36\u00f4\3\2\2\2 \u00f6\3\2\2\2\"\u00f8"+
		"\3\2\2\2$\u00fa\3\2\2\2&\u00fc\3\2\2\2(\u00fe\3\2\2\2*+\7\5\2\2+,\7.\2"+
		"\2,\60\7\22\2\2-/\5\6\4\2.-\3\2\2\2/\62\3\2\2\2\60.\3\2\2\2\60\61\3\2"+
		"\2\2\61\66\3\2\2\2\62\60\3\2\2\2\63\65\5\b\5\2\64\63\3\2\2\2\658\3\2\2"+
		"\2\66\64\3\2\2\2\66\67\3\2\2\2\679\3\2\2\28\66\3\2\2\29:\7\23\2\2:;\7"+
		"\2\2\3;\3\3\2\2\2<B\7.\2\2=>\7.\2\2>?\7\26\2\2?@\7/\2\2@B\7\27\2\2A<\3"+
		"\2\2\2A=\3\2\2\2B\5\3\2\2\2CD\5\16\b\2DI\5\4\3\2EF\7\36\2\2FH\5\4\3\2"+
		"GE\3\2\2\2HK\3\2\2\2IG\3\2\2\2IJ\3\2\2\2JL\3\2\2\2KI\3\2\2\2LM\7\35\2"+
		"\2M\7\3\2\2\2NQ\5\16\b\2OQ\7\b\2\2PN\3\2\2\2PO\3\2\2\2QR\3\2\2\2RS\7."+
		"\2\2S`\7\24\2\2TU\5\16\b\2UV\7.\2\2V]\3\2\2\2WX\7\36\2\2XY\5\16\b\2YZ"+
		"\7.\2\2Z\\\3\2\2\2[W\3\2\2\2\\_\3\2\2\2][\3\2\2\2]^\3\2\2\2^a\3\2\2\2"+
		"_]\3\2\2\2`T\3\2\2\2`a\3\2\2\2ab\3\2\2\2bc\7\25\2\2cd\5\n\6\2d\t\3\2\2"+
		"\2ei\7\22\2\2fh\5\f\7\2gf\3\2\2\2hk\3\2\2\2ig\3\2\2\2ij\3\2\2\2jo\3\2"+
		"\2\2ki\3\2\2\2ln\5\20\t\2ml\3\2\2\2nq\3\2\2\2om\3\2\2\2op\3\2\2\2pr\3"+
		"\2\2\2qo\3\2\2\2rs\7\23\2\2s\13\3\2\2\2tu\5\16\b\2uz\7.\2\2vw\7\36\2\2"+
		"wy\7.\2\2xv\3\2\2\2y|\3\2\2\2zx\3\2\2\2z{\3\2\2\2{}\3\2\2\2|z\3\2\2\2"+
		"}~\7\35\2\2~\r\3\2\2\2\177\u0080\t\2\2\2\u0080\17\3\2\2\2\u0081\u0082"+
		"\5\30\r\2\u0082\u0083\5\22\n\2\u0083\u0084\5\32\16\2\u0084\u0085\7\35"+
		"\2\2\u0085\u00a5\3\2\2\2\u0086\u0087\5\24\13\2\u0087\u0088\7\35\2\2\u0088"+
		"\u00a5\3\2\2\2\u0089\u008a\7\t\2\2\u008a\u008b\7\24\2\2\u008b\u008c\5"+
		"\32\16\2\u008c\u008d\7\25\2\2\u008d\u0090\5\n\6\2\u008e\u008f\7\n\2\2"+
		"\u008f\u0091\5\n\6\2\u0090\u008e\3\2\2\2\u0090\u0091\3\2\2\2\u0091\u00a5"+
		"\3\2\2\2\u0092\u0093\7\13\2\2\u0093\u0094\7.\2\2\u0094\u0095\7\34\2\2"+
		"\u0095\u0096\5\32\16\2\u0096\u0097\7\36\2\2\u0097\u0098\5\32\16\2\u0098"+
		"\u0099\5\n\6\2\u0099\u00a5\3\2\2\2\u009a\u009c\7\7\2\2\u009b\u009d\5\32"+
		"\16\2\u009c\u009b\3\2\2\2\u009c\u009d\3\2\2\2\u009d\u009e\3\2\2\2\u009e"+
		"\u00a5\7\35\2\2\u009f\u00a0\7\f\2\2\u00a0\u00a5\7\35\2\2\u00a1\u00a2\7"+
		"\r\2\2\u00a2\u00a5\7\35\2\2\u00a3\u00a5\5\n\6\2\u00a4\u0081\3\2\2\2\u00a4"+
		"\u0086\3\2\2\2\u00a4\u0089\3\2\2\2\u00a4\u0092\3\2\2\2\u00a4\u009a\3\2"+
		"\2\2\u00a4\u009f\3\2\2\2\u00a4\u00a1\3\2\2\2\u00a4\u00a3\3\2\2\2\u00a5"+
		"\21\3\2\2\2\u00a6\u00ac\7\34\2\2\u00a7\u00a8\7\30\2\2\u00a8\u00ac\7\34"+
		"\2\2\u00a9\u00aa\7\31\2\2\u00aa\u00ac\7\34\2\2\u00ab\u00a6\3\2\2\2\u00ab"+
		"\u00a7\3\2\2\2\u00ab\u00a9\3\2\2\2\u00ac\23\3\2\2\2\u00ad\u00ae\5\26\f"+
		"\2\u00ae\u00b7\7\24\2\2\u00af\u00b4\5\32\16\2\u00b0\u00b1\7\36\2\2\u00b1"+
		"\u00b3\5\32\16\2\u00b2\u00b0\3\2\2\2\u00b3\u00b6\3\2\2\2\u00b4\u00b2\3"+
		"\2\2\2\u00b4\u00b5\3\2\2\2\u00b5\u00b8\3\2\2\2\u00b6\u00b4\3\2\2\2\u00b7"+
		"\u00af\3\2\2\2\u00b7\u00b8\3\2\2\2\u00b8\u00b9\3\2\2\2\u00b9\u00ba\7\25"+
		"\2\2\u00ba\u00ca\3\2\2\2\u00bb\u00bc\7\16\2\2\u00bc\u00bd\7\24\2\2\u00bd"+
		"\u00be\7*\2\2\u00be\u00bf\7\36\2\2\u00bf\u00c4\5\34\17\2\u00c0\u00c1\7"+
		"\36\2\2\u00c1\u00c3\5\34\17\2\u00c2\u00c0\3\2\2\2\u00c3\u00c6\3\2\2\2"+
		"\u00c4\u00c2\3\2\2\2\u00c4\u00c5\3\2\2\2\u00c5\u00c7\3\2\2\2\u00c6\u00c4"+
		"\3\2\2\2\u00c7\u00c8\7\25\2\2\u00c8\u00ca\3\2\2\2\u00c9\u00ad\3\2\2\2"+
		"\u00c9\u00bb\3\2\2\2\u00ca\25\3\2\2\2\u00cb\u00cc\7.\2\2\u00cc\27\3\2"+
		"\2\2\u00cd\u00d4\7.\2\2\u00ce\u00cf\7.\2\2\u00cf\u00d0\7\26\2\2\u00d0"+
		"\u00d1\5\32\16\2\u00d1\u00d2\7\27\2\2\u00d2\u00d4\3\2\2\2\u00d3\u00cd"+
		"\3\2\2\2\u00d3\u00ce\3\2\2\2\u00d4\31\3\2\2\2\u00d5\u00d6\b\16\1\2\u00d6"+
		"\u00d7\7\31\2\2\u00d7\u00e2\5\32\16\5\u00d8\u00d9\7&\2\2\u00d9\u00e2\5"+
		"\32\16\4\u00da\u00e2\5\30\r\2\u00db\u00e2\5\24\13\2\u00dc\u00e2\5(\25"+
		"\2\u00dd\u00de\7\24\2\2\u00de\u00df\5\32\16\2\u00df\u00e0\7\25\2\2\u00e0"+
		"\u00e2\3\2\2\2\u00e1\u00d5\3\2\2\2\u00e1\u00d8\3\2\2\2\u00e1\u00da\3\2"+
		"\2\2\u00e1\u00db\3\2\2\2\u00e1\u00dc\3\2\2\2\u00e1\u00dd\3\2\2\2\u00e2"+
		"\u00e9\3\2\2\2\u00e3\u00e4\f\6\2\2\u00e4\u00e5\5\36\20\2\u00e5\u00e6\5"+
		"\32\16\7\u00e6\u00e8\3\2\2\2\u00e7\u00e3\3\2\2\2\u00e8\u00eb\3\2\2\2\u00e9"+
		"\u00e7\3\2\2\2\u00e9\u00ea\3\2\2\2\u00ea\33\3\2\2\2\u00eb\u00e9\3\2\2"+
		"\2\u00ec\u00ef\5\32\16\2\u00ed\u00ef\7*\2\2\u00ee\u00ec\3\2\2\2\u00ee"+
		"\u00ed\3\2\2\2\u00ef\35\3\2\2\2\u00f0\u00f5\5 \21\2\u00f1\u00f5\5\"\22"+
		"\2\u00f2\u00f5\5$\23\2\u00f3\u00f5\5&\24\2\u00f4\u00f0\3\2\2\2\u00f4\u00f1"+
		"\3\2\2\2\u00f4\u00f2\3\2\2\2\u00f4\u00f3\3\2\2\2\u00f5\37\3\2\2\2\u00f6"+
		"\u00f7\t\3\2\2\u00f7!\3\2\2\2\u00f8\u00f9\t\4\2\2\u00f9#\3\2\2\2\u00fa"+
		"\u00fb\t\5\2\2\u00fb%\3\2\2\2\u00fc\u00fd\t\6\2\2\u00fd\'\3\2\2\2\u00fe"+
		"\u00ff\t\7\2\2\u00ff)\3\2\2\2\31\60\66AIP]`ioz\u0090\u009c\u00a4\u00ab"+
		"\u00b4\u00b7\u00c4\u00c9\u00d3\u00e1\u00e9\u00ee\u00f4";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}