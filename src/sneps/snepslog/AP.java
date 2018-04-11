package sneps.snepslog;

import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import java_cup.runtime.Symbol;

@SuppressWarnings("deprecation")
public class AP {

	public static String executeSnepslogCommand(String command) throws Exception{
		InputStream is = new ByteArrayInputStream(command.getBytes(StandardCharsets.UTF_8));
		DataInputStream dis = new DataInputStream(is);
		parser parser = new parser(new Lexer(dis));
		Symbol res = parser.parse();
		String output = (String) res.value;
		is.close();
		dis.close();
		return output;
	}
	
}
