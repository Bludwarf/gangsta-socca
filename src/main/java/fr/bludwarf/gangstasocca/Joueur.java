package fr.bludwarf.gangstasocca;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import javax.swing.JOptionPane;

import org.apache.commons.io.IOUtils;
import org.simpleframework.xml.Root;
import org.simpleframework.xml.Text;

import fr.bludwarf.commons.StringUtils;
import fr.bludwarf.commons.formatters.CollectionFormatter;
import fr.bludwarf.gangstasocca.exceptions.PseudoDejaUtiliseException;
import fr.bludwarf.gangstasocca.stats.Stats;

@Root(name = "joueur")
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
	
	private ArrayList<String> _pseudos;
	
	@Text
	private String _nom;
	
	private Sandwich _sandwich;
	private String _email;

	private String _pseudoActuel;

	private Stats stats;

	private TreeSet<Match> matchesJoués;
	private Object matchesJoués_ref;
	
	public Joueur(@Text String nom)
	{
		_nom = nom;
		addPseudo(nom);
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

	public List<String> getPseudos()
	{
		if (_pseudos == null)
		{
			_pseudos = new ArrayList<String>();
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
		    DoodleConnector.EMAIL_ORGANISATEUR);
	}

	/**
	 * @return
	 */
	private static String promptNom(final String pseudo)
	{
		return JOptionPane.showInputDialog(
		    null,
		    String.format("Quel est le nom complet du joueur portant le pseudo %s ?", pseudo),
		    pseudo);
	}
	
	public void setEmail(String email)
	{
		_email = email;
	}

	public boolean addPseudo(String pseudo)
	{
		final List<String> pseudos = getPseudos();
		if (pseudos.contains(pseudo)) return false;
		return pseudos.add(pseudo);
	}
	
	public int compareTo(Joueur o)
	{
		return this.getNom().compareTo(o.getNom()); 
	}
	
	@Override
	public boolean equals(Object obj)
	{
		if (this == obj) return true;
		if (obj instanceof Joueur)
		{
			return compareTo((Joueur) obj) == 0;
		}
		return false;
	}
	
	public static String getListeNumerotee(Collection<Joueur> joueurs)
	{
		return StringUtils.join(joueurs, IOUtils.LINE_SEPARATOR, LIST_FORMAT);
	}
	
	public static String getListeDeDiffusion(Collection<Joueur> joueurs)
	{
		return StringUtils.join(joueurs, "; ", FORMAT);
	}

	/**
	 * @return le dernier pseudo du joueur, sinon le nom complet si pas de pseudo
	 */
	public String getPseudo()
	{
		final List<String> pseudos = getPseudos();
		if (pseudos == null || pseudos.isEmpty())
		{
			return getNom();
		}
		
		return pseudos.get(pseudos.size() - 1);
	}

	/**
	 * @see #getPseudoActuel()
	 */
	public void setPseudoActuel(String pseudo)
	{
		_pseudoActuel = pseudo;
	}
	
	/**
	 * @return le pseudo actuellement utilisé par le joueur, le dernier pseudo si inconnu
	 * @since 2 mai 2014
	 * 
	 * @see #getPseudo()
	 */
	public String getPseudoActuel()
	{
		if (_pseudoActuel != null)
		{
			return _pseudoActuel;
		}
		return getPseudo();
	}
	
	public boolean joueEnRouge(Match match)
	{
		return match.joueurJoueRouge(this);
	}
	
	public Stats getStats(Matches matches)
	{
		if (stats == null)
		{
			
			stats = new Stats(matches);
		}
		return stats;
	}

	public Set<Joueur> getEquipe(Match match) throws Exception
	{
		Set<Joueur> equipe = new LinkedHashSet<Joueur>();
		for (final Joueur joueur : match.getJoueurs())
		{
			if (joueur.joueAvec(this, match))
			{
				equipe.add(joueur);
			}
		}
		return equipe;
	}

	public Set<Joueur> getEquipeAdverse(Match match) throws Exception
	{
		Set<Joueur> equipe = new LinkedHashSet<Joueur>();
		for (final Joueur joueur : match.getJoueurs())
		{
			if (!joueur.joueAvec(this, match))
			{
				equipe.add(joueur);
			}
		}
		return equipe;
	}

	public boolean joueAvec(Joueur joueur, Match match)
	{
		return match.memeEquipe(joueur, this);
	}

	public TreeSet<Match> getMatches(Matches matches)
	{
		if (matchesJoués_ref == null || matchesJoués_ref != matches) // need refresh ?
		{
			matchesJoués = new TreeSet<Match>();
			for (final Match match : matches)
			{
				if (this.aJoué(match))
				{
					matchesJoués.add(match);
				}
			}
			matchesJoués_ref = matches;
		}
		return matchesJoués;
	}

	/**
	 * @param match
	 * @return <code>true</code> ssi le match a été joué et que le joueur comptait parmi les participants
	 */
	public boolean aJoué(Match match)
	{
		return match.aÉtéJoué() && match.contains(this);
	}
	
	public Match getPremierMatch(Matches matches)
	{
		return getMatches(matches).first();
	}

	public boolean premierMatch(Match match, Matches matches)
	{
		return getPremierMatch(matches).equals(match);
	}

	public Match matchPrécédent(Match match, Matches matches)
	{
		return getMatches(matches).lower(match);
	}
	
	@Override
	public int hashCode()
	{
		return getNom().hashCode();
	}
}
