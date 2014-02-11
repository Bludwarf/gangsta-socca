package fr.bludwarf.gangstasocca;

import java.util.Collection;
import java.util.Set;
import java.util.TreeSet;

import javax.swing.JOptionPane;

import org.apache.commons.io.IOUtils;

import fr.bludwarf.commons.StringUtils;
import fr.bludwarf.commons.formatters.CollectionFormatter;
import fr.bludwarf.gangstasocca.exceptions.PseudoDejaUtiliseException;

public class Joueur implements Comparable<Joueur>
{
	
	/** Log */
	protected static org.apache.log4j.Logger LOG = org.apache.log4j.Logger
			.getLogger(Joueur.class);
	
	final static CollectionFormatter<Joueur> FORMAT = new CollectionFormatter<Joueur>() {
		public String format(Joueur e, int i)
		{
			return e.getEmail();
		}
	};
	final static CollectionFormatter<Joueur> LIST_FORMAT = new CollectionFormatter<Joueur>() {
		public String format(Joueur e, int i)
		{
			final Sandwich dwich = e.getSandwich();
			final int maxLength = getMaxLengthNoms(false);
			if (Sandwich.isSandwich(dwich))
			{
				return String.format("%s.\t%-" + maxLength + "s (%s)", i + 1, e.getNom(), dwich.getNom());
			}
			else
			{
				return String.format("%s.\t%-" + maxLength + "s", i + 1, e.getNom());
			}
		}
	};
	
	private static int _maxLengthNoms = -1;
	
	private Set<String> _pseudos;
	private String _nom;
	private Sandwich _sandwich;
	private String _email;
	
	public Joueur(String nom)
	{
		_nom = nom;
	}
	
	/**
	 * @return la taille max des joueurs (garde en mémoire la valeur du premièr appel)
	 */
	protected static int getMaxLengthNoms(final boolean refresh)
	{
		if (_maxLengthNoms == -1 || refresh)
		{
			try
			{
				_maxLengthNoms = StringUtils.getMaxLength(JoueursRepository.getInstance().getNomsJoueurs());
			} catch (Exception e)
			{
				LOG.error(e);
				_maxLengthNoms = 0;
			}
		}
		return _maxLengthNoms;
	}

	public static Joueur createFromPseudo(final String pseudo, final JoueursRepository joueursExistants) throws Exception
	{
		if (joueursExistants.containsPseudo(pseudo))
		{
			throw new PseudoDejaUtiliseException(String.format(
				"Impossible de créer un nouveau joueur car le pseudo \"%s\" est déjà utilisé par \"%s\"",
				pseudo,
				joueursExistants.getJoueurByPseudo(pseudo)));
		}
		final String nom = promptNom(pseudo);
		if (nom == null)
			return null;
		
		return new Joueur(nom);
	}

	public Set<String> getPseudos()
	{
		if (_pseudos == null)
		{
			_pseudos = new TreeSet<String>();
		}
		return _pseudos;
	}
	
	public String getNom()
	{
		return _nom;
	}
	
	@Override
	public String toString()
	{
		return getNom();
	}

	public void setSandwich(Sandwich sandwich)
	{
		_sandwich = sandwich;
	}
	
	public Sandwich getSandwich()
	{
		return _sandwich;
	}

	/**
	 * @param dwich nom d'un sandwich écrit par un joueur (peut nécessiter un formattage pour éviter les doublons)
	 * @throws Exception 
	 */
	public void setDwich(String dwich) throws Exception
	{
		if (StringUtils.isNotEmpty(dwich))
		{
			final Sandwich sandwich = SandwichRepository.getInstance().getSandwichByNomDoodle(dwich);
			this.setSandwich(sandwich);
		}
	}

	public String getEmail()
	{
		if (_email == null)
		{
			_email = promptEmail();
		}
		return _email;
	}

	/**
	 * @return
	 */
	private String promptEmail()
	{
		return JOptionPane.showInputDialog(
		    null,
		    String.format("Quel est l'adresse email de %s ?", getNom()),
		    "Question");
	}

	/**
	 * @return
	 */
	private static String promptNom(final String pseudo)
	{
		return JOptionPane.showInputDialog(
		    null,
		    String.format("Quel est le nom complet du joueur portant le pseudo %s ?", pseudo),
		    "Question");
	}
	
	public void setEmail(String email)
	{
		_email = email;
	}

	public void addPseudo(String pseudo)
	{
		getPseudos().add(pseudo);
	}
	
	public int compareTo(Joueur o)
	{
		return this.getNom().compareTo(o.getNom()); 
	}
	
	public static String getListeNumerotee(Collection<Joueur> joueurs)
	{
		return StringUtils.join(joueurs, IOUtils.LINE_SEPARATOR, LIST_FORMAT);
	}
	
	public static String getListeDeDiffusion(Collection<Joueur> joueurs)
	{
		return StringUtils.join(joueurs, "; ", FORMAT);
	}
}
