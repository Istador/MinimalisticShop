<?xml version="1.0" encoding="UTF-8"?>

<xslt:stylesheet version="1.0" xmlns:xslt="http://www.w3.org/1999/XSL/Transform">



<xslt:import href="shared.xslt"/>



<!-- Navigation: Warenkorb Navigation Start -->
<xslt:template name="order_navigation">
<xslt:for-each select="/shop/order[1]">
	<xslt:if test="@status = 0 or @status = 1">
	<div id="orderNavi">
		<form method="post" accept-charset="UTF-8">
			<xslt:attribute name="action">Bestellung.jsp?b=<xslt:value-of select="@id" /></xslt:attribute>
			<input type="hidden" name="action" value="storno"/>
			<input type="submit" value="Bestellung stornieren" />
		</form>
	</div>
	</xslt:if>
</xslt:for-each>
</xslt:template>
<!-- Navigation: Warenkorb Navigation Ende -->





<!-- Content: Einzelne Bestellung start -->
<xslt:template name="order_content">
<xslt:for-each select="/shop/order[1]">
	<div class="order">
		Bestellnr.: <xslt:value-of select="@id"/>
		<br/>Bestelldatum: <xslt:value-of select="@datetime"/>
		<br/>Status:
		<xslt:call-template name="order_status">
			<xslt:with-param name="status" select="@status" />
		</xslt:call-template>
		<br/>
		<br/>
		<div class="liefer">
			Lieferadresse:
			<xslt:for-each select="liefer[1]/address[1]/text()">
				<br/>
				<xslt:value-of select="."/>
			</xslt:for-each>
		</div>
		<div class="rechnung">
			Rechnungsadresse:
			<xslt:for-each select="rechnung[1]/address[1]/text()">
				<br/>
				<xslt:value-of select="."/>
			</xslt:for-each>
		</div>
		<div class="clear"></div>
		
		<br/>
		
		<!-- Header / Legende -->
		<div class="orderItem">
			<span class="name"><span>Name</span></span>
			<span class="price"><span>Produkt</span></span>
			<!-- <span class="price"><span>MwSt</span></span> -->
			<span class="price"><span>Nettopreis</span></span>
			<span class="amount"><span>Menge</span></span>
			<span class="clear" />
		</div>
		
		<!-- für jedes Item -->
		<xslt:for-each select="item">
			<a class="orderItem">
				<xslt:attribute name="href">Article.jsp?a=<xslt:value-of select="article/@id" />&amp;c=1</xslt:attribute>
				
				<!-- Artikelname -->
				<span class="name">
					<span><xslt:value-of select="article/@name" /></span>
				</span>
				
				<!-- Preis * Anzahl -->
				<span class="price"><span>≃ <xslt:value-of select="format-number((1.0 div 1.19) * number(@amount) * number(substring-before(@price, ' €')), '#0.00')" /> €</span></span>
				
				<!-- Mehrwertsteuer -->
				<!-- <span class="price"><span><xslt:value-of select="format-number((1.0 - 1.0 div 1.19) * number(substring-before(@price, ' €')), '#0.00')" /> €</span></span> -->
				
				<!-- Artikelpreis in Bestellung -->
				<span class="price"><span><xslt:value-of select="format-number((1.0 div 1.19) * number(substring-before(@price, ' €')), '#0.00')" /> €</span></span>
				
				<!-- Artikelmenge in Bestellung -->
				<span class="amount"><span><xslt:value-of select="@amount" /> x</span></span>
									
				<span class="clear" />
			</a>
		</xslt:for-each>
		
		<!-- Summe[i:item](i.Preis * i.Menge) -->
		<xslt:variable name="preis" select="number(substring-before(@price, ' €'))"/>
		<!-- <xslt:variable name="sum"><xslt:call-template name="sum_item"/></xslt:variable> -->
		
		<!-- Summe -->
		<div class="orderItem">
			<span class="price"><span><xslt:value-of select="format-number((1.0 div 1.19) * ($preis - (5.0*1.19)),'#0.00')"/> €</span></span>
			<span class="amount"><span>Summe:</span></span>
			<span class="clear" />
		</div>
		
		<!-- Versandkosten -->
		<div class="orderItem">
			<span class="price"><span>5.00 €</span></span>
			<span class="amount"><span>Versand:</span></span>
			<span class="clear" />
		</div>
		
		<!-- MwSt = (Summe+5.00) * 0.19 -->
		<div class="orderItem">
			<span class="price"><span><xslt:value-of select="format-number((0.19 div 1.19) * $preis,'#0.00')"/> €</span></span>
			<span class="amount"><span>MwSt:</span></span>
			<span class="clear" />
		</div>
		
		<!-- Gesamtpreis -->
		<div class="orderItem">
			<span class="price"><span><xslt:value-of select="@price"/></span></span>
			<span class="amount"><span>Total:</span></span>
			<span class="clear" />
		</div>
	</div>
</xslt:for-each>
</xslt:template>
<!-- Content: Einzelne Bestellung ende -->




</xslt:stylesheet>