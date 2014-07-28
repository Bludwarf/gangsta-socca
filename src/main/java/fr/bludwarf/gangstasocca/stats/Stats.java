package fr.bludwarf.gangstasocca.stats;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.apache.log4j.Logger;

import fr.bludwarf.gangstasocca.Joueur;
import fr.bludwarf.gangstasocca.Match;
import fr.bludwarf.gangstasocca.Matches;

public class Stats
{
	protected static Logger LOG = Logger.getLogger(Stats.class);
	
	public static final int START_ELO = 1200;
	public static final int K = 40;
	
	private Matches matches;
	
	private Map<Match, Map<Joueur, Double>> elosAprès = new HashMap<Match, Map<Joueur,Double>>();

	public Stats(Matches matches)
	{
		this.matches = matches;
	}
	
	public int getEloActuel(Joueur joueur)
	{
		final Date t0 = new Date();
		final int elo = (int) Math.round(getEloAprès(matches.last(), joueur));
		final Date t1 = new Date();
		
		final TreeSet<Match> matchesJoueur = joueur.getMatches(matches);
		LOG.info(String.format("ELO de %s : %s (au %s sur %s matche(s))", joueur, elo, matchesJoueur.last().getDateStr(), matchesJoueur.size()));
		LOG.info(String.format("Stats générées en %s ms", t1.getTime() - t0.getTime()));
		
		return elo;
	}

	// FIXME : si c'est le premier match du joueur -> 1200
	/**
	 * @param match
	 * @param joueur
	 * @return 1200 si match <code>null</code> (et pas nul ^^)
	 */
	public double getEloAprès(Match match, Joueur joueur)
	{
		final Double savedElo = load(match, joueur);
		if (savedElo != null) return savedElo;
		final double elo = getEloAvant(match, joueur) + K * G(match, joueur) * (W(match, joueur) - We(match, joueur));
		LOG.info(String.format("Après le %s : elo de %s = %s", match, joueur, elo));
		save(match, joueur, elo);
		return elo;
	}

	public void setEloAprès(Match match, Joueur joueur, double elo)
	{
		save(match, joueur, elo);
	}

	private void save(Match match, Joueur joueur, double elo)
	{
		if (!elosAprès.containsKey(match))
		{
			elosAprès.put(match, new HashMap<Joueur, Double>(12));
		}
		final Map<Joueur, Double> elosMatch = elosAprès.get(match);
		elosMatch.put(joueur, elo);
	}

	private Double load(Match match, Joueur joueur)
	{
		if (!elosAprès.containsKey(match))
		{
			return null;
		}
		final Map<Joueur, Double> elosMatch = elosAprès.get(match);
		return elosMatch.get(joueur);
	}

	public double getEloAvant(Match match, Joueur joueur)
	{
		final Match matchPre = joueur.matchPrécédent(match, matches);
		if (matchPre == null) return START_ELO;
		return getEloAprès(matchPre, joueur);
	}

	private double W(Match match, Joueur joueur)
	{
		return match.victoire(joueur);
	}

	private double We(Match match, Joueur joueur)
	{
		double dr = getEloEquipe(match, joueur, true) - getEloEquipe(match, joueur, false);
		return 1 / (Math.pow(10, -dr/400) + 1);
	}

	// Todo : résultat en cache
	private double getEloEquipe(Match match, Joueur joueur, boolean ami)
	{
		double eloMoy = 0;
		
		final Set<Joueur> joueurs = ami ? joueur.getEquipe(match) : joueur.getEquipeAdverse(match);
		for (final Joueur joueurI : joueurs)
		{
			eloMoy += getEloAvant(match, joueurI);
		}
		
		eloMoy /= joueurs.size();
		LOG.info(String.format("ELO Equipe ami=%b, Match[%s] = %s", ami, match, eloMoy));
		return eloMoy;
	}

	private Match matchPrécédent(Match match)
	{
		return matches.matchPrécédent(match);
	}

	private double G(Match match, Joueur joueur)
	{
		final int n = match.buts(joueur);
		if (n >= 4)
		{
			return 1 + 3/4 + (n - 3)/8;
		}
		else if (n >= 3)
		{
			return 1 + 3/4;
		}
		else if (n >= 2)
		{
			return 1 + 1/2;
		}
		else
		{
			return 1;
		}
	}
}
