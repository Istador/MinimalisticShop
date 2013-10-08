<%@ page contentType="text/plain" %><%@ page import="shop.db.Tables" %>
<%
	out.println("drop...");
	Tables.drop();
	out.println("create...");
	Tables.create();
	out.println("init...");
	Tables.init();
	out.println("empty caches...");
	Tables.clearCaches();
	out.println("done.");
%>