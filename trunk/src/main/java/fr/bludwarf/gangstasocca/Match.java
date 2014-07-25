package fr.bludwarf.gangstasocca;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;
import org.simpleframework.xml.Text;
import org.simpleframework.xml.core.Commit;

import fr.bludwarf.commons.web.URLUtils;
import fr.bludwarf.gangstasocca.json.DoodleJSONParser;
import fr.bludwarf.gangstasocca.output.MatchWriter;
import fr.bludwarf.gangstasocca.stats.StatsMatch;

@Root(name = "match")
public class Match implements Comparable<Match>
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -6850634223529348632L;

	@Attribute(name = "score-rouges")
	int scoreRouges = 0;
	
	@Attribute
	String doodle;
	
	Map<String, JoueurXML> joueursXML;
	
	/**
	 * Joueur pour ce match
	 * 
	 * @author bludwarf@gmail.com
	 */
	@Root(name = "joueur")
	private static class JoueurXML extends Joueur
	{
		/** Équipe rouge */
		@Attribute
		protected boolean rouge = true;
		
		public JoueurXML(@Text String nom)
		{
			super(nom);
		}
		
		@Override
		public String getPseudo()
		{
			return getNom();
		}
		
	}
	

	@SuppressWarnings("unused")
	@ElementList(name = "joueurs", entry = "joueur")
	private void setJoueursXML(Collection<JoueurXML> joueursXML)
	{
		this.joueursXML = new HashMap<String, Match.JoueurXML>(joueursXML.size());
		for (final JoueurXML joueur : joueursXML)
		{
			this.joueursXML.put(joueur.getNom(), joueur);
		}
	}
	

	@ElementList(name = "joueurs", entry = "joueur")
	private Collection<JoueurXML> getJoueursXML()
	{
		return joueursXML.values();
	}
	
	@Attribute(name = "date")
	private Date _date;
	
	private Set<Joueur> joueurs;
	private String _doodle;

	private StatsMatch stats;

	public Match(@Attribute(name="doodle") final String doodle,
			   	 @Attribute(name="date")   final Date date)
	{
		_doodle = doodle;
		_date = date;
	}
	
	public void setJoueurs(Set<Joueur> joueurs)
	{
		this.joueurs = joueurs;
	}
	
	/**
	 * @return dans l'ordre du doodle
	 */
	public Set<Joueur> getJoueurs()
	{
		return joueurs;
	}
	
	public Date getDate()
	{
		return _date;
	}
	
	public String getDoodle()
	{
		return _doodle;
	}
	
	/**
	 * @return
	 * 
	 * @see Sandwich#getSandwiches(SortedSet)
	 */
	public Map<Sandwich, Integer> getSandwiches()
	{
		return Sandwich.getSandwiches(getJoueurs());
	}
	
	public String getCommunicator()
	{
		return URLUtils.communicator(getEmails());
	}
	
	public List<String> getEmails()
	{
		List<String> emails = new ArrayList<String>(getJoueurs().size());
		for (final Joueur j : getJoueurs())
		{
			emails.add(j.getEmail());
		}
		return emails;
	}
	
	/**
	 * @return mailto:*
	 */
	public String getMail()
	{
		final String sujet = getTitre();
		return URLUtils.mail(getEmails(), sujet);
	}

	/**
	 * @return
	 */
	public String getTitre()
	{
		final Properties props = new Properties();
		props.setProperty("date", getDateStr());
		return GangstaSoccaProperties.getInstance().getString("match.titre", props);
	}

	/**
	 * @return
	 */
	public String getDateStr()
	{
		return MatchWriter.DF.format(getDate());
	}

	public boolean contains(Joueur joueur)
	{
		return joueurs.contains(joueur);
	}
	
	@SuppressWarnings("unused")
	@Commit
	private void callbackLoadXML()
	{
		joueurs = new TreeSet<Joueur>();
		joueurs.addAll(getJoueursXML());
	}

	public Joueur getJoueur(String pseudo)
	{
		return getJoueurXML(pseudo);
	}

	public boolean joueurJoueRouge(String pseudo)
	{
		return joueursXML.get(pseudo).rouge;
	}

	/**
	 * @param joueur
	 * @return <code>true</code> si le joueur joue rouge dans ce match (on le cherche dans ce match parmis tous ses pseudos connus)
	 */
	public boolean joueurJoueRouge(Joueur joueur)
	{
		return getJoueurXML(joueur).rouge;
	}


	private JoueurXML getJoueurXML(Joueur joueur)
	{
		// cherche par tous les pseudos du joueur
		for (final String pseudo : joueur.getPseudos())
		{
			final JoueurXML joueurXML = getJoueurXML(pseudo);
			if (joueurXML != null) return joueurXML;
		}
		return null;
	}

	private JoueurXML getJoueurXML(String pseudo)
	{
		return joueursXML.get(pseudo);
	}
	
	public StatsMatch getStats()
	{
		if (stats == null)
		{
			
			stats = new StatsMatch(this);
		}
		return stats;
	}


	/**
	 * @param joueur
	 * @return <ul>
	 * <li>  1 : victoire</li>
	 * <li>0.5 : nul</li>
	 * <li>  0 : défaite</li>
	 * </ul>
	 */
	public double victoire(Joueur joueur)
	{
		final int buts = buts(joueur);
		if (buts > 0)
		{
			return 1;
		}
		else if (buts == 0)
		{
			return 0.5;
		}
		else
		{
			return 0;
		}
	}


	/**
	 * @return différence de buts marqués par l'équipe
	 */
	public int buts(Joueur joueur)
	{
		final JoueurXML jx = getJoueurXML(joueur);
		return scoreRouges * (jx.rouge ? 1 : -1);
	}


	public boolean memeEquipe(Joueur joueur1, Joueur joueur2)
	{
		if (joueur1.equals(joueur2)) return true;
		final JoueurXML jx1 = getJoueurXML(joueur1);
		final JoueurXML jx2 = getJoueurXML(joueur2);
		return jx1.rouge == jx2.rouge;
	}
	
	@Override
	public String toString()
	{
		return "Match du " + DoodleJSONParser.DF_OUT.format(getDate());
	}


	public int compareTo(Match match)
	{
		return this.getDate().compareTo(match.getDate());
	}
	
	@Override
	public boolean equals(Object obj)
	{
		if (this == obj) return true;
		if (obj instanceof Match)
		{
			return this.compareTo((Match) obj) == 0;
		}
		return false;
	}
	
	@Override
	public int hashCode()
	{
		return getDate().hashCode();
	}
}
