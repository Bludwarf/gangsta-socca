package fr.bludwarf.gangstasocca;

import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

import fr.bludwarf.commons.StringUtils;
import fr.bludwarf.commons.xml.XMLRepository;

public class JoueursRepository extends XMLRepository<SortedSet<Joueur>, JoueursXML>
{
	
	/** Log */
	protected static org.apache.log4j.Logger LOG = org.apache.log4j.Logger
			.getLogger(JoueursRepository.class);
	
	/** instance */
	private static JoueursRepository _instance = null;

	private JoueursRepository()
	{
	}

	/**
	 * @return l'instance de JoueursRepository
	 */
	public static final synchronized JoueursRepository getInstance()
	{
		if (_instance == null)
		{
			_instance = new JoueursRepository();
		}
		return _instance;
	}
	
	private static final String FILE = "joueurs.xml";

	public SortedSet<Joueur> getJoueurs() throws Exception
	{
		return getElements();
	}

	@Override
	public JoueursXML getXmlBinder()
	{
		return new JoueursXML();
	}

	@Override
	protected String getFile()
	{
		return FILE;
	}
	
	public SortedSet<String> getNomsJoueurs() throws Exception
	{
		final SortedSet<Joueur> joueurs = getJoueurs();
		SortedSet<String> noms = new TreeSet<String>();
		for (final Joueur joueur : joueurs)
		{
			noms.add(joueur.getNom());
		}
		return noms;
	}

//	public Joueur getJoueurByPseudo(String pseudo) throws Exception
//	{
//		final List<String> noms = getNomsJoueurs();
//		
//		// Le pseudo est exactement le nom complet du joueur
//		if (noms.contains(pseudo))
//		{
//			LOG.debug(pseudo + " déjà connu");
//			return getJoueurByNom(pseudo);
//		}
//		else
//		{
//			final String nom = JoueurMatcher.getJoueurByPseudo(pseudo, noms, true);
//			LOG.debug(pseudo + " correspond au nom " + nom);
//			
//			// Un joueur a été retrouvé à partir de son pseudo
//			if (StringUtils.isNotEmpty(nom))
//			{
//				final Joueur joueurByNom = getJoueurByNom(nom);
//				LOG.debug(pseudo + " correspond au joueur " + joueurByNom);
//				joueurByNom.addPseudo(pseudo);
//				return joueurByNom;
//			}
//			
//			// Le joueur est inconnu
//			else
//			{
//				final Joueur newJoueur = Joueur.createFromPseudo(pseudo);
//				LOG.debug(pseudo + " correspond (prompt) au joueur " + newJoueur);
//				if (newJoueur != null)
//				{
//					getJoueurs().add(newJoueur);
//					LOG.debug(newJoueur + " ajouté");
//				}
//				return newJoueur;
//			}
//		}
//	}

	public Joueur getJoueurByPseudo(String pseudo) throws Exception
	{
		final SortedSet<String> noms = getNomsJoueurs();
		
		// Le pseudo est exactement le nom complet du joueur
		if (noms.contains(pseudo))
		{
			LOG.debug(pseudo + " déjà connu");
			return getJoueurByNom(pseudo);
		}
		
		else
		{
			// Le pseudo est déjà connu ?
			for (final Joueur joueur : getJoueurs())
			{
				if (joueur.getPseudos().contains(pseudo))
				{
					return joueur;
				}
			}
			
			
			final String nom = DoodleElementMatcher.prompt(pseudo, getNomsJoueurs(), true);
			LOG.debug(pseudo + " correspond au nom " + nom);
			
			final Joueur joueur;
			
			// Un joueur a été retrouvé à partir de son pseudo
			if (StringUtils.isNotEmpty(nom))
			{
				joueur = getJoueurByNom(nom);
				LOG.debug(pseudo + " correspond au joueur " + joueur);
			}
			
			// Le joueur est inconnu => ajout dans le repo
			else
			{
				joueur = Joueur.createFromPseudo(pseudo, this);
				LOG.debug(pseudo + " correspond (prompt) au joueur " + joueur);
				if (joueur != null)
				{
					getJoueurs().add(joueur);
					LOG.debug(joueur + " ajouté");
				}
			}
			
			// Pseudo
			joueur.addPseudo(pseudo);
			
			return joueur;
		}
	}

	private Joueur getJoueurByNom(String pseudo) throws Exception
	{
		for (final Joueur joueur : getJoueurs())
		{
			if (joueur.getNom().equals(pseudo))
			{
				return joueur;
			}
		}
		return null;
	}

	public boolean containsPseudo(String pseudo) throws Exception
	{
		for (final Joueur joueur : getJoueurs())
		{
			if (joueur.getPseudos().contains(pseudo))
			{
				return true;
			}
		}
		return false;
	}
	
	public void save() throws Exception
	{
		super.save();
		SandwichRepository.getInstance().save();
	}

}
