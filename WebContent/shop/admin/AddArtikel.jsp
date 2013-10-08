<!doctype html>
<html><%@ page contentType="text/html" %><%@ page import="shop.db.*" %><%@ page import="shop.util.*" %>
<head><title>Artikel erstellen</title></head>
<body>
<% 
String name = request.getParameter("name");
String desc = request.getParameter("desc");
String money = request.getParameter("price");
if(name != null && desc != null && money != null && name != "" && desc != "" && money != ""){
	MoneyClass price = shop.db.package$.MODULE$.stringToMoney(money);
	Artikel a = Artikel.create(name, desc, price);
	if(a != null){
		shop.db.Tables.clearCaches();
		out.println("Erstellt mit ID "+a.id());
	}
	else
		out.println("Fehler beim Erstellen");
}
%><form action="AddArtikel.jsp" method="post" accept-charset="UTF-8">
	Name: <input type="text" name="name"/>
	<br/>Preis: <input type="text" name="price"/>
	<br/>Beschreibung:
	<br/><textarea rows="5" cols="80" name="desc"></textarea>
	<br/><input type="submit" value="Artikel Erstellen"/>
</form>

</body>
</html>