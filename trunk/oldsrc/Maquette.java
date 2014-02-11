package fr.bludwarf.gangstasocca;

import java.io.File;
import java.io.IOException;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.FileUtils;
import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Test;

import fr.bludwarf.commons.StringUtils;
import fr.bludwarf.gangstasocca.json.DoodleJSONParser;

public class Maquette
{

	@Test
	public void test() throws Exception
	{
		final PrintStream o = System.out;
		o.println(StringUtils.getLevenshteinMatching("Nico", "Nicolas"));
		o.println(StringUtils.getLevenshteinMatching("M", "Nicolas"));
		o.println(StringUtils.getLevenshteinMatching("Nico", "Beitz"));
		o.println(StringUtils.getLevenshteinMatching("M", "Beitz"));
		o.println();
		o.println(StringUtils.getLevenshteinMatching("Nico", "Nicolas"));
		o.println(StringUtils.getLevenshteinMatching("M", "Nicolas"));
		o.println(StringUtils.getLevenshteinMatching("Nico", "Murigneux"));
		o.println(StringUtils.getLevenshteinMatching("M", "Murigneux"));
		o.println();
		o.println();
		o.println(StringUtils.getLevenshteinMatching("Babs", "Régis"));
		o.println(StringUtils.getLevenshteinMatching("Babs", "Malgras"));
		o.println();
		o.println(StringUtils.getLevenshteinMatching("Babs", "Babacar"));
		o.println(StringUtils.getLevenshteinMatching("Babs", "NDIAYE"));
	}

	@Test
	public void test2() throws Exception
	{
		final PrintStream o = System.out;
		o.println(StringUtils.getLevenshteinDistance("Nico", "Nicolas"));
		o.println(StringUtils.getLevenshteinDistance("M", "Nicolas"));
		o.println(StringUtils.getLevenshteinDistance("Nico", "Beitz"));
		o.println(StringUtils.getLevenshteinDistance("M", "Beitz"));
		o.println();
		o.println(StringUtils.getLevenshteinDistance("Nico", "Nicolas"));
		o.println(StringUtils.getLevenshteinDistance("M", "Nicolas"));
		o.println(StringUtils.getLevenshteinDistance("Nico", "Murigneux"));
		o.println(StringUtils.getLevenshteinDistance("M", "Murigneux"));
		o.println();
		o.println();
		o.println(StringUtils.getLevenshteinDistance("Babs", "Régis"));
		o.println(StringUtils.getLevenshteinDistance("Babs", "Malgras"));
		o.println();
		o.println(StringUtils.getLevenshteinDistance("Babs", "Babacar"));
		o.println(StringUtils.getLevenshteinDistance("Babs", "NDIAYE"));
	}
	
	@Test
	public void testHTTP() throws Exception
	{
		final JoueursRepository rep = JoueursRepository.getInstance();
		rep.load();
		
		final DoodleConnector con = new DoodleConnector();
		System.out.println(con.getListeDeDiffusionProchainMatch());
		System.out.println(con.getListeJoueursProchainMatch());
		
		rep.save();
	}
	
	@Test
	public void testRepo() throws Exception
	{
//		final List<String> noms = JoueursRepository.getInstance().getNomsJoueurs();
//		System.out.println(noms);
////		System.out.println(JoueurMatcher.prompt("Mika", noms));
//		System.out.println(JoueurMatcher.getJoueurByPseudo("Mika", noms));
////		JoueursRepository.getInstance().save();
		
		final JoueursRepository rep = JoueursRepository.getInstance();
		System.out.println(rep.getJoueurByPseudo("Mika"));
		System.out.println(rep.getJoueurByPseudo("Mika"));
		System.out.println(rep.getJoueurByPseudo("Mika"));
		System.out.println(rep.getJoueurByPseudo("Dany K"));
		System.out.println(rep.getJoueurByPseudo("Dany K"));
		System.out.println(rep.getJoueurByPseudo("JPB"));
		System.out.println(rep.getJoueurs());
		
		rep.save();
	}

	@Test
	public void testSimple() throws Exception
	{
		final PrintStream o = System.out;
		o.println(StringUtils.getCommonPrefix(new String[]{"Nico M", "Nicolas"}));
		o.println(StringUtils.getCommonPrefix(new String[]{"Nico M", "Nicolas"}));
		
		o.println(StringUtils.getCommonPrefix(new String[]{"M", "BEITZ"}));
		o.println(StringUtils.getCommonPrefix(new String[]{"M", "Murigneux"}));
	}

}
