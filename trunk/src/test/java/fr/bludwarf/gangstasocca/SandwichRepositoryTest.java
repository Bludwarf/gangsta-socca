package fr.bludwarf.gangstasocca;

import static org.junit.Assert.*;

import java.util.Set;
import java.util.SortedSet;

import org.junit.Test;

import fr.bludwarf.commons.test.TestUtils;

public class SandwichRepositoryTest
{

	@Test
	public void testGetSandwiches() throws Exception
	{
		final SortedSet<Sandwich> sandwiches = SandwichRepository.getInstance().getSandwiches();
	}

//	@Test
//	public void testGetByNom() throws Exception
//	{
//		final SandwichRepository rep = SandwichRepository.getInstance();
//		
//		TestUtils.info("La fenêtre suivante va vous demander à quel sandwich correspond le club, vérifier que la liste contient (aucun sandwich) ");
//		rep.getSandwichByNomDoodle("club");
//		rep.getSandwichByNomDoodle("club");
//		rep.getSandwichByNomDoodle("jambon");
//		rep.getSandwichByNomDoodle("rien");
//	}

}
