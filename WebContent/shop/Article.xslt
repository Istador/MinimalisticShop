<?xml version="1.0" encoding="UTF-8"?>

<xslt:stylesheet version="1.0" xmlns:xslt="http://www.w3.org/1999/XSL/Transform">



<xslt:import href="shared.xslt"/>



<!-- Content: Einzelner Artikel start -->
<xslt:template name="art_content">
<xslt:for-each select="/shop/article[1]">
	<div class="article">
		<!-- Artikelbild -->
		<xslt:call-template name="art_image">
			<xslt:with-param name="id" select="@id" />
			<xslt:with-param name="name" select="@name" />
			<xslt:with-param name="hasPicture" select="@hasPicture" />
		</xslt:call-template>
		<!-- Artikel zum Warenkorb hinzufügen -->
		<form action="Warenkorb.jsp" method="post" accept-charset="UTF-8">
			<input type="hidden" name="a">
				<xslt:attribute name="value"><xslt:value-of select="@id" /></xslt:attribute>
			</input>
			<input type="submit" value="Zum Warenkorb hinzufügen" />
		</form>

		Name: <xslt:value-of select="@name" />
		<br/>Preis: <xslt:value-of select="@price" />
		<br/>Beschreibung:
		<br/><xslt:value-of select="desc" />
		<br/>Kategorien:
		<xslt:for-each select="category">
			<div class="category">
				<a>
					<xslt:attribute name="href">Kategorie.jsp?c=<xslt:value-of select="@id" /><xslt:copy-of select="$q" /></xslt:attribute>
					<xslt:value-of select="@name" />
				</a>
			</div>
		</xslt:for-each>
		<div class="clear" />
	</div>
</xslt:for-each>
</xslt:template>
<!-- Content: Einzelner Artikel ende -->




</xslt:stylesheet>