<xsl:stylesheet version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	<xsl:output method="xml" indent="yes" />
	
	<xsl:template match="*">
		<xsl:element name="{name(.)}">
			<xsl:apply-templates select="@*|node()" />
		</xsl:element>
	</xsl:template>

	<xsl:template match="*[local-name()='Message']/@class">
		<xsl:attribute name="class">06</xsl:attribute>
	</xsl:template>

</xsl:stylesheet>