<?xml version='1.0' ?>
<xsl:stylesheet xmlns:xsl='http://www.w3.org/1999/XSL/Transform' version='1.0'>
<xsl:output method='html'/>
<xsl:preserve-space elements='Pagination PubDate Affiliation' />

<xsl:template match="/">
<html><body><div style="width:18cm; color:black; background-color:white; ">
<xsl:apply-templates/>
</div></body></html>
</xsl:template>

<!-- =================================================================== -->
<!-- =================================================================== -->
<!-- =================================================================== -->

<xsl:template match="PubmedArticleSet">
<div style="width:18cm; color:black; background-color:white; ">
<xsl:apply-templates select='PubmedArticle'/>
</div>
</xsl:template>

<!-- =================================================================== -->
<!-- =================================================================== -->
<!-- =================================================================== -->

<xsl:template match="PubmedArticle">
<div style="width:18cm; color:black; background-color:white; ">
<xsl:element name='a'><xsl:attribute name="name">pmid<xsl:value-of select='MedlineCitation/PMID'/></xsl:attribute></xsl:element>
<i>
<xsl:element name='a'><xsl:attribute name="href">http://www.ncbi.nlm.nih.gov/entrez/query.fcgi?cmd=Retrieve&amp;db=PubMed&amp;list_uids=<xsl:value-of select='normalize-space(MedlineCitation/PMID)'/>&amp;dopt=Abstract</xsl:attribute>PMID: <xsl:value-of select='MedlineCitation/PMID'/></xsl:element></i><br/>
<xsl:apply-templates select='MedlineCitation/MedlineJournalInfo' /><xsl:text> </xsl:text>
	<xsl:apply-templates select='MedlineCitation/Article/Journal/JournalIssue' /><xsl:text> </xsl:text>
	p. <xsl:apply-templates select='MedlineCitation/Article/Pagination' /><br />
<xsl:apply-templates select='MedlineCitation/Article/AuthorList' /><br />
<xsl:apply-templates select='MedlineCitation/Article/Affiliation'/>
<h3><xsl:apply-templates select='MedlineCitation/Article/ArticleTitle' /></h3>
<p style='font-size:12pt; width:15cm;'><xsl:apply-templates select='MedlineCitation/Article/Abstract'/></p>

<!-- xsl:apply-templates select='MeshHeadingList' -->

<p style='align:right;'>
<xsl:element name='a'><xsl:attribute name="href">http://www.ncbi.nlm.nih.gov:80/entrez/query.fcgi?db=PubMed&amp;cmd=Display&amp;dopt=pubmed_pubmed&amp;from_uid=<xsl:value-of select='MedlineCitation/PMID'/></xsl:attribute>Related Articles</xsl:element>
</p>
<hr/>

</div>
</xsl:template>


<!-- =================================================================== -->
<!-- =================================================================== -->
<!-- =================================================================== -->
<xsl:template match="AuthorList">
<xsl:apply-templates select='Author' />
</xsl:template>

<!-- =================================================================== -->
<!-- =================================================================== -->
<!-- =================================================================== -->
<xsl:template match="Author">
<xsl:if test='position() &gt; 1 and position()!=last()'>, </xsl:if>
<xsl:if test='position() &gt; 1 and position()=last()'> and  </xsl:if>
		<xsl:call-template name="pubmed-anchor-query">
                <xsl:with-param name="term">&quot;<xsl:value-of select='LastName' /><xsl:text> </xsl:text><xsl:value-of select='Initials'/> &quot;[AU]</xsl:with-param>
                <xsl:with-param name="content"><xsl:value-of select='ForeName'/><xsl:text> </xsl:text><xsl:value-of select='LastName'/></xsl:with-param>
                </xsl:call-template>
</xsl:template>

<!-- =================================================================== -->
<!-- =================================================================== -->
<!-- =================================================================== -->
<xsl:template match="MeshHeadingList">
<table><tr style='font-size:7pt;'>
<xsl:apply-templates select='MeshHeading' />
</tr></table>
</xsl:template>

<!-- =================================================================== -->
<!-- =================================================================== -->
<!-- =================================================================== -->
<xsl:template match="MeshHeading">
<xsl:element name='td'>
<xsl:attribute name='style'>
<xsl:if test='position() mod 2 = 1'>background:lightgray</xsl:if>
<xsl:if test='position() mod 2 = 0'>background:rgb(250,250,250)</xsl:if>
</xsl:attribute>
</xsl:element>
</xsl:template>




<!-- =================================================================== -->
<!-- =================================================================== -->
<!-- =================================================================== -->
<xsl:template match="MedlineJournalInfo">
                <xsl:call-template name="pubmed-anchor-query">
                <xsl:with-param name="term"><xsl:value-of select='MedlineTA' />[TA]</xsl:with-param>
                <xsl:with-param name="content"><xsl:value-of select='MedlineTA' /></xsl:with-param>
                </xsl:call-template>
</xsl:template>

<!-- =================================================================== -->
<!-- =================================================================== -->
<!-- =================================================================== -->
<xsl:template match="JournalIssue">
	<b><xsl:value-of select="Volume"/></b>
	<xsl:text> </xsl:text>(<xsl:value-of select="Issue" />):
	<xsl:value-of select="PubDate" />
</xsl:template>




<!-- =================================================================== -->
<!-- =================================================================== -->
<!-- =================================================================== -->

<xsl:template name="name-url-to-anchor">
<xsl:param name="name" />
<xsl:param name="mail" />
<xsl:element name="a">
	<xsl:attribute name="href">mailto:<xsl:value-of select="$mail" /></xsl:attribute>
	<xsl:value-of select="$name" />
</xsl:element>
</xsl:template>
<!-- =================================================================== -->
<!-- =================================================================== -->
<!-- =================================================================== -->
<xsl:template name="pubmed-anchor-query">
<xsl:param name="content" />
<xsl:param name="term" />
<xsl:value-of select="$content" />
</xsl:template>

<!-- =================================================================== -->
<!-- =================================================================== -->
<!-- =================================================================== -->



</xsl:stylesheet>

