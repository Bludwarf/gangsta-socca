package fr.bludwarf.gangstasocca.stats;

import static fr.bludwarf.gangstasocca.MatchesTest.getMatches;
import static org.junit.Assert.*;

import org.junit.Test;

import fr.bludwarf.gangstasocca.Joueur;
import fr.bludwarf.gangstasocca.MatchesTest;

public class StatsJoueurTest
{
	public static Joueur MICKAEL = MatchesTest.MICKAEL;
	public static Joueur EDDY	 = MatchesTest.EDDY;

	@Test
	public void testGetElo() throws Exception
	{
		final Stats stats = new Stats(getMatches()); 
		assertEquals(1220, Math.round(stats.getEloActuel(MICKAEL)));
	}

}
