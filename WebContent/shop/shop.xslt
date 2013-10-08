<?xml version="1.0" encoding="UTF-8"?>

<xslt:stylesheet version="1.0" xmlns:xslt="http://www.w3.org/1999/XSL/Transform">



<xslt:output method="html" 
	encoding="UTF-8"
	doctype-system="about:legacy-compat"
	omit-xml-declaration="yes"
	standalone="yes"
	indent="yes"
/>



<!-- Includes -->
<xslt:include href="Article.xslt"/>
<xslt:include href="Kategorie.xslt"/>
<xslt:include href="Warenkorb.xslt"/>
<xslt:include href="Bestellungen.xslt"/>
<xslt:include href="Bestellung.xslt"/>
<xslt:include href="Bestellen.xslt"/>
<xslt:include href="Register.xslt"/>
<xslt:include href="Profil.xslt"/>



<xslt:template name="navigation">
	<xslt:choose>
		<xslt:when test="/shop/category[1]"><xslt:call-template name="cat_navigation"/></xslt:when>
		<xslt:when test="/shop/basket[1]"><xslt:call-template name="wk_navigation"/></xslt:when>
		<xslt:when test="/shop/order[1]"><xslt:call-template name="order_navigation"/></xslt:when>
		<xslt:otherwise></xslt:otherwise>
	</xslt:choose>
</xslt:template>



<xslt:template name="content">
	<xslt:choose>
		<xslt:when test="/shop/article[1]"><xslt:call-template name="art_content"/></xslt:when>
		<xslt:when test="/shop/articles[1]"><xslt:call-template name="cat_content"/></xslt:when>
		<xslt:when test="/shop/basket[1]"><xslt:call-template name="wk_content"/></xslt:when>
		<xslt:when test="/shop/orders[1]"><xslt:call-template name="orders_content"/></xslt:when>
		<xslt:when test="/shop/order[1]"><xslt:call-template name="order_content"/></xslt:when>
		<xslt:when test="/shop/createorder[1]"><xslt:call-template name="corder_content"/></xslt:when>
		<xslt:when test="/shop/register[1]"><xslt:call-template name="reg_content"/></xslt:when>
		<xslt:when test="/shop/user[1]/liefer[1]"><xslt:call-template name="profil_content"/></xslt:when>
		<xslt:otherwise></xslt:otherwise>
	</xslt:choose>
</xslt:template>



<!-- HTML generierung -->
<xslt:template match="/">
<html lang="de">
<head>
	<title>Shop</title>
	<link rel="stylesheet" type="text/css" href="shop.css" />
	<script type="text/javascript" src="shop.js" />
</head>
<!-- This site requires IE9, FireFox 16, Chrome 26, Safari 7 or Opera 15 to display properly. -->
<body>
	<!-- Navigation start -->
	<div id="navigation">
		<span><a href="Kategorie.jsp">Startseite</a></span>
		<span><a href="Warenkorb.jsp">Warenkorb</a></span>
		<span><a href="https://blackpinguin.de/imp.html" target="_blank">Impressum</a></span>
		<!-- Login -->
		<xslt:if test="not(/shop/user[1])">
			<div id="register">
				<span><a href="Register.jsp">Registrieren</a></span>
			</div>
			<div id="login">
				<div>
				<form action="Login.jsp" method="post" accept-charset="UTF-8">
					<input type="text" name="login" />
					<input type="password" name="pw" />
					<input type="submit" value="Login" />
				</form>
				</div>
			</div>
		</xslt:if>
		<!-- oder Profil-Link -->
		<xslt:for-each select="/shop/user[1]">
			<div id="profile">
				<span><a href="Bestellungen.jsp">Bestellungen</a></span>
				<span><a href="Profil.jsp">Profil</a></span>
				<span><a href="Logout.jsp">Logout</a></span>
			</div>
		</xslt:for-each>
		<div id="title"><span><span>Minimalistic Shop</span></span></div>
		<div class="clear" />
	</div>
	<!-- Navigation ende -->

	<!-- Fehlermeldungen ausgeben -->
	<xslt:for-each select="/shop/error">
		<div class="error"><xslt:value-of select="@msg" /></div>
	</xslt:for-each>
			
	<!-- Warnungen ausgeben -->
	<xslt:for-each select="/shop/warn">
		<div class="warn"><xslt:value-of select="@msg" /></div>
	</xslt:for-each>
			
	<!-- Infos ausgeben -->
	<xslt:for-each select="/shop/info">
		<div class="info"><xslt:value-of select="@msg" /></div>
	</xslt:for-each>
	
	<div id="content">
		<xslt:call-template name="navigation"/>
		<div id="mainContent" class="right">
			<xslt:call-template name="content"/>
		</div>
		<div class="clear" />
	</div>
</body>
</html>
</xslt:template>


</xslt:stylesheet>