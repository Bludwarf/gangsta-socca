package fr.bludwarf.gangstasocca;

import java.util.TreeSet;

import fr.bludwarf.commons.xml.XMLRepository;
import fr.bludwarf.gangstasocca.exceptions.MatchDéjàJoué;
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
		// On remplace le match existant s'il n'a pas été joué
		if (getElements().contains(match)) {
			final Match ancienneVersion = getMatchSauvegardé(match);
			
			if (ancienneVersion.aÉtéJoué()) throw new MatchDéjàJoué("Match déjà joué (impossible à remplacer) : " + match);
			
			// Avant de le supprimer on fusionner les informations à conserver
			match.fusionnerDepuis(ancienneVersion);
			
			// Puis on le supprime du repo
			getElements().remove(match);
		}
		getElements().add(match);
		// FIXME : màj stats
	}

	private Match getMatchSauvegardé(Match match) throws Exception
	{
		for (final Match match_i : getElements())
		{
			if (match_i.equals(match)) return match_i;
		}
		return null;
	}

	public void remove(Match match) throws Exception
	{
		getElements().remove(match);
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
