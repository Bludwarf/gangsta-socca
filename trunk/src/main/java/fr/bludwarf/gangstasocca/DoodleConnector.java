package fr.bludwarf.gangstasocca;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;
import java.util.SortedSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.configuration.plist.ParseException;
import org.apache.commons.io.IOUtils;

import fr.bludwarf.commons.StringUtils;
import fr.bludwarf.commons.formatters.MapFormatter;
import fr.bludwarf.commons.web.WebConnector;
import fr.bludwarf.gangstasocca.json.DoodleJSONParser;
import fr.bludwarf.gangstasocca.output.MatchWriter;

public class DoodleConnector extends WebConnector
{
	
	/** Log */
	protected static org.apache.log4j.Logger LOG = org.apache.log4j.Logger
			.getLogger(DoodleConnector.class);
	public static String URL = "http://www.doodle.com/uvbbrna677dfbw7e";
	public static String EMAIL_ORGANISATEUR = "mathieu.lavigne@capgemini.com";
	
	public static MapFormatter<Sandwich, Integer> DWICH_FORMATTER = new MapFormatter<Sandwich, Integer>() {

		public String format(Sandwich dwich, Integer n, int i)
		{
			return String.format("%s x %s", n, dwich);
		}
	};
	
	private URL _url;
	private DoodleJSONParser _parser;
	private Match _prochainMatch;

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
	
	/**
	 * @return la date du prochain match dans le Doodle, <code>null</code> si plus aucune partie
	 * @throws ParseException 
	 * @throws IOException 
	 */
	public Match getProchainMatch() throws IOException, ParseException
	{
		if (_prochainMatch == null)
		{
			_prochainMatch = getParser()
					.getProchainMatch();
			
			_prochainMatch.setJoueurs(getParser().getJoueurs(_prochainMatch));
		}
		
		return _prochainMatch;
	}
	
	public SortedSet<Joueur> getJoueursProchainMatch() throws IOException, ParseException 
	{
		final Match match = getProchainMatch();
		if (match == null)
		{
			throw new RuntimeException("La date du prochain match est inconnue");
		}
		
		return match.getJoueurs();
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
			_parser = new DoodleJSONParser(getURL(), getData());
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
		
//		// Texte
//		final File file = new File("./prochainMatch.txt");
//		MatchWriter.writeProchainMatch(con, file);
		// HTML
		final File file = new File("./prochainMatch.html.template");
		MatchWriter.writeProchainMatchHTML(con, file);
		
		rep.save();
	}

	public String getListeSandwichesProchainMatch() throws IOException, ParseException
	{
		Map<Sandwich, Integer> dwiches = Sandwich.getSandwiches(
			getJoueursProchainMatch());
		
		return StringUtils.join(dwiches, IOUtils.LINE_SEPARATOR, DWICH_FORMATTER);
	}
}
