<?xml version="1.0" encoding="UTF-8"?>

<xslt:stylesheet version="1.0" xmlns:xslt="http://www.w3.org/1999/XSL/Transform">



<xslt:import href="shared.xslt"/>




<!-- Content: Create Bestellung start -->
<xslt:template name="corder_content">
<xslt:for-each select="/shop/createorder[1]/basket[1]">
	<xslt:variable name="sum"><xslt:call-template name="sum_item_art"/></xslt:variable>
	<div class="order">
	<form action="Bestellen.jsp" method="post" accept-charset="UTF-8">
		<xslt:call-template name="user_adress">
			<xslt:with-param name="liefer" select="/shop/createorder[1]/liefer[1]" />
			<xslt:with-param name="rechnung" select="/shop/createorder[1]/rechnung[1]" />
		</xslt:call-template>
		
		<!-- Header / Legende -->
		<div class="orderItem">
			<span class="name"><span>Name</span></span>
			<span class="price"><span>Produkt</span></span>
			<!-- <span class="price"><span>MwSt</span></span> -->
			<span class="price"><span>Nettopreis</span></span>
			<span class="amount"><span>Menge</span></span>
			<span class="clear" />
		</div>
	
		<!-- alle Warenkorbitems ausgeben -->
		<xslt:for-each select="item">
			<span class="orderItem">
				
				<!-- Artikelname -->
				<span class="name">
					<span><xslt:value-of select="article/@name" /></span>
				</span>
				
				<!-- Preis * Anzahl -->
				<span class="price"><span>≃ <xslt:value-of select="format-number((1.0 div 1.19) * number(@amount) * number(substring-before(article/@price, ' €')), '#0.00')" /> €</span></span>
				
				<!-- Artikelpreis in Bestellung -->
				<span class="price"><span><xslt:value-of select="format-number((1.0 div 1.19) * number(substring-before(article/@price, ' €')), '#0.00')" /> €</span></span>
				
				<!-- Artikelmenge in Bestellung -->
				<span class="amount"><span><xslt:value-of select="@amount" /> x</span></span>
									
				<span class="clear" />
			</span>
		</xslt:for-each>
		
		<!-- Summe[i:item](i.Preis * i.Menge) -->
		<xslt:variable name="preis" select="number($sum + 5.0*1.19)"/>
		
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
			<span class="price"><span><xslt:value-of select="format-number($preis,'#0.00')"/> €</span></span>
			<span class="amount"><span>Total:</span></span>
			<span class="clear" />
		</div>
		
		
		<input type="submit" value="Bestellen" />
	</form>	
	</div>
</xslt:for-each>
</xslt:template>
<!-- Content: Create Bestellung ende -->



</xslt:stylesheet>