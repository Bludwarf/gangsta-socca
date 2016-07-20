package fr.bludwarf.gangstasocca.output;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import org.apache.commons.configuration.plist.ParseException;
import org.apache.log4j.Logger;

import fr.bludwarf.commons.StringBuilder;
import fr.bludwarf.commons.io.FileUtils;
import fr.bludwarf.gangstasocca.DoodleConnector;
import fr.bludwarf.gangstasocca.GangstaSoccaProperties;
import fr.bludwarf.gangstasocca.Joueur;
import fr.bludwarf.gangstasocca.Match;
import fr.bludwarf.gangstasocca.Sandwich;
import fr.bludwarf.gangstasocca.json.DoodleJSONParser;
import fr.bludwarf.gangstasocca.tirages.DeuxDerniersMatches;
import fr.bludwarf.gangstasocca.tirages.TirageAuSort;

public class MatchWriter
{
	protected static Logger LOG = Logger.getLogger(MatchWriter.class);
	public static DateFormat DF = DoodleJSONParser.DF_OUT;
	
	public static void writeProchainMatch(final DoodleConnector con, File file) throws Exception 
	{
		final String date = DF.format(con.getProchainMatch());
		final StringBuilder sb = new StringBuilder();
		
		sb.append("Prochain match : ").append(date).newLines(2)
		
			.append("Liste de diffusion : ")
			.append(con.getListeDeDiffusionProchainMatch()).newLines(2)

			.append("Joueurs :").newLine()
			.indent()
			.append(con.getListeJoueursProchainMatch()).newLine()
			.unindent().newLine()

			.append("Sandwiches :").newLine()
			.indent()
			.append(con.getListeSandwichesProchainMatch()).newLine()
			.unindent()
			
		;
		
		FileUtils.writeStringToFile(file, sb.toString());
	}
	
	public static File writeProchainMatchHTML(final DoodleConnector con, File template) throws Exception 
	{		
		return writeProchainMatchHTML(con.getProchainMatch(), template);
	}

	public static File writeProchainMatchHTML(Match match, File template) throws Exception
	{
		StringBuilder sb;
		
		final Properties props = new Properties();
		props.setProperty("date", match.getDateStr());
		props.setProperty("doodle.url", match.getDoodle());

		// Joueurs
		final Set<Joueur> joueursInscrits = match.getJoueurs();
		final List<Joueur> joueurs; // sélectionnés
		
		// Tirage au sort des joueurs si besoin
		final int nbJoueursMax = GangstaSoccaProperties.getInstance().getInt("match.nbJoueursMax");
		if (joueursInscrits.size() > nbJoueursMax)
		{
			LOG.warn(String.format("Un tirage au sort est nécessaire pour choisir %s joueurs parmi les %s inscrits", nbJoueursMax, joueursInscrits.size()));
			TirageAuSort tirage = new DeuxDerniersMatches();
			tirage.tirer(nbJoueursMax, joueursInscrits);
			joueurs = tirage.getJoueurs();
		}
		else
		{
			joueurs = new ArrayList<Joueur>(joueursInscrits);
		}
		
		props.setProperty("joueurs.nb", Integer.toString(joueurs.size()));
		
		sb = new StringBuilder();
		for (final Joueur j : joueurs)
		{
			final Properties jProps = new Properties();
			jProps.setProperty("pseudo", j.getPseudoActuel());
			jProps.setProperty("email", j.getEmail());
			jProps.setProperty("sandwich", j.getSandwich().getNom());
			final String jContent = FileUtils.readTemplate("joueurs.html.li.template", jProps);
			sb.append(jContent);
		}
		props.setProperty("joueurs.html.li", sb.toString());
		
		// Dwiches
		sb = new StringBuilder();
		int nbSand = 0;
		final Map<Sandwich, Integer> sandwiches = match.getSandwiches();
		for (final Sandwich s : sandwiches.keySet())
		{
			final int nb = sandwiches.get(s);
			sb.append(String.format("<li><b>%s</b> x %s</li>",
				nb,
				s.getNom()));
			nbSand += nb;
		}
		props.setProperty("sandwiches.html.li", sb.toString());
		
		props.setProperty("joueurs.communicator", match.getCommunicator());
		props.setProperty("joueurs.mail", match.getMail());
		
		// Nb de sandwiches
		props.setProperty("sandwiches.nb", Integer.toString(nbSand));
		
		return writeProchainMatchHTML(props, template);
	}
	
	public static File writeProchainMatchHTML(final Properties props, File template) throws IOException, ParseException 
	{
		return FileUtils.writeTemplate(props, template);
	}
	
}
