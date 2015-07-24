package fr.bludwarf.gangstasocca;

import static org.junit.Assert.*;

import java.util.Date;
import java.util.Set;
import java.util.TreeSet;

import org.junit.Ignore;
import org.junit.Test;

public class MatchesRepositoryTest
{

	@Test
	public void testAdd() throws Exception
	{
		final MatchesRepository rep = MatchesRepository.getInstance();
		rep.load();
		assertEquals(2, rep.size());
		
		final Match match = new Match("doodle", new Date());
		
		Set<Joueur> joueurs = new TreeSet<Joueur>();
		joueurs.add(new Joueur("Mathieu"));
		joueurs.add(new Joueur("Martin"));
		match.setJoueurs(joueurs);
		
		rep.add(match);

		assertEquals(3, rep.size());
		
//		rep.save();
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
		
		Set<Joueur> joueurs = new TreeSet<Joueur>();
		joueurs.add(new Joueur("Mathieu"));
		joueurs.add(new Joueur("Martin"));
		match.setJoueurs(joueurs);
		
		rep.add(match);

		// ... Il ne doit pas être ajouté ...
		assertEquals(2, rep.size());
		
		// ... Et doit en plus remplacer celui existant 
		lastMatch = rep.getElements().last();
		final Set<Joueur> joueursReels = lastMatch.getJoueurs();
		assertEquals(2, joueursReels.size());
	}

}
