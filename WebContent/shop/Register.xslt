<?xml version="1.0" encoding="UTF-8"?>

<xslt:stylesheet version="1.0" xmlns:xslt="http://www.w3.org/1999/XSL/Transform">



<xslt:import href="shared.xslt"/>



<!-- Content:Registrieren start -->
<xslt:template name="reg_content">
<xslt:for-each select="/shop/register[1]">
	<div class="register">
		<form action="Register.jsp" method="post" accept-charset="UTF-8">
			Benutzername:
			<input type="text" name="login">
				<xslt:attribute name="value"><xslt:value-of select="@loginname" /></xslt:attribute>
			</input>
			
			<br/>Passwort:
			<input type="password" name="pw" />
			
			<br/>E-Mail:
			<input type="text" name="email">
				<xslt:attribute name="value"><xslt:value-of select="@email" /></xslt:attribute>
			</input>

			<br/>
			<input type="submit" value="Registrieren" />
		</form>
	</div>
</xslt:for-each>
</xslt:template>
<!-- Content: Registrieren ende -->




</xslt:stylesheet>