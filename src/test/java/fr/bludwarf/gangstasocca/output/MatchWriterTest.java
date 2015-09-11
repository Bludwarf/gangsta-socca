package fr.bludwarf.gangstasocca.output;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Properties;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.commons.configuration.plist.ParseException;
import org.junit.Test;

import fr.bludwarf.commons.io.FileUtils;
import fr.bludwarf.commons.test.TestUtils;
import fr.bludwarf.commons.web.URLUtils;
import fr.bludwarf.gangstasocca.DoodleConnector;
import fr.bludwarf.gangstasocca.Joueur;
import fr.bludwarf.gangstasocca.Match;
import fr.bludwarf.gangstasocca.Sandwich;

public class MatchWriterTest
{

	private static final Sandwich SAND_CLUB = new Sandwich("Club");
	private static final Sandwich SAND_JAM = new Sandwich("Jambon");
	private static final Sandwich SAND_POUL = new Sandwich("Poulet");
	private static final Sandwich SAND_THON = new Sandwich("Thon");

	@Test
	public void testWriteProchainMatchHTMLDoodleConnectorPropertiesFile() throws Exception
	{
		// match
		final Calendar cal = Calendar.getInstance();
		cal.set(2014, 2, 7, 12, 15);
		final Match match = new Match("http://www.doodle.com/uvbbrna677dfbw7e", cal.getTime());
		
		// Joueurs
		final Joueur j1 = new Joueur("Mathieu Lavigne");
		j1.addPseudo("Matt La Rage"); // on prend le dernier pseudo
		j1.setEmail("mathieu.lavigne@mail.com");
		match.add(j1);
		final Joueur j2 = new Joueur("Babacar");
		j2.addPseudo("King Babs"); // on prend le dernier pseudo
		j2.addPseudo("Babs"); // on prend le dernier pseudo
		j2.setEmail("babacar.ndiaye@mail.com");
		match.add(j2);
		
		// Sandwiches
		j1.setSandwich(SAND_THON);
		j2.setSandwich(SAND_POUL);
		
		// Template original
		final File template = new File("src/test/resources/prochainMatch.html.template");
		
		final File outFile = MatchWriter.writeProchainMatchHTML(match, template);
		final File outFileExpected = new File("src/test/resources/prochainMatch.html.expected");
		
		// Sortie
		assertEquals(new File("src/test/resources/prochainMatch.html").getPath(), outFile.getPath());
		
		// Contenu
		TestUtils.assertEqualsXML(outFileExpected, outFile, true);
	}

}
