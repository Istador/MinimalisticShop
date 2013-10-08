<!doctype html>
<html><%@ page contentType="text/html" %><%@ page import="shop.db.*" %><%@ page import="shop.xml.*" %>
<head><title>Artikel zu Kategorie hinzufügen</title></head>
<body>
<% 
String art = request.getParameter("art");
String cat = request.getParameter("cat");
if(art != null && cat != null && art != "" && cat != ""){
	Artikel a = ArtikelX.get(Integer.valueOf(art));
	Kategorie k = KategorieX.get(Integer.valueOf(cat));
	
	if(a != null && k != null){
		k.add(a);
		shop.db.Tables.clearCaches();
		out.println("Hinzugefügt");
	}
	else
		out.println("Artikel oder Kategorie existieren nicht.");
}
%><form action="AddArtikelToKategorie.jsp" method="post" accept-charset="UTF-8">
	Artikel-ID: <input type="text" name="art"/>
	<br/>Kategorie-ID: <input type="text" name="cat"/>
	<br/><input type="submit" value="Artikel hinzufügen"/>
</form>

</body>
</html>