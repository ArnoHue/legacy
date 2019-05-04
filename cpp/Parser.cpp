#include <stdlib.h>
#include <string.h>
#include <iostream.h>
#include "Parser.h"

namespace Parser_Space {

	Token::Token(char ch) {
		value = 0.0;
		switch(ch) {
			case '(':
				kind = kind_open_brace;
				break;
			case ')':
				kind = kind_close_brace;
				break;
			case '+':
				kind = kind_add;
				break;
			case '-':
				kind = kind_sub;
				break;
			case '*':
				kind = kind_mul;
				break;
			case '/':
				kind = kind_div;
				break;
			case ' ':
				kind = kind_whitespace;
				break;
			case '\0':
				kind = kind_end;
				break;
			default:
				if (ch >= '0' && ch <= '9') {
					kind = kind_number;
				}
				else {
					kind = kind_unknown;
				}
				break;
		}
	}


	Parser::Parser(char* dataParam) {
		data = dataParam;
		parsePos = 0;
	}

	Parser::~Parser() {
		delete[] data;
	}

	Token Parser::getLookaheadToken() {
		Token token = Token(data[parsePos]);
		while (token.kind == kind_whitespace) {
			parsePos++;
			token = Token(data[parsePos]);
		}
		if (token.kind == kind_unknown) {
			throw excpt_parsing;
		}
		return token;
	}

	double Parser::getNumberValue() {
		int len;
		char* number;
		double value = 0.0;
		for (len = 0; (data[parsePos + len] >= '0' && data[parsePos + len] <= '9') || data[parsePos + len] == '.'; len++);
		if (len > 0) {
			number = new char[len + 1];
			if (number != NULL) {
				memcpy(number, data + parsePos, len);
				number[len] = '\0';
				value = atof(number);
			}
			else {
				throw excpt_out_of_memory;
			}
			delete[] number;
			parsePos += len;
		}
		else {
			throw excpt_parsing;
		}
		return value;
	}

	double Parser::getExprValue() {
		double val;
		Token token = getLookaheadToken();
		if (token.kind == kind_sub) {
			parsePos++;
			val = -getTermValue();
		}
		else {
			val = getTermValue();
		}
		token = getLookaheadToken();
		while (token.kind == kind_add || token.kind == kind_sub) {
			parsePos++;
			if (token.kind == kind_add) {
				val += getTermValue();
			}
			else {
				val -= getTermValue();
			}
			token = getLookaheadToken();
		}
		if (token.kind != kind_end && token.kind != kind_close_brace) {
			throw excpt_parsing;
		}
		return val;
	}

	double Parser::getTermValue() {
		double val = getFactorValue();
		Token token = getLookaheadToken();
		while (token.kind == kind_mul || token.kind == kind_div) {
			parsePos++;
			if (token.kind == kind_mul) {
				val *= getFactorValue();
			}
			else {
				val /= getFactorValue();
			}
			token = getLookaheadToken();
		}
		return val;
	}

	double Parser::getFactorValue() {
		double val = 0.0;
		Token token = getLookaheadToken();
		if (token.kind == kind_number) {
			val = getNumberValue();
		}
		else if (token.kind == kind_open_brace) {
			parsePos++;
			val = getExprValue();
			token = getLookaheadToken();
			if (token.kind != kind_close_brace) {
				throw excpt_parsing;
			}
			parsePos++;
		}
		else {
			throw excpt_parsing;
		}
		return val;
	}

}
