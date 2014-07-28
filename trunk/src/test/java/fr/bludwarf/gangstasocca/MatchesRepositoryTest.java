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
	@Ignore
	public void testAdd() throws Exception
	{
		final MatchesRepository rep = MatchesRepository.getInstance();
		rep.load();
		
		final Match match = new Match("doodle", new Date());
		
		Set<Joueur> joueurs = new TreeSet<Joueur>();
		joueurs.add(new Joueur("Mathieu"));
		joueurs.add(new Joueur("Martin"));
		match.setJoueurs(joueurs);
		
		rep.add(match);
		
		rep.save();
	}

}
