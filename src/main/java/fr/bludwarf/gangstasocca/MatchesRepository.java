package fr.bludwarf.gangstasocca;

import java.util.TreeSet;

import fr.bludwarf.commons.xml.XMLRepository;
import fr.bludwarf.gangstasocca.stats.Stats;

public class MatchesRepository extends XMLRepository<TreeSet<Match>, Matches>
{
	
	/** instance */
	private static MatchesRepository _instance = null;
	private Stats stats;
	
	// FIXME : pour les stats uniquement et pour le XML mais pas pour le métier !?
	private Matches matches;

	private MatchesRepository()
	{
	}

	/**
	 * @return l'instance de MatchesRepository
	 */
	public static final synchronized MatchesRepository getInstance()
	{
		if (_instance == null)
		{
			_instance = new MatchesRepository();
		}
		return _instance;
	}

	@Override
	public Matches getXmlBinder()
	{
		return new Matches();
	}

	@Override
	protected String getFile()
	{
		return "matches.xml";
	}

	public void add(Match match) throws Exception
	{
		// On remplace le match existant 
		if (getElements().contains(match)) getElements().remove(match);
		getElements().add(match);
		// FIXME : màj stats
	}

	public Stats getStats() throws Exception
	{
		if (stats == null)
		{
			stats = new Stats(getMatches());
		}
		return stats;
	}

	public Matches getMatches() throws Exception
	{
		if (matches == null)
		{
			matches = new Matches();
			matches.fromObject(getElements());;
		}
		return matches;
	}

	public int size() throws Exception
	{
		int n = 0;
		for (final Match match : getElements())
		{
			++n;
		}
		return n;
	}

}
