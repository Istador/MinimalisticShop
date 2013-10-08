<?xml version="1.0" encoding="UTF-8"?>

<xslt:stylesheet version="1.0" xmlns:xslt="http://www.w3.org/1999/XSL/Transform">



<xslt:import href="shared.xslt"/>



<!-- Content: Bestellungen start -->
<xslt:template name="orders_content">
<xslt:for-each select="/shop/orders[1]">
	<!-- wenn keine Bestellungen gefunden -->
	<xslt:if test="not(order)">
		<div class="warn">Sie haben noch keine Bestellungen.</div>
	</xslt:if>
	
	<div class="orders">
		<!-- alle Bestellungen ausgeben -->
		<xslt:for-each select="order">
			<a class="order">
				<xslt:attribute name="href">Bestellung.jsp?b=<xslt:value-of select="@id" /></xslt:attribute>
				<span class="date">Bestellung von <xslt:value-of select="@datetime"/></span>
				<!-- Bestellstatus ausgeben -->
				<xslt:call-template name="order_status">
					<xslt:with-param name="status" select="@status" />
				</xslt:call-template>
				<span class="price">Summe: <xslt:value-of select="@price"/></span>
				<span class="clear" />
			</a>
		</xslt:for-each>
	</div>
</xslt:for-each>
</xslt:template>
<!-- Content: Bestellungen ende -->



</xslt:stylesheet>