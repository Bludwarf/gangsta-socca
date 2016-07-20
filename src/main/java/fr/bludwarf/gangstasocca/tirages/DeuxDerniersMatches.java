package fr.bludwarf.gangstasocca.tirages;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.lang.math.RandomUtils;
import org.apache.log4j.Logger;

import fr.bludwarf.gangstasocca.Joueur;
import fr.bludwarf.gangstasocca.Match;
import fr.bludwarf.gangstasocca.Matches;
import fr.bludwarf.gangstasocca.MatchesRepository;

public class DeuxDerniersMatches implements TirageAuSort
{
	protected static Logger LOG = Logger
			.getLogger(DeuxDerniersMatches.class);
	private List<Joueur> selection;
	private List<Joueur> remplaçants;

	public List<Joueur> tirer(int nb, Collection<Joueur> joueurs) throws Exception
	{		
		// Deux derniers matches
		final Matches matches = MatchesRepository.getInstance().getMatches();
		final Match last = matches.last();
		final List<Match> derniersMatches = new ArrayList<Match>();
		// FIXME : il faut prendre les matches précédent le prochain qui n'a pas encore été joué
		derniersMatches.add(matches.matchPrécédent(last));
		derniersMatches.add(last);
		
		// Tri
		final List<Joueur> list = new ArrayList<Joueur>(joueurs.size());
		list.addAll(joueurs);
		Collections.sort(list, new Comparator<Joueur>() {
			public int compare(Joueur j1, Joueur j2)
			{
				final int n1 = j1.getMatches(derniersMatches, false).size();
				final int n2 = j2.getMatches(derniersMatches, false).size();
				LOG.debug(String.format("%s <? %s : %s <? %s", j1, j2, n1, n2));
				
				// Nombre de matches inscrits
				if (n1 != n2) return n1 - n2;
				
				// Tri aléatoire != 0
				return Math.round(RandomUtils.nextInt(1)) * 2 - 1;
			}
		});
		
		// Gagnants
		this.selection = new ArrayList<Joueur>(list.subList(0, nb));
		
		// Perdants
		this.remplaçants = new ArrayList<Joueur>(list.subList(nb, list.size()));
		
		return selection;
	}

	public List<Joueur> getJoueurs() throws Exception
	{
		if (this.selection == null) throw new Exception("Tirage au sort non effectué via #tirer(int, Set<Joueur>)");
		return this.selection;
	}

	public List<Joueur> getRemplaçants() throws Exception
	{
		if (this.remplaçants == null) throw new Exception("Tirage au sort non effectué via #tirer(int, Set<Joueur>)");
		return this.remplaçants;
	}

}
