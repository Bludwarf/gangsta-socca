package fr.bludwarf.gangstasocca;

import java.util.ArrayList;
import java.util.SortedSet;
import java.util.TreeSet;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import fr.bludwarf.commons.xml.ElementXML;

@Root(name="sandwiches")
public class SandwichesXML extends ElementXML<SortedSet<Sandwich>>
{
	
	@ElementList(inline=true, entry="sandwich")
	ArrayList<SandwichXML> sandwiches;

	private static class SandwichXML extends ElementXML<Sandwich>
	{
		
		@Attribute
		String nom;
		
		@ElementList(entry="doodle", inline=true, required=false)
		ArrayList<String> nomsDoodle;

		@Override
		public void fromObject(Sandwich obj) throws Exception
		{
			nom = obj.getNom();
			nomsDoodle = new ArrayList<String>(obj.getNomsDoodle());
		}

		@Override
		public Sandwich toObject() throws Exception
		{
			final Sandwich obj = new Sandwich(nom);
			
			if (nomsDoodle != null)
			{
				obj.addNomsDoodle(nomsDoodle);
			}
			
			return obj;
		}
		
	}
	

	@Override
	public void fromObject(SortedSet<Sandwich> obj) throws Exception
	{
		sandwiches = new ArrayList<SandwichesXML.SandwichXML>();
		
		for (final Sandwich sandwich : obj)
		{
			SandwichXML sandwichXML = new SandwichXML();
			sandwichXML.fromObject(sandwich);
			sandwiches.add(sandwichXML);
		}
	}

	@Override
	public SortedSet<Sandwich> toObject() throws Exception
	{
		final SortedSet<Sandwich> obj = new TreeSet<Sandwich>();
	
		if (sandwiches != null)
		{
			for (final SandwichXML sandwichXML : sandwiches)
			{
				obj.add(sandwichXML.toObject());
			}
		}
		
		return obj;
	}

}
