package fr.bludwarf.gangstasocca;

import static org.junit.Assert.*;

import java.util.Set;

import org.junit.Test;

public class JoueursRepositoryTest
{

	@Test
	public void testLoad() throws Exception
	{
		final Set<Joueur> joueurs = JoueursRepository.getInstance().load();
		assertEquals(33, joueurs.size());
	}

}
