package fr.bludwarf.gangstasocca;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.apache.commons.lang.time.DateUtils;
import org.apache.log4j.Logger;
import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;
import org.simpleframework.xml.Text;
import org.simpleframework.xml.core.Commit;

import fr.bludwarf.commons.web.URLUtils;
import fr.bludwarf.gangstasocca.json.DoodleJSONParser;
import fr.bludwarf.gangstasocca.output.MatchWriter;
import fr.bludwarf.gangstasocca.stats.Stats;
import fr.bludwarf.gangstasocca.stats.StatsMatch;

@Root(name = "match")
public class Match implements Comparable<Match>
{
	
	protected static Logger LOG = Logger.getLogger(Match.class);

	@Attribute(name = "score-rouges")
	int scoreRouges = 0;

	@Attribute(required = false)
	boolean joué = false;
	
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
		
		/** ELO avant */
		protected double eloAvant = Stats.START_ELO;
		
		protected double eloAprès = Stats.START_ELO;
		
		protected Match match;
		
		public JoueurXML(@Text String nom)
		{
			super(nom);
		}
		
		public JoueurXML(Joueur joueur)
		{
			super(joueur.getNom());
		}

		@Override
		public String getPseudo()
		{
			return getNom();
		}

		/**
		 * @return
		 * @throws Exception
		 */
		public Stats getStats() throws Exception
		{
			return MatchesRepository.getInstance().getStats();
		}
		
		/**
		 * @param eloAprès ELO avant le match
		 * @throws Exception 
		 */
		@Attribute(name = "elo-avant", required = false)
		public void setEloAvant(double eloAvant)
		{
			this.eloAvant = eloAvant;
		}

		@Attribute(name = "elo-avant", required = false)
		public double getEloAvant() throws Exception
		{
			return getStats().getEloAvant(getMatch(), this);
		}
		
		/**
		 * @param eloAprès ELO après le match
		 */
		@Attribute(name = "elo-après", required = false)
		public void setEloAprès(double eloAprès) throws Exception
		{
			this.eloAprès = eloAprès;
			getStats().setEloAprès(getMatch(), this, eloAvant);
		}

		@Attribute(name = "elo-après", required = false)
		// FIXME : ne pas faire de write sur cet attribut si le match n'a pas été joué
		public double getEloAprès() throws Exception
		{
			return getStats().getEloAprès(getMatch(), this);
		}
		
		public void setMatch(Match match)
		{
			this.match = match;
		}
		
		public Match getMatch()
		{
			return match;
		}
		
	}
	

	@SuppressWarnings("unused")
	@ElementList(name = "joueurs", entry = "joueur")
	private void setJoueursXML(ArrayList<JoueurXML> joueursXML)
	{
		this.joueursXML = new LinkedHashMap<String, Match.JoueurXML>(joueursXML.size());
		for (final JoueurXML joueur : joueursXML)
		{
			this.joueursXML.put(joueur.getNom(), joueur);
			joueur.setMatch(this);
		}
	}
	
	private void setJoueursXML(Set<Joueur> joueurs)
	{
		LOG.debug(String.format("setJoueurXML(%s)", joueurs));
		this.joueursXML = new LinkedHashMap<String, Match.JoueurXML>(joueurs.size());
		for (final Joueur joueur : joueurs)
		{
			final Match.JoueurXML joueurXML = new Match.JoueurXML(joueur);
			this.joueursXML.put(joueur.getNom(), joueurXML);
			joueurXML.setMatch(this);
		}
	}
	

	@ElementList(name = "joueurs", entry = "joueur")
	private ArrayList<JoueurXML> getJoueursXML()
	{
		if (joueursXML == null)
		{
			joueursXML = new LinkedHashMap<String, Match.JoueurXML>();
		}
		return new ArrayList<Match.JoueurXML>(joueursXML.values());
	}
	
	@Attribute(name = "date")
	private Date _date;
	
	private Set<Joueur> joueurs;

	private StatsMatch stats;

	private Date _dateFin;

	
	/**
	 * @param doodle
	 * @param date fixe l'heure en fonction de match.date.hh et match.date.mm
	 */
	public Match(@Attribute(name="doodle") final String doodle,
			   	 @Attribute(name="date")   final Date date)
	{
		this.doodle = doodle;
		final Date[] dates = setMidi(date);
		_date = dates[0];
		_dateFin = dates[1];
	}
	
	public void setJoueurs(Set<Joueur> joueurs)
	{
		this.joueurs = joueurs;
		setJoueursXML(joueurs);
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
	
	public String getDoodle() throws Exception
	{
		if (doodle == null)
		{
			doodle = JoueursRepository.getInstance().getDoodleURL();
		}
		return doodle;
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
		if (joueurs == null) return false;
		return joueurs.contains(joueur);
	}
	
	@SuppressWarnings("unused")
	@Commit
	private void callbackLoadXML() throws Exception
	{
		joueurs = new TreeSet<Joueur>();
		joueurs.addAll(getJoueursXML());
		doodle = getDoodle();
		
		// Lien match <-> joueur
		for (final JoueurXML joueur : getJoueursXML())
		{
			joueur.setMatch(this);
		}
		
		final Date[] dates = setMidi(_date);
		_date = dates[0];
		_dateFin = dates[1];
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

	public boolean aÉtéJoué()
	{
		return joué;
	}

	public Date getDateFin()
	{
		if (_dateFin == null)
		{
			
			_dateFin = DateUtils.addHours(getDate(), 2);
		}
		return _dateFin;
	}
	
	public static Date[] setMidi(final Date date)
	{
		final Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		
		final GangstaSoccaProperties props = GangstaSoccaProperties.getInstance();
		cal.set(Calendar.HOUR_OF_DAY, 	props.getInt("match.heure.hh"));
		cal.set(Calendar.MINUTE, 		props.getInt("match.heure.mm"));
		final Date deb = cal.getTime();
		
		cal.set(Calendar.HOUR_OF_DAY, 	props.getInt("match.heureFin.hh"));
		cal.set(Calendar.MINUTE, 		props.getInt("match.heureFin.mm"));
		final Date fin = cal.getTime();
		
		return new Date[]{deb, fin};
	}
}
