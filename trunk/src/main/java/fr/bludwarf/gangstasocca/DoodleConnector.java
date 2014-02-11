package fr.bludwarf.gangstasocca;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.configuration.plist.ParseException;
import org.apache.commons.io.IOUtils;

import fr.bludwarf.commons.StringBuilder;
import fr.bludwarf.commons.StringUtils;
import fr.bludwarf.commons.formatters.MapFormatter;
import fr.bludwarf.commons.io.FileUtils;
import fr.bludwarf.commons.web.WebConnector;
import fr.bludwarf.gangstasocca.json.DoodleJSONParser;

public class DoodleConnector extends WebConnector
{
	
	/** Log */
	protected static org.apache.log4j.Logger LOG = org.apache.log4j.Logger
			.getLogger(DoodleConnector.class);
	public static String URL = "http://www.doodle.com/uvbbrna677dfbw7e";
	public static DateFormat DF = DoodleJSONParser.DF_OUT;
	
	public static MapFormatter<Sandwich, Integer> DWICH_FORMATTER = new MapFormatter<Sandwich, Integer>() {

		public String format(Sandwich dwich, Integer n, int i)
		{
			return String.format("%s x %s", n, dwich);
		}
	};
	
	private URL _url;
	private DoodleJSONParser _parser;

	public DoodleConnector() throws MalformedURLException
	{
		super();
		_url = new URL(URL);
	}

	public DoodleConnector(final String url) throws MalformedURLException
	{
		super();
		_url = new URL(url);
	}
	
	public URL getURL()
	{
		return _url;
	}
	
	public final String getDoodlePage() throws IOException
	{
		return get(getURL());
	}
	
	/**
	 * @return valeur de l'objet JSON <code>doodleJS.data.poll</code> dans la page web du Doodle
	 * @throws IOException
	 * @throws ParseException 
	 */
	public final String getData() throws IOException, ParseException
	{
		final Pattern p = Pattern.compile("doodleJS.data.poll = (\\{.+\\});");
		final Matcher m = p.matcher(getDoodlePage());
		
		if (m.find())
		{
			return m.group(1);
		}
		else
		{
			throw new ParseException("Impossible de trouver la valeur de la variable JSON \"doodleJS.data.poll\" sur la page Doodle : " + _url);
		}
	}
	
	public SortedSet<Joueur> getJoueurs(final Date date) throws IOException, ParseException 
	{
		final List<String> pseudosEtDwiches = getParser()
				.getParticipants(date);
		
		// On doit séparer les pseudos des dwiches
		List<String> pseudos = new ArrayList<String>();
		List<String> dwiches = new ArrayList<String>();
		for (final String pseudoEtDwich : pseudosEtDwiches)
		{
			// Avec dwich ?
			if (pseudoEtDwich.contains("("))
			{
				final Pattern p = Pattern.compile("(.+?)\\s*\\((.+)\\)");
				final Matcher m = p.matcher(pseudoEtDwich);
				if (m.matches())
				{
					final String pseudo = m.group(1);
					pseudos.add(pseudo);
					final String dwich  = m.group(2);
					dwiches.add(dwich);
				}
				else
				{
					LOG.error(String.format(
						"Le pseudo (avec sandwich) \"%s\" n'est pas matché par \"%s\"" + pseudoEtDwich,
						pseudoEtDwich,
						p.toString()));
				}
			}
			
			// Pas de dwich
			else
			{
				pseudos.add(pseudoEtDwich);
				dwiches.add(null);
			}
		}
		
		final List<Joueur> joueurs = new ArrayList<Joueur>(pseudosEtDwiches.size());
		for (int i = 0; i < pseudos.size(); ++i)
		{
			final String pseudo = pseudos.get(i);
			final String dwich  = dwiches.get(i);
			try
			{
				final Joueur joueur = JoueursRepository.getInstance().getJoueurByPseudo(pseudo);
				if (StringUtils.isNotEmpty(dwich))
				{
					joueur.setDwich(dwich);
				}
				joueurs.add(joueur);
			}
			catch (Exception e)
			{
				LOG.error("Impossible de créer le joueur  : " + pseudo, e);
			}
		}
		
		return new TreeSet<Joueur>(joueurs);
	}
	
	/**
	 * @return la date du prochain match dans le Doodle, <code>null</code> si plus aucune partie
	 * @throws ParseException 
	 * @throws IOException 
	 */
	public Date getProchainMatch() throws IOException, ParseException
	{
		return getParser()
				.getProchainMatch();
	}
	
	public SortedSet<Joueur> getJoueursProchainMatch() throws IOException, ParseException 
	{
		final Date date = getProchainMatch();
		if (date == null)
		{
			throw new RuntimeException("La date du prochain match est inconnue");
		}
		
		return getJoueurs(date);
	}

	/**
	 * @return
	 * @throws IOException
	 * @throws ParseException
	 */
	public DoodleJSONParser getParser() throws IOException, ParseException
	{
		if (_parser == null)
		{
			_parser = new DoodleJSONParser(getData());
		}
		return _parser;
	}
	
	@Override
	public String getEncoding()
	{
		return ENCODING_UTF;
	}
	
	public String getListeDeDiffusionProchainMatch() throws IOException, ParseException
	{
		return Joueur.getListeDeDiffusion(
			getJoueursProchainMatch());
	}

	public String getListeJoueursProchainMatch() throws IOException, ParseException
	{
//		return StringUtils.join(getJoueursProchainMatch(), IOUtils.LINE_SEPARATOR);
		return Joueur.getListeNumerotee(
			getJoueursProchainMatch());
	}
	
	public static void main(final String[] args) throws Exception
	{
		final JoueursRepository rep = JoueursRepository.getInstance();
		rep.load();
		
		final DoodleConnector con = new DoodleConnector();
		
		final StringBuilder sb = new StringBuilder();

		final String date = DF.format(con.getProchainMatch());
		
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
			

		final File file = new File("./prochainMatch.txt");
		FileUtils.writeStringToFile(file, sb.toString());
		
		rep.save();
	}

	public String getListeSandwichesProchainMatch() throws IOException, ParseException
	{
		Map<Sandwich, Integer> dwiches = Sandwich.getSandwiches(
			getJoueursProchainMatch());
		
		return StringUtils.join(dwiches, IOUtils.LINE_SEPARATOR, DWICH_FORMATTER);
	}
}
