package fr.bludwarf.gangstasocca;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.apache.commons.configuration.plist.ParseException;
import org.apache.commons.io.IOUtils;

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
//	public static String URL = "http://www.doodle.com/uvbbrna677dfbw7e";
	public static String EMAIL_ORGANISATEUR = "mathieu.lavigne@mail.com";
	
	public static MapFormatter<Sandwich, Integer> DWICH_FORMATTER = new MapFormatter<Sandwich, Integer>() {

		public String format(Sandwich dwich, Integer n, int i)
		{
			return String.format("%s x %s", n, dwich);
		}
	};
	
	private URL _url;
	private DoodleJSONParser _parser;
	private Match _prochainMatch;

	/**
	 * Par défaut l'URL de joueurs.xml
	 * @throws Exception 
	 */
	public DoodleConnector() throws Exception
	{
		super();
		_url = new URL(JoueursRepository.getInstance().getDoodleURL());
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
		// Cookie pour rester dans l'ancienne version de Doodle
		final Map<String, String> cookies = new HashMap<String, String>();
		cookies.put("d-betaCode", "true");
		return get(getURL(), cookies);
	}
	
//	/**
//	 * @return valeur de l'objet JSON <code>doodleJS.data.poll</code> dans la page web du Doodle
//	 * @throws IOException
//	 * @throws ParseException 
//	 */
//	public final String getData() throws IOException, ParseException
//	{
//		final Pattern p = Pattern.compile("doodleJS.data.poll = (\\{.+\\});");
//		final Matcher m = p.matcher(getDoodlePage());
//		
//		if (m.find())
//		{
//			return m.group(1);
//		}
//		else
//		{
//			throw new ParseException("Impossible de trouver la valeur de la variable JSON \"doodleJS.data.poll\" sur la page Doodle : " + _url);
//		}
//	}
	
	/**
	 * @return valeur de l'objet JSON <code>doodleJS.data.poll</code> dans la page web du Doodle
	 * @throws IOException
	 * @throws ParseException 
	 * @since 26 févr. 2015
	 */
	public final String getData() throws IOException, ParseException
	{
		final String open  = GangstaSoccaProperties.getInstance().getString("doodle.data.pattern.open");
		final String close = GangstaSoccaProperties.getInstance().getString("doodle.data.pattern.close");
//		FileUtils.writeStringToFile(new File("doodle-2015.html"), getDoodlePage());
		final String html = getDoodlePage();
		final String data = StringUtils.substringBetween(html, open, close);
		
		if (StringUtils.isNotEmpty(data))
		{
			LOG.debug("doodleJS.data.poll = " + data);
			return data;
		}
		else
		{
			org.apache.commons.io.FileUtils.writeStringToFile(new File("doodle.html"), html);
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
	
	public Set<Joueur> getJoueursProchainMatch() throws Exception 
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
	
	public String getListeDeDiffusionProchainMatch() throws Exception
	{
		return Joueur.getListeDeDiffusion(
			getJoueursProchainMatch());
	}

	public String getListeJoueursProchainMatch() throws Exception
	{
//		return StringUtils.join(getJoueursProchainMatch(), IOUtils.LINE_SEPARATOR);
		return Joueur.getListeNumerotee(
			getJoueursProchainMatch());
	}

	public String getListeSandwichesProchainMatch() throws Exception
	{
		Map<Sandwich, Integer> dwiches = Sandwich.getSandwiches(
			getJoueursProchainMatch());
		
		return StringUtils.join(dwiches, IOUtils.LINE_SEPARATOR, DWICH_FORMATTER);
	}
}
