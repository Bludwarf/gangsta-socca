package fr.bludwarf.gangstasocca.stats;

import java.util.Map;

import fr.bludwarf.gangstasocca.Joueur;
import fr.bludwarf.gangstasocca.Match;

public class StatsMatch
{
	private Match match;
	private Map<Joueur, StatsJoueurMatch> statsJoueurs;

	public StatsMatch(Match match)
	{
		this.match = match;
	}
	
	public StatsJoueurMatch getStats(Joueur joueur)
	{
		if (!statsJoueurs.containsKey(joueur))
		{
			
		}
		return statsJoueurs.get(joueur);
	}
}
