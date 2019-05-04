#include <iostream.h>
#include <string.h>
#include "Parser.h"

void parse(char* text) {
	Parser_Space::Parser* parser;
	try {
		parser = new Parser_Space::Parser(text);
		cout << text << " = " << parser->getExprValue() << endl;
	}
	catch (int excpt) {
		switch (excpt) {
			case Parser_Space::excpt_out_of_memory:
				cout << "OutOfMemory Exception" << endl;
				break;
			case Parser_Space::excpt_parsing:
				cout << "Parsing Exception" << endl;
				break;
			default:
				cout << "Unknown Exception" << endl;
				break;
		}
	}
	if (parser != NULL) {
		delete parser;
	}
}

int main(void) {
	char input[256];
	int done;

	parse("-3 * 6 + 4");
	parse("(2 + 2) * (3.4 + 3)");
	parse("(2 * (3 + 3 * (4 + 8.286))) - 6 * (-1.2)");
	parse("(2 / (3 + 3 / (4 + 8.286))) - 6 / (-1.2)");

	do {

		cout << "Term eingeben oder <e> fuer exit (bitte keine Blanks): ";
		cin >> input;
		done = strlen(input) == 0 || input[0] == 'e';
		if (!done) {
			parse(input);
		}
	} 
	while (!done);

	free(input);

	return 0;
}
