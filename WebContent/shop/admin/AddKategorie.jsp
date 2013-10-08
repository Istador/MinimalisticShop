<!doctype html>
<html><%@ page contentType="text/html" %><%@ page import="shop.db.*" %><%@ page import="shop.xml.*" %>
<head><title>Kategorie erstellen</title></head>
<body>
<% 
String name = request.getParameter("name");
String parent = request.getParameter("parent");
if(name != null && name != ""){
	Kategorie p = null;
	if(parent != null && parent != ""){
		p = KategorieX.get(Integer.valueOf(parent)); 
	}
	Kategorie k = Kategorien.create(name, p);
	if(p != null){
		shop.db.Tables.clearCaches();
		out.println("Erstellt mit ID "+k.id());
	}
	else
		out.println("Fehler beim Erstellen");
}
%><form action="AddKategorie.jsp" method="post" accept-charset="UTF-8">
	Name: <input type="text" name="name"/>
	<br/>Parent-ID: <input type="text" name="parent"/>
	<br/><input type="submit" value="Kategorie Erstellen"/>
</form>

</body>
</html>