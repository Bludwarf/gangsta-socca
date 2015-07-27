package fr.bludwarf.gangstasocca;

import static org.junit.Assert.*;

import java.io.File;
import java.util.Date;
import java.util.Set;
import java.util.TreeSet;

import org.junit.Ignore;
import org.junit.Test;

import fr.bludwarf.commons.xml.ElementXML;
import fr.bludwarf.gangstasocca.stats.Stats;

public class MatchesRepositoryTest
{

	@Test
	public void testAdd() throws Exception
	{
		final MatchesRepository rep = MatchesRepository.getInstance();
		rep.load();
		assertEquals(2, rep.size());
		
		final Match match = new Match("doodle", new Date());
		
		match.addJoueur("Mathieu");
		match.addJoueur("Martin");
		
		rep.add(match);

		assertEquals(3, rep.size());
		
//		rep.save();
		
		rep.remove(match);
	}

	@Test
	public void testAddSame() throws Exception
	{
		final MatchesRepository rep = MatchesRepository.getInstance();
		rep.load();
		assertEquals(2, rep.size());
		Match lastMatch = rep.getElements().last();
		
		// On ajoute un matche qui existe déjà
		final Match match = new Match("doodle", lastMatch.getDate()); // dernier match dans le XML

		match.addJoueur("Mathieu");
		match.addJoueur("Martin");
		
		rep.add(match);

		// ... Il ne doit pas être ajouté ...
		assertEquals(2, rep.size());
		
		// ... Et doit en plus remplacer celui existant 
		lastMatch = rep.getElements().last();
		final Set<Joueur> joueursReels = lastMatch.getJoueurs();
		assertEquals(2, joueursReels.size());
		
		rep.remove(match);
	}

	/**
	 * Un joueur qui change de pseudo doit garder son ELO (bug #2)
	 */
	@Test
	public void testEloPseudoDiff() throws Exception
	{
		final File file = new File("src/test/resources/matches/elo-pseudo-diff.xml");
		final Matches matches = new Matches();
		matches.load(file);
		assertEquals(2, matches.nb);
		
		// Dernier match
		Match first = null;
		Match last = null;
		for (final Match match : matches)
		{
			if (first == null || match.compareTo(first) < 0) first = match;
			if (last == null || match.compareTo(last) > 0) last = match;
		}
		assertNotNull(first);
		assertNotNull(last);
		
		// Dans les 2 matches c'est toujours Babs même s'il a changé de pseudo
		final Joueur j1 = first.getJoueur("Babs");
		final Joueur j2 = last.getJoueur("Rivaldo");
		assertTrue("Babs a changé de pseudo mais c'est bien lui dans les deux matches", j1 == j2);
		
		// stats
		final Stats stats = new Stats(matches);
		Joueur joueur = JoueursRepository.getInstance().getJoueurByPseudo("Babs");
		assertEquals(1300, Math.round(stats.getEloAvant(last, joueur)));
	}

}
