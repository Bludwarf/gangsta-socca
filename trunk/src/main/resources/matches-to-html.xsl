<?xml version="1.0" encoding="UTF-8" ?>
<xsl:stylesheet version="2.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
>

	<xsl:template match="/matches">
		<html>
			<head>
				<style type="text/css">
					
					div#matches {
					}
				
					table td {
						border: solid 1px black;
					}
					
					td.rouge {
						background: #FAA;
					}
					
					td.bleu {
						background: #AAF;
					}
				</style>
			</head>
			<body>
			
				<div id="matches">
			
					<h1>Matches</h1>
					
					<table>
						<xsl:for-each select="match[@joué = 'true']">
							<xsl:sort select="@date" order="descending" />
						
							<xsl:variable name="date" select="substring-before(@date, 'T')" />
								
							<!-- Joueurs -->
							<xsl:for-each select="joueurs/joueur">
							
								<!-- Style -->
								<xsl:variable name="joueur-class">
									<xsl:choose>
										<xsl:when test="@rouge = 'true'">
											<xsl:text>rouge</xsl:text>
										</xsl:when>
										<xsl:otherwise>
											<xsl:text>bleu</xsl:text>
										</xsl:otherwise>
									</xsl:choose>
								</xsl:variable>
							
								<tr>
									<xsl:if test="position() = 1">
										<td rowspan="{count(../joueur)}"><xsl:value-of select="$date" /><br/>
										(<xsl:value-of select="round(sum(../joueur[@rouge = 'true']/@elo-avant) div count(../joueur[@rouge = 'true']))" />
										VS
										<xsl:value-of select="round(sum(../joueur[not(@rouge = 'true')]/@elo-avant) div count(../joueur[not(@rouge = 'true')]))" />)</td>
									</xsl:if>
									<td class="{$joueur-class}"><xsl:value-of select="text()" /></td>
									<td class="{$joueur-class}"><xsl:value-of select="round(@elo-avant)" /></td>
									<td class="{$joueur-class}">
										<xsl:choose>
											<xsl:when test="@elo-après &gt; @elo-avant">
												<xsl:text>▲</xsl:text>
											</xsl:when>
											<xsl:otherwise>
												<xsl:text>▼</xsl:text>
											</xsl:otherwise>
										</xsl:choose>
										<xsl:value-of select="round(@elo-après - @elo-avant)" />
									</td>
									<td class="{$joueur-class}"><xsl:value-of select="round(@elo-après)" /></td>
								</tr>
							
							</xsl:for-each>
								
						
						</xsl:for-each>
					</table>
					
				</div>
				
				<div id="classement">
					
					<h1>Classement :</h1>
					
					<table>
						
						<tr>
							<th>Score</th>
							<th>Joueur</th>
						</tr>
						
						<xsl:for-each-group select="match[@joué = 'true']/joueurs/joueur" group-by=".">
						
							<tr>
								<td>group</td>
								<td><xsl:value-of select="." /></td>
							</tr>
						
						</xsl:for-each-group>
						
					</table>
					
				</div>
				
			</body>
		</html>
	</xsl:template>
	
</xsl:stylesheet>