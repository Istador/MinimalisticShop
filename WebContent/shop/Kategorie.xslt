<?xml version="1.0" encoding="UTF-8"?>

<xslt:stylesheet version="1.0" xmlns:xslt="http://www.w3.org/1999/XSL/Transform">



<xslt:import href="shared.xslt"/>



<!-- Navigation Start -->
<xslt:template name="cat_navigation">
<xslt:for-each select="/shop/category[1]">
	<div id="categoryNavi">
		<!-- Suche start -->
		<form action="Kategorie.jsp" method="get" accept-charset="UTF-8">
			<input type="text" name="q">
				<xslt:attribute name="value"><xslt:value-of select="/shop/@q" /></xslt:attribute>
			</input>
			<input type="hidden" name="c">
				<xslt:attribute name="value"><xslt:value-of select="@id" /></xslt:attribute>
			</input>
			<input type="submit" value="Suche" />
			<div class="clear" />
		</form>
		<!-- Suche ende -->
		
		<!-- Funktionsaufruf: Parents ausgeben -->
		<xslt:call-template name="printparents">
			<xslt:with-param name="p" select="parent[1]" />
			<xslt:with-param name="output">
				<!-- diese Kategorie -->
				<div id="selectedCategory">
					<xslt:value-of select="/shop/category/@name" />
					<!-- und Kindskategorien -->
					<xslt:for-each select="category">
						<div class="subCategory">
							<a>
								<xslt:attribute name="href">Kategorie.jsp?c=<xslt:value-of
									select="@id" /><xslt:copy-of select="$q" /></xslt:attribute>
								<xslt:value-of select="@name" />
							</a>
						</div>
					</xslt:for-each>
				</div>
			</xslt:with-param>
		</xslt:call-template>
	</div>
</xslt:for-each>
</xslt:template>
<!-- Navigation: Kategoriebaum Ende -->



<!-- Content: Mehrere Artikel start -->
<xslt:template name="cat_content">
<xslt:for-each select="/shop/articles[1]">
	<!-- wenn keine Artikel gefunden -->
	<xslt:if test="not(article)">
		<div class="warn">Keine Artikel gefunden</div>
	</xslt:if>
	
	<div class="articles">
		<!-- alle gefundenen Artikel ausgeben -->
		<xslt:for-each select="article">
			<a class="articleItem">
				<xslt:attribute name="href">Article.jsp?a=<xslt:value-of select="@id" /><xslt:copy-of select="$c" /><xslt:copy-of select="$q" /></xslt:attribute>
				<!-- Artikelbild -->
				<xslt:call-template name="art_image">
					<xslt:with-param name="id" select="@id" />
					<xslt:with-param name="name" select="@name" />
					<xslt:with-param name="hasPicture" select="@hasPicture" />
				</xslt:call-template>
				<!-- Artikelpreis -->
				<span class="price"><span><xslt:value-of select="@price" /></span></span>
				<!-- Artikelname -->
				<span class="name"><span><xslt:value-of select="@name" /></span></span>
				<span class="clear" />
			</a>
		</xslt:for-each>
	</div>
</xslt:for-each>
</xslt:template>
<!-- Content: Mehrere Artikel ende -->



<!-- Funktion: Rekursiv die Parents ausgeben -->
<xslt:template name="printparents">
	<!-- Funktionsparameter -->
	<xslt:param name="p" /> <!-- Parent -->
	<xslt:param name="output" /> <!-- Ausgabe wenn keine Parents mehr -->
	
	<!-- Funktionskörper start -->
	<xslt:choose>
		<!-- if( parent nicht vorhanden ) -->
		<xslt:when test="not($p)">
			<xslt:copy-of select="$output" />
		</xslt:when>
		<!-- else // parent vorhanden -->
		<xslt:otherwise>
			<xslt:for-each select="$p">
				<!-- Rekursiver Funktionsaufruf start -->
				<xslt:call-template name="printparents">
					<xslt:with-param name="p" select="parent[1]" />
					<!-- Ouput Parameter in ein weiteres div packen -->
					<xslt:with-param name="output">
						<div class="parentCategory">
							<a>
								<xslt:attribute name="href">Kategorie.jsp?c=<xslt:value-of select="@id" /><xslt:copy-of select="$q" /></xslt:attribute>
								<xslt:value-of select="@name" />
							</a>
							<xslt:copy-of select="$output" />
						</div>
					</xslt:with-param>
				</xslt:call-template>
				<!-- Rekursiver Funktionsaufruf ende -->
			</xslt:for-each>
		</xslt:otherwise>
	</xslt:choose>
	<!-- Funktionskörper ende -->
</xslt:template>



</xslt:stylesheet>