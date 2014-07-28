package fr.bludwarf.gangstasocca;

import static org.junit.Assert.*;

import java.io.File;
import java.util.Date;
import java.util.Set;
import java.util.TreeSet;

import org.junit.Ignore;
import org.junit.Test;

import fr.bludwarf.commons.xml.ElementXML;

public class MatchesTest
{
	public static Joueur MICKAEL = new Joueur("Mickael");
	public static Joueur EDDY	 = new Joueur("Eddy Z");

	@Test
	public void testXML() throws Exception
	{
		final Matches matchesXML = getMatches();
		assertEquals(2, matchesXML.nb);
		
		
		final Match m1 = matchesXML.matches.get(0);
		final Match m2 = matchesXML.matches.get(1);
		
		assertEquals(matchesXML.debut, m1.getDate());
		assertEquals(matchesXML.fin,   m2.getDate());
		
		
		
		// Joueurs
		assertEquals(10, m1.getJoueurs().size());
		assertEquals(10, m2.getJoueurs().size());
		
		assertEquals(true,  MICKAEL.joueEnRouge(m1)); 	// Mickael en rouge le 17/07
		assertEquals(false, MICKAEL.joueEnRouge(m2)); 	// Mickael en bleu  le 22/07
	}

	/**
	 * @return
	 * @throws Exception
	 */
	public static Matches getMatches() throws Exception
	{
		final Matches matchesXML = new Matches();
		matchesXML.load(new File("src/test/resources/matches.xml"));
		return matchesXML;
	}

	@Test
	public void testGetMatchesJoueur() throws Exception
	{
		final Matches matchesXML = getMatches();
		
		assertEquals(2, matchesXML.getMatches(MICKAEL).size());
		assertEquals(1, matchesXML.getMatches(EDDY).size());
	}

	@Test
	public void testFromObjectTreeSetOfMatch() throws Exception
	{
		final Matches matchesXML = new Matches();
		TreeSet<Match> matches = new TreeSet<Match>();
		final Match match = new Match("doodle", new Date());
		
		Set<Joueur> joueurs = new TreeSet<Joueur>();
		joueurs.add(new Joueur("Mathieu"));
		joueurs.add(new Joueur("Martin"));
		match.setJoueurs(joueurs);
		matches.add(match);
		
		matchesXML.fromObject(matches);
		final File file = new File("src/test/resources/xml/matches.out.xml");
		matchesXML.save(file);
	}

	@Test
	@Ignore
	public void testToObject()
	{
		fail("Not yet implemented");
	}

}
