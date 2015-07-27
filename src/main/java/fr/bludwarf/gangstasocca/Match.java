package fr.bludwarf.gangstasocca;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.SortedSet;

import org.apache.commons.lang.time.DateUtils;
import org.apache.log4j.Logger;
import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;
import org.simpleframework.xml.Text;
import org.simpleframework.xml.core.Commit;
import org.simpleframework.xml.core.Persist;

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
	
	@Attribute(name = "diff-elo-rouges", required = false)
	Double diffEloRouges = null;
	
	@Attribute
	String doodle;
	
	Map<String, JoueurXML> joueursXML;
	
	/**
	 * Joueur pour ce match
	 * 
	 * @author bludwarf@gmail.com
	 */
	@Root(name = "joueur")
	public static class JoueurXML
	{
		Joueur joueur = null;
		
		@Attribute(name = "inscrit-avant", required = false)
		protected Date inscritAvant = null;
		
		/** Équipe rouge */
		@Attribute(required = false)
		protected boolean rouge = true;
		
		@Text
		String nom;
		
		/** ELO avant */
		@Attribute(name = "elo-avant", required = false)
		protected Double eloAvant = null;

		@Attribute(name = "elo-après", required = false)
		protected Double eloAprès = null;
		
		protected Match match;
		
		public JoueurXML()
		{
			
		}
		
		public JoueurXML(Joueur joueur)
		{
			this.joueur = joueur;
			this.nom = joueur.getNom();
		}

		public JoueurXML(String nom)
		{
			this.nom = nom;
		}

		public String getNom()
		{
			return nom;
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
		 * @return le joueur réel (et pas seulement l'instance dans le XML car son pseudo peut changer)
		 * @throws Exception 
		 */
		public Joueur getJoueur() throws Exception
		{
			if (joueur == null)
			{
				joueur = JoueursRepository.getInstance().getJoueurByPseudo(getNom());
			}
			return joueur;
		}
		
		/**
		 * @param eloAprès ELO avant le match
		 * @throws Exception 
		 */
//		@Attribute(name = "elo-avant", required = false)
		public void setEloAvant(double eloAvant)
		{
			this.eloAvant = eloAvant;
		}

//		@Attribute(name = "elo-avant", required = false)
		@Deprecated // recalculerEloAvant
		public double getEloAvant() throws Exception
		{
			if (eloAvant != null) return eloAvant;
			return getStats().getEloAvant(getMatch(), getJoueur());
		}
		
		public double recalculerEloAvant() throws Exception
		{
			eloAvant = getStats().getEloAvant(getMatch(), getJoueur());
			return eloAvant;
		}
		
		/**
		 * @param eloAprès ELO après le match
		 */
//		@Attribute(name = "elo-après", required = false)
		public void setEloAprès(double eloAprès) throws Exception
		{
			this.eloAprès = eloAprès;
			getStats().setEloAprès(getMatch(), getJoueur(), eloAvant);
		}

//		@Attribute(name = "elo-après", required = false)
		// FIXME : ne pas faire de write sur cet attribut si le match n'a pas été joué
		public double getEloAprès() throws Exception
		{
			if (eloAprès != null) return eloAprès;
			return getStats().getEloAprès(getMatch(), getJoueur());
		}
		
		public void setMatch(Match match)
		{
			this.match = match;
			if (this.inscritAvant == null) this.inscritAvant = match._lastmod;
		}
		
		public Match getMatch() throws MatchInconnuException
		{
			if (match == null) throw new MatchInconnuException("Match inconnu pour le joueur " + this);
			return match;
		}
		
		@Persist
		protected void persist()
		{
			if (eloAvant != null) eloAvant = Stats.roundElo(eloAvant);
			if (eloAprès != null) eloAprès = Stats.roundElo(eloAprès);
		}
		
		@Override
		public String toString()
		{
			return nom;
		}

		public void reset(JoueurXML joueurAvant)
		{
			if (LOG.isDebugEnabled()) LOG.debug("fusion avec l'ancienne version du match de " + this);
			rouge = joueurAvant.rouge;
			eloAvant = joueurAvant.eloAvant;
			eloAprès = joueurAvant.eloAprès;
			
			if (LOG.isDebugEnabled()) LOG.debug("joueurAvant.inscritAvant = " + joueurAvant.inscritAvant);
			if (joueurAvant.inscritAvant != null) inscritAvant = joueurAvant.inscritAvant;
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
	
//	private void setJoueursXML(Set<Joueur> joueurs)
//	{
//		LOG.debug(String.format("setJoueurXML(%s)", joueurs));
//		this.joueursXML = new LinkedHashMap<String, Match.JoueurXML>(joueurs.size());
//		for (final Joueur joueur : joueurs)
//		{
//			final Match.JoueurXML joueurXML = new Match.JoueurXML(joueur);
//			this.joueursXML.put(joueur.getNom(), joueurXML);
//			joueurXML.setMatch(this);
//		}
//	}
	

	/**
	 * @return
	 * @deprecated Ne pas utiliser pour ajouter des joueurs
	 */
	@Deprecated
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
	
	@Attribute(name = "lastmod", required = false)
	private Date _lastmod;

	private StatsMatch stats;

	private Date _dateFin;

	@Deprecated
	protected Match()
	{
		
	}
	
	/**
	 * @param doodle
	 * @param date fixe l'heure en fonction de match.date.hh et match.date.mm
	 */
	public Match(final String doodle, final Date date)
	{
		this.doodle = doodle;
		final Date[] dates = setMidi(date);
		_date = dates[0];
		_dateFin = dates[1];
		_lastmod = new Date();
	}
	
//	public void setJoueurs(Set<Joueur> joueurs)
//	{
//		setJoueursXML(joueurs);
//	}

	public void setJoueurs(Set<Joueur> joueurs)
	{
		joueursXML = new LinkedHashMap<String, Match.JoueurXML>(joueurs.size());
		for (final Joueur joueur : joueurs)
		{
			add(joueur);
		}
	}

	/**
	 * @return dans l'ordre du doodle
	 * @throws Exception 
	 */
	public Set<Joueur> getJoueurs() throws Exception
	{
		Set<Joueur> joueurs = new LinkedHashSet<Joueur>(); // en linked pour garder l'ordre du Doodle
		for (final JoueurXML joueurXml : getJoueursXML())
		{
			final Joueur joueur = JoueursRepository.getInstance().getJoueurByPseudo(joueurXml.getNom());
			if (joueur == null) throw new JoueurInconnuException("Nom/pseudo inconnu : " + joueurXml.getNom());
			joueurs.add(joueur);
		}
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
	 * @throws Exception 
	 * 
	 * @see Sandwich#getSandwiches(SortedSet)
	 */
	public Map<Sandwich, Integer> getSandwiches() throws Exception
	{
		return Sandwich.getSandwiches(getJoueurs());
	}
	
	public String getCommunicator() throws Exception
	{
		return URLUtils.communicator(getEmails());
	}
	
	public List<String> getEmails() throws Exception
	{
		List<String> emails = new ArrayList<String>(getJoueurs().size());
		for (final Joueur j : getJoueurs())
		{
			emails.add(j.getEmail());
		}
		return emails;
	}
	
	/**
	 * @return mailto:
	 * @throws Exception *
	 */
	public String getMail() throws Exception
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

	/**
	 * Compare avec la liste des pseudos du joueur
	 * @param joueur
	 * @return
	 */
	public boolean contains(Joueur joueur)
	{
		for (final JoueurXML joueurXml : getJoueursXML())
		{
			if (joueur.getNom().equals(joueurXml.getNom())  || joueur.getPseudos().contains(joueurXml.getNom())) return true;
		}
		return false;
	}
	
	@SuppressWarnings("unused")
	@Commit
	private void callbackLoadXML() throws Exception
	{
//		joueurs = new TreeSet<Joueur>();
//		joueurs.addAll(getJoueurs());
		doodle = getDoodle();
		
		// Lien match <-> joueur
		for (final JoueurXML joueur : joueursXML.values())
		{
			joueur.setMatch(this);
		}
		
		final Date[] dates = setMidi(_date);
		_date = dates[0];
		_dateFin = dates[1];
	}

	public Joueur getJoueur(String pseudo) throws Exception
	{
		return getJoueurXML(pseudo).getJoueur();
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

	private JoueurXML getJoueurXML(String nom)
	{
		return joueursXML.get(nom);
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

	public Double getEloAprès(Joueur joueur)
	{
		final JoueurXML jXml = getJoueurXML(joueur);
		return jXml.eloAprès;
	}

	public void addJoueur(String nom)
	{
		final JoueurXML xml = new JoueurXML(nom);
		xml.setMatch(this);
		getJoueursXML();
		joueursXML.put(nom, xml);
	}

	public void add(Joueur joueur)
	{
		LOG.info("Ajout du joueur : " + joueur + " : pseudo = " + joueur.getPseudoActuel());
		final JoueurXML xml = new JoueurXML(joueur);
		xml.setMatch(this);
		getJoueursXML();
		joueursXML.put(joueur.getNom(), xml);
	}
	
	@Persist
	protected void persist() throws Exception
	{
		// Si match non joué on calcul quand même le ELO AVANT
		if (!aÉtéJoué())
		{
			final String comment = "  recalcul ELO " + this + " : ";
			for (final JoueurXML joueur : joueursXML.values())
			{
				if (joueur.eloAvant == null)
				{
					LOG.info(comment + joueur.getNom());
					joueur.recalculerEloAvant();
				}
			}
			
		}

		else
		{
			if (diffEloRouges == null)
			{
				// Elo équipe
				List<JoueurXML> rouges = new ArrayList<Match.JoueurXML>();
				List<JoueurXML> nonRouges = new ArrayList<Match.JoueurXML>();
				diffEloRouges = 0.0;
				for (final JoueurXML joueurXml : getJoueursXML())
				{
					if (joueurXml.eloAvant == null) throw new RuntimeException("Le ELO avant le " + this + " est vide pour " + joueurXml);
					if (joueurXml.rouge) {
						rouges.add(joueurXml);
						diffEloRouges += joueurXml.eloAvant;
					}
					else {
						nonRouges.add(joueurXml);
						diffEloRouges -= joueurXml.eloAvant;
					}
				}
				
				// Arrondi
				diffEloRouges = Stats.roundElo(diffEloRouges);
			}
		}
	}

	public Double getDiffEloRouges()
	{
		return diffEloRouges;
	}

	public boolean estRouge(Joueur joueur)
	{
		final JoueurXML xml = getJoueurXML(joueur);
		return xml.rouge;
	}

	/**
	 * Récupère les informations à partir de la précédente version (Doodle) du match
	 * @param ancienneVersion
	 */
	public void fusionnerDepuis(Match ancienneVersion)
	{
		// On remplace les infos des joueurs actuels uniquement
		for (final String nom : joueursXML.keySet())
		{
			final JoueurXML joueurAvant = ancienneVersion.getJoueurXML(nom);
			if (joueurAvant != null)
			{
				final JoueurXML joueurActuel = joueursXML.get(nom);
				joueurActuel.reset(joueurAvant);
			}
		}
	}
}
