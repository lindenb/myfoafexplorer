<?xml version="1.0" ?>
<xsl:stylesheet
	version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
	xml:lang="en"
	xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#"
	xmlns:rdfs="http://www.w3.org/2000/01/rdf-schema#"
	xmlns:foaf="http://xmlns.com/foaf/0.1/"
	xmlns:dc="http://purl.org/dc/elements/1.1/"
	>

<xsl:output method="html" version="1.0" encoding="UTF-8" indent="yes" />
<xsl:key name="indi" match="foaf:Person" use="@rdf:ID"/>
<xsl:key name="lab" match="foaf:Group" use="foaf:member/@rdf:resource"/>

<xsl:template match="/">
<html>
<head>
<link rel="shortcut icon" href="http://www.foaf-project.org/favicon.ico" />
<title><xsl:value-of select="/rdf:RDF/foaf:PersonalProfileDocument/dc:title"/></title>
</head>
<body>
<table>
<tr>	<td><img src="http://www.foaf-project.org/images/foaflets.jpg"/></td>
	<td><h1><xsl:value-of select="/rdf:RDF/foaf:PersonalProfileDocument/dc:title"/></h1></td></tr></table>
<i>This page is a <a href="http://www.foaf-project.org/">RDF/FOAF</a> file displayed as an html using
<a href="http://www.urbigene.com/foaf/foaf2html.xsl">xslt</a> transformation</i>

<xsl:for-each select="rdf:RDF/foaf:Group">

<xsl:element name="a">
<xsl:attribute name="name"><xsl:value-of select="@rdf:ID"/></xsl:attribute>
</xsl:element>

<h2><xsl:value-of select="foaf:name"/></h2>
<div style="margin-left:3cm; margin-right:3cm; background-color:rgb(220,220,255);">
<dl>
<dt>Members</dt>
<dd>
<xsl:for-each select="foaf:member">
<xsl:if test="position() &gt; 1"> , </xsl:if>
<xsl:element name="a"><xsl:attribute name="href"><xsl:value-of select="@rdf:ID"/></xsl:attribute><xsl:value-of 
select="key('indi',substring(@rdf:resource,2))/foaf:name"/></xsl:element>
</xsl:for-each>
</dd>
</dl>
</div>
</xsl:for-each>

<xsl:for-each select="rdf:RDF/foaf:Person">
<xsl:element name="a">
<xsl:attribute name="name"><xsl:value-of select="@rdf:ID"/></xsl:attribute>
</xsl:element>
<h2><xsl:value-of select="foaf:name"/></h2>
<div style="margin-left:3cm; margin-right:3cm; background-color:rgb(220,220,255);">
<dl>

<xsl:for-each select="foaf:mbox_sha1sum">
 <dt>Mailbox sha1sum : </dt><dd><xsl:value-of select="."/></dd>
</xsl:for-each>

<xsl:for-each select="foaf:nick">
 <dt>Nickname : </dt><dd><xsl:value-of select="."/></dd>
</xsl:for-each>

<xsl:for-each select="foaf:homepage">
 <dt>Homepage : </dt><dd><xsl:element name="a"><xsl:attribute name="href"><xsl:value-of 
select="@rdf:resource"/></xsl:attribute><xsl:value-of select="@rdf:resource"/></xsl:element></dd>
</xsl:for-each>

<xsl:for-each select="foaf:depiction">
 <dt>Depiction : </dt><dd><xsl:element name="img"><xsl:attribute name="src"><xsl:value-of 
select="@rdf:resource"/></xsl:attribute><xsl:value-of select="@rdf:resource"/></xsl:element></dd>
</xsl:for-each>

 <dt>Publications :</dt>
<dd>
<xsl:for-each select="foaf:publications/.//foaf:Document">
<xsl:element name="a">
<xsl:attribute name="href"><xsl:value-of select="rdfs:seeAlso/@rdf:resource"/></xsl:attribute>
<xsl:value-of select="dc:title"/>
</xsl:element><br/>
</xsl:for-each>
</dd>

<dt>Interest : </dt>
<dd>
<xsl:for-each select="foaf:interest">
<xsl:if test="position() &gt; 1"> , </xsl:if>
<xsl:element name="a">
<xsl:attribute name="href"><xsl:value-of select="rdf:Description/@rdf:about"/></xsl:attribute>
<xsl:value-of select="rdf:Description/dc:title"/>
</xsl:element>
</xsl:for-each>
</dd>


<dt>Knows : </dt>
<dd>
<xsl:for-each select="foaf:knows">
<xsl:if test="position() &gt; 1"> , </xsl:if>
<xsl:element name="a">
<xsl:attribute name="href"><xsl:value-of select="@rdf:resource"/></xsl:attribute>
<xsl:value-of select="key('indi',substring(@rdf:resource,2))/foaf:name"/>
</xsl:element>
</xsl:for-each>
</dd>

<dt>Laboratories : </dt>
<dd>
<xsl:for-each select="key('lab',concat(&apos;#&apos;,@rdf:ID))">
<xsl:if test="position() &gt; 1"> , </xsl:if>
<xsl:element name="a"><xsl:attribute name="href">#<xsl:value-of select="@rdf:ID"/></xsl:attribute>
<xsl:value-of  select="foaf:name"/></xsl:element>
</xsl:for-each>
</dd>
</dl>
</div>
</xsl:for-each>

<hr/>
<div style="font-size:9pt;">

	<a href="http://www.urbigene.com/foaf/">SciFOAF</a> -
	<a href="http://www.foaf-project.org/">http://www.foaf-project.org</a> -
	<a href="http://xml.mfd-consult.dk/foaf/explorer/">FOAF explorer</a> -
	<a href="http://www.foafnaut.org/">FOAFnaut</a> -
	<a href="http://www.ncbi.nlm.nih.gov/entrez/query.fcgi">NCBI Pubmed</a> -
	<a href="http://www.integragen.com">Integragen</a> - 
	<a href="http://www.linkedin.com/">http://www.linkedin.com/</a> -
	<a href="http://www.openbc.com/">http://www.openbc.com/</a> -
	<a href="http://www.yebn.org/">Young European Biotech Netwok</a>



<hr/>
<adress>
Pierre Lindenbaum PhD<br/>
<a href="mailto:NOSPAM_lindenb@integragen.com">lindenb@integragen.com</a><br/>
<a href="http://www.integragen.com">Integragen</a><br/>
4, rue Pierre Fontaine.
91000 EVRY.
</adress>
</div>
<div align="center"><a href="http://www.integragen.com"><img src="http://www.integragen.com/img//title.png" 
border="1" /></a></div>
</body>
</html>
</xsl:template>

</xsl:stylesheet>
