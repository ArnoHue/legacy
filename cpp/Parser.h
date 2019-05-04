#ifndef PARSER
#define PARSER

#include <stdlib.h>

namespace Parser_Space {

	const int kind_number = 0;
	const int kind_open_brace = 1;
	const int kind_close_brace = 2;
	const int kind_add = 3;
	const int kind_sub = 4;
	const int kind_mul = 5;
	const int kind_div = 6;
	const int kind_whitespace = 7;
	const int kind_end = 8;
	const int kind_unknown = 9;

	class Token {
		public:
			Token(char ch);
			int kind;
			double value;
	};


	const int excpt_out_of_memory = 0;
	const int excpt_parsing = 1;
	const int excpt_unknown = 2;

	class Parser {
		public:
			Parser(char dataParam[]);
			~Parser();
			double getExprValue();
		protected:
			double getTermValue();
			double getFactorValue();
			double getNumberValue();
			Token getLookaheadToken();
		private:
			char* data;
			int parsePos;
	};

}

#endif