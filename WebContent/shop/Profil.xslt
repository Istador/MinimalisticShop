<?xml version="1.0" encoding="UTF-8"?>

<xslt:stylesheet version="1.0" xmlns:xslt="http://www.w3.org/1999/XSL/Transform">



<xslt:import href="shared.xslt"/>



<!-- Content:Profil start -->
<xslt:template name="profil_content">
<xslt:for-each select="/shop/user[1]">
	<div class="profil">
		Benutzername:
		<input type="text" name="login" readonly="readonly">
			<xslt:attribute name="value"><xslt:value-of select="@loginname" /></xslt:attribute>
		</input>
		
		<br/>Passwort:
		<input type="password" name="pw" />
		<input type="button" value="ändern" disabled="disabled"/>
		
		<br/>E-Mail:
		<input type="text" name="email">
			<xslt:attribute name="value"><xslt:value-of select="@email" /></xslt:attribute>
		</input>
		<input type="button" value="ändern" disabled="disabled"/>
		
		<br/>
		
		<form action="Profil.jsp" method="post" accept-charset="UTF-8">
			<xslt:call-template name="user_adress">
				<xslt:with-param name="liefer" select="liefer[1]" />
				<xslt:with-param name="rechnung" select="rechnung[1]" />
			</xslt:call-template>
			<input type="submit" value="speichern" />
		</form>
	</div>
</xslt:for-each>
</xslt:template>
<!-- Content: Profil ende -->




</xslt:stylesheet>