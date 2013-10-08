<?xml version="1.0" encoding="UTF-8"?>

<xslt:stylesheet version="1.0" xmlns:xslt="http://www.w3.org/1999/XSL/Transform">



<!-- Kategorie GET Parameter c -->
<xslt:variable name="c"><xslt:if test="/shop/category/@id != ''">&amp;c=<xslt:value-of select="/shop/category/@id" /></xslt:if></xslt:variable>



<!-- Suche GET Parameter q -->
<xslt:variable name="q"><xslt:if test="/shop/@q != ''">&amp;q=<xslt:value-of select="/shop/@q" /></xslt:if></xslt:variable>



<!-- Funktion: Artikelbild ausgeben wenn vorhanden, sonst Platzhalter -->
<xslt:template name="art_image">
	<!-- Funktionsparameter -->
	<xslt:param name="id" />
	<xslt:param name="name" />
	<xslt:param name="hasPicture" />

	<!-- Funktionskörper -->
	<span class="imgHolder">
		<xslt:if test="$hasPicture = 'true'">
			<img>
				<xslt:attribute name="src">images/<xslt:value-of select="$id" />.jpg</xslt:attribute>
				<xslt:attribute name="alt"><xslt:value-of select="$name" /></xslt:attribute>
			</img>
		</xslt:if>
	</span>
</xslt:template>



<!-- Funktion: Bestellstatus ausgeben -->
<xslt:template name="order_status">
	<!-- Funktionsparameter -->
	<xslt:param name="status" />
	<!-- Funktionskörper -->
	<span class="status">
	<xslt:attribute name="class">status status<xslt:value-of select="$status" /></xslt:attribute>
	<xslt:choose>
		<xslt:when test="$status = 0">Angelegt</xslt:when>
		<xslt:when test="$status = 1">Wird Geprüft</xslt:when>
		<xslt:when test="$status = 2">In Bearbeitung</xslt:when>
		<xslt:when test="$status = 3">Storniert</xslt:when>
		<xslt:when test="$status = 4">Im Versand</xslt:when>
		<xslt:when test="$status = 5">Abgeschlossen</xslt:when>
		<xslt:otherwise>Unbekannt</xslt:otherwise>
	</xslt:choose>
	</span>
</xslt:template>



<!-- Rekursive Funktion zur Berechnung der Summe -->
<xslt:template name="sum_item">
	<!-- Rekursions Akkumulator -->
	<xslt:param name="item" select="item[1]" />
	<xslt:param name="accu" select="0"/>
	<xslt:param name="i" select="1"/>
	
	<!-- Summe -->
	<xslt:variable name="sum" select="$accu + number($item/@amount) * number(substring-before($item/@price, ' €'))" />
	
	<xslt:choose>
		<!-- wenn noch mehr <item>-elemente -->
		<xslt:when test="item[$i + 1]">
			<!-- Rekursionsschritt -->
			<xslt:call-template name="sum_item">
				<xslt:with-param name="item" select="item[$i + 1]" />
				<xslt:with-param name="accu" select="$sum" />
				<xslt:with-param name="i" select="$i + 1" />
			</xslt:call-template>
		</xslt:when>
		<!-- Rekursionsabbruch: Summe ausgeben -->
		<xslt:otherwise><xslt:value-of select="$sum"/></xslt:otherwise>
	</xslt:choose>
</xslt:template>



<!-- Rekursive Funktion zur Berechnung der Summe -->
<xslt:template name="sum_item_art">
	<!-- Rekursions Akkumulator -->
	<xslt:param name="item" select="item[1]" />
	<xslt:param name="accu" select="0"/>
	<xslt:param name="i" select="1"/>
	
	<!-- Summe -->
	<xslt:variable name="sum" select="$accu + number($item/@amount) * number(substring-before($item/article/@price, ' €'))" />
	
	<xslt:choose>
		<!-- wenn noch mehr <item>-elemente -->
		<xslt:when test="item[$i + 1]">
			<!-- Rekursionsschritt -->
			<xslt:call-template name="sum_item_art">
				<xslt:with-param name="item" select="item[$i + 1]" />
				<xslt:with-param name="accu" select="$sum" />
				<xslt:with-param name="i" select="$i + 1" />
			</xslt:call-template>
		</xslt:when>
		<!-- Rekursionsabbruch: Summe ausgeben -->
		<xslt:otherwise><xslt:value-of select="$sum"/></xslt:otherwise>
	</xslt:choose>
</xslt:template>



<!-- Funktion: Liefer und Rechnungsadresse im Form ausgeben -->
<xslt:template name="user_adress">
	<!-- Funktionsparameter -->
	<xslt:param name="liefer" />
	<xslt:param name="rechnung" />
	
	<xslt:variable name="r_wie_l" select="not($liefer/address[1]) or not($rechnung/address[1]) or ($rechnung/address[1]/@id = $liefer/address[1]/@id)" />

	<!-- Funktionskörper -->
	<div class="liefer">
		Lieferadresse:
		<br/>
		<input id="l_old" name="l_new" value="0" onclick="javascript:lold()" type="radio">
			<xslt:if test="$liefer/address[1]"><xslt:attribute name="checked">checked</xslt:attribute></xslt:if>
			<xslt:if test="not($liefer/address[1])"><xslt:attribute name="disabled">disabled</xslt:attribute></xslt:if>
		</input> Profil-Adresse
		<br/>
		<input id="l_new" name="l_new" value="1" onclick="javascript:lnew()" type="radio">
			<xslt:if test="not($liefer/address[1])"><xslt:attribute name="checked">checked</xslt:attribute></xslt:if>
		</input> Neue Adresse
		<br/>
		<textarea id="liefer" name="liefer" rows="3" cols="25">
			<xslt:if test="$liefer/address[1]"><xslt:attribute name="disabled">disabled</xslt:attribute></xslt:if>
			<xslt:value-of select="$liefer/address[1]"/>
		</textarea>
		<br/>
		<input id="r_wie_l" name="r_wie_l" onclick="javascript:rwiel()" value="1" type="checkbox">
			<xslt:if test="$r_wie_l"><xslt:attribute name="checked">checked</xslt:attribute></xslt:if>
		</input> Rechnungs wie Lieferadresse
	</div>
	
	<div id="r_control" class="rechnung">
		<xslt:if test="$r_wie_l"><xslt:attribute name="style">display:none;</xslt:attribute></xslt:if>
		Rechnungsadresse:
		<br/>
		<input id="r_old" name="r_new" value="0" onclick="javascript:rold()" type="radio">
			<xslt:if test="$rechnung/address[1]"><xslt:attribute name="checked">checked</xslt:attribute></xslt:if>
			<xslt:if test="not($rechnung/address[1])"><xslt:attribute name="disabled">disabled</xslt:attribute></xslt:if>
		</input> Profil-Adresse
		<br/>
		<input id="r_new" name="r_new" value="1" onclick="javascript:rnew()" type="radio">
			<xslt:if test="not($rechnung/address[1])"><xslt:attribute name="checked">checked</xslt:attribute></xslt:if>
		</input> Neue Adresse
		<br/>
		<textarea id="rechnung" name="rechnung" rows="3" cols="25">
			<xslt:if test="$rechnung/address[1]"><xslt:attribute name="disabled">disabled</xslt:attribute></xslt:if>
			<xslt:value-of select="$rechnung/address[1]"/>
		</textarea>
	</div>
	
	<div class="clear"></div>
	<br/>
</xslt:template>





</xslt:stylesheet>