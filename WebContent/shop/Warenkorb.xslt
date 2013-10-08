<?xml version="1.0" encoding="UTF-8"?>

<xslt:stylesheet version="1.0" xmlns:xslt="http://www.w3.org/1999/XSL/Transform">



<xslt:import href="shared.xslt"/>



<!-- Navigation: Warenkorb Navigation Start -->
<xslt:template name="wk_navigation">
<xslt:for-each select="/shop/basket[1]">
	<!-- Nur wenn Artikel vorhanden -->
	<xslt:if test="item">
		<div id="basketNavi">
			<xslt:variable name="sum"><xslt:call-template name="sum_item_art"/></xslt:variable>
			<span>Gesamtpreis: <xslt:value-of select="format-number($sum, '#0.00')"/> €</span>
			
			<form action="Warenkorb.jsp?a=-1" method="post" accept-charset="UTF-8">
				<input type="submit" value="Warenkorb leeren" />
			</form>
			<xslt:choose>
				<!-- nicht eingeloggt -->
				<xslt:when test="not(/shop/user[1])">
					<br/>Sie müssen sich erst <a href="Register.jsp">Registrieren</a> um bestellen zu können.
				</xslt:when>
				<!-- eingeloggt -->
				<xslt:otherwise>
					<form action="Bestellen.jsp" method="get" accept-charset="UTF-8">
						<input type="submit" value="Zur Kasse" />
					</form>
				</xslt:otherwise>
			</xslt:choose>
		</div>
	</xslt:if>
</xslt:for-each>
</xslt:template>
<!-- Navigation: Warenkorb Navigation Ende -->



<!-- Content: Warenkorb start -->
<xslt:template name="wk_content">
<xslt:for-each select="/shop/basket[1]">
	<!-- wenn keine Artikel im Warenkorb -->
	<xslt:if test="not(item)">
		<div class="warn">Warenkorb ist leer</div>
	</xslt:if>
	
	<div class="basket">
		<!-- alle Warenkorbitems ausgeben -->
		<xslt:for-each select="item">
			<div class="basketItem">
				<!-- Artikelbild -->
				<a class="image">
					<xslt:attribute name="href">Article.jsp?a=<xslt:value-of select="article/@id" />&amp;c=1</xslt:attribute>
					<xslt:call-template name="art_image">
						<xslt:with-param name="id" select="article/@id" />
						<xslt:with-param name="name" select="article/@name" />
						<xslt:with-param name="hasPicture" select="article/@hasPicture" />
						</xslt:call-template>
					<span class="clear"/>
				</a>
				<!-- Artikel-Anzahl-Steuerung -->
				<div class="control">
					<!-- Anzahl des Artikel im Warenkorb ändern -->
					<form action="Warenkorb.jsp" method="post" accept-charset="UTF-8">
						<input type="hidden" name="a">
							<xslt:attribute name="value"><xslt:value-of select="article/@id" /></xslt:attribute>
						</input>
						<input type="text" name="n">
							<xslt:attribute name="value"><xslt:value-of select="@amount" /></xslt:attribute>
						</input>
						<input type="submit" value="ändern" />
					</form>
					<!-- Artikel von Warenkorb entfernen -->
					<form action="Warenkorb.jsp" method="post" accept-charset="UTF-8">
						<input type="hidden" name="a">
							<xslt:attribute name="value"><xslt:value-of	select="article/@id" /></xslt:attribute>
						</input>
						<input type="hidden" name="n" value="0" />
						<input type="submit" value="entfernen" />
					</form>
				</div>
				
				<!-- Artikelpreis -->
				<div class="price"><span><xslt:value-of select="article/@price" /></span></div>
				
				<!-- Artikelname -->
				<a class="name">
					<xslt:attribute name="href">Article.jsp?a=<xslt:value-of select="article/@id" />&amp;c=1</xslt:attribute>
					<span><xslt:value-of select="article/@name" /></span>
				</a>
										
				<div class="clear" />
			</div>
		</xslt:for-each>
	</div>
</xslt:for-each>
</xslt:template>
<!-- Content: Warenkorb ende -->



</xslt:stylesheet>