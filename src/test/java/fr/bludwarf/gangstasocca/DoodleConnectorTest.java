package fr.bludwarf.gangstasocca;

import static org.junit.Assert.*;

import java.util.Set;

import org.junit.Ignore;
import org.junit.Test;

public class DoodleConnectorTest
{

	@Test
	@Ignore // test en live
	public void testGetJoueursProchainMatch() throws Exception
	{
		final DoodleConnector con = new DoodleConnector();
		final Match match = con.getProchainMatch();
		final Set<Joueur> joueurs = match.getJoueurs();
		
		for (final Joueur joueur : joueurs)
		{
			if (joueur.getNom().equals("XAVI"))
			{
				System.out.println(joueur.getPseudoActuel());
			}
		}
	}

}
