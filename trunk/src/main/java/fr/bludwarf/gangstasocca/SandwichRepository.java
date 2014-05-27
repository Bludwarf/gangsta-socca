package fr.bludwarf.gangstasocca;

import java.util.SortedSet;
import java.util.TreeSet;

import fr.bludwarf.commons.StringUtils;
import fr.bludwarf.commons.xml.XMLRepository;

public class SandwichRepository extends XMLRepository<SortedSet<Sandwich>, SandwichesXML>
{
	
	/** Log */
	protected static org.apache.log4j.Logger LOG = org.apache.log4j.Logger
			.getLogger(SandwichRepository.class);
	
	/** instance */
	private static SandwichRepository _instance = null;

	private SandwichRepository()
	{
	}

	/**
	 * @return l'instance de JoueursRepository
	 */
	public static final synchronized SandwichRepository getInstance()
	{
		if (_instance == null)
		{
			_instance = new SandwichRepository();
		}
		return _instance;
	}
	
	private static final String FILE = "sandwiches.xml";

	public SortedSet<Sandwich> getSandwiches() throws Exception
	{
		return getElements();
	}

	@Override
	public SandwichesXML getXmlBinder()
	{
		return new SandwichesXML();
	}

	@Override
	protected String getFile()
	{
		return FILE;
	}
	
	public SortedSet<String> getNomsSandwiches() throws Exception
	{
		final SortedSet<Sandwich> sandwiches = getSandwiches();
		SortedSet<String> noms = new TreeSet<String>();
		for (final Sandwich sandwich : sandwiches)
		{
			noms.add(sandwich.getNom());
		}
		return noms;
	}

	public Sandwich getSandwichByNomDoodle(String nomDoodle) throws Exception
	{
		final SortedSet<String> noms = getNomsSandwiches();
		
		// Le pseudo est exactement le nom complet du joueur
		if (noms.contains(nomDoodle))
		{
			LOG.debug(nomDoodle + " déjà connu");
			return getSandwichByNom(nomDoodle);
		}
		
		else
		{
			// Le pseudo est déjà connu ?
			for (final Sandwich sandwich : getSandwiches())
			{
				if (sandwich.getNomsDoodle().contains(nomDoodle))
				{
					return sandwich;
				}
			}
			
			
			final String question = String.format("A quel sandwich correspond \"%s\" ?", nomDoodle);
			final String nom = DoodleElementMatcher.prompt(question, nomDoodle, getNomsSandwiches(), true);
			LOG.debug(nomDoodle + " correspond au nom " + nom);
			
			final Sandwich sandwich;
			
			// Un sandwich a été retrouvé à partir de son nomDoodle
			if (StringUtils.isNotEmpty(nom))
			{
				sandwich = getSandwichByNom(nom);
				LOG.debug(nomDoodle + " correspond au sandwich " + sandwich);
			}
			
			// Le sandwich est inconnu => ajout dans le repo
			else
			{
				sandwich = Sandwich.createFromNomDoodle(nomDoodle, this);
				LOG.debug(nomDoodle + " correspond (prompt) au sandwich " + sandwich);
				if (sandwich != null)
				{
					getSandwiches().add(sandwich);
					LOG.debug(sandwich + " ajouté");
				}
			}
			
			// Pseudo
			sandwich.addNomDoodle(nomDoodle);
			
			return sandwich;
		}
	}

	private Sandwich getSandwichByNom(String pseudo) throws Exception
	{
		for (final Sandwich sandwich : getSandwiches())
		{
			if (sandwich.getNom().equals(pseudo))
			{
				return sandwich;
			}
		}
		return null;
	}

	public boolean containsNomDoodle(String nomDoodle) throws Exception
	{
		for (final Sandwich sandwich : getSandwiches())
		{
			if (sandwich.getNomsDoodle().contains(nomDoodle))
			{
				return true;
			}
		}
		return false;
	}
	
	@Override
	protected void addDefaultElements(SortedSet<Sandwich> elements)
	{
		elements.add(Sandwich.PAS_DE_SANDWICH);
	}

}
