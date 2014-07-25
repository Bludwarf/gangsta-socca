package fr.bludwarf.gangstasocca;

import static fr.bludwarf.gangstasocca.DoodleElementMatcher.getElementByNomDoodle;
import static org.junit.Assert.*;

import java.io.File;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.junit.Ignore;
import org.junit.Test;

import fr.bludwarf.commons.test.TestUtils;

public class JoueurMatcherTest
{
	
	@SuppressWarnings("unchecked")
	public static List<String> getJoueurs() throws Exception
	{
		return FileUtils.readLines(new File("src/test/resources/joueurs/joueurs.txt"));
	}
	
	@SuppressWarnings("unchecked")
	public static List<String> getPseudos() throws Exception
	{
		return FileUtils.readLines(new File("src/test/resources/joueurs/pseudos.txt"));
	}

	@Test
	public void testGetJoueurByPseudo() throws Exception
	{
		final List<String> joueurs = getJoueurs();
		
		for (final String pseudoExpectedJoueur : getPseudos())
		{
			final String[] split = pseudoExpectedJoueur.split("\\t");
			final String pseudo = split[0];
			final String expectedJoueur = split[1];
			
			if (expectedJoueur.equals("ERROR"))
			{
				try
				{
					getElementByNomDoodle(pseudo, joueurs, false);
					fail("devrait planter");
				}
				catch (final Exception e)
				{
					// ok;
				}
			}
			else
			{
				assertEquals("Erreur avec le pseudo : " + pseudo, expectedJoueur, getElementByNomDoodle(pseudo, joueurs, false));
			}
		}
	}

	@Test
	@Ignore
	public void testGetJoueurByPseudoPrompt() throws Exception
	{
		TestUtils.info("La fenêtre suivante doit apparaitre une seule fois. Saisissez \"Mathieu Blandin\"");
		
		assertEquals("Mathieu BLANDIN", getElementByNomDoodle("MAthieu B", getJoueurs(), true));
		
		// en cache :
		assertEquals("Mathieu BLANDIN", getElementByNomDoodle("MAthieu B", getJoueurs(), true));
		
		TestUtils.confirm("La fenêtre n'est apparu qu'une seule fois ?");
	}

}
