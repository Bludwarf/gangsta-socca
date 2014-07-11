package fr.bludwarf.gangstasocca;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.SortedSet;

import fr.bludwarf.commons.web.URLUtils;
import fr.bludwarf.gangstasocca.output.MatchWriter;

public class Match
{
	private Date _date;
	private Set<Joueur> joueurs;
	private String _doodle;

	public Match(final String doodle, final Date date)
	{
		_doodle = doodle;
		_date = date;
	}
	
	public void setJoueurs(Set<Joueur> joueurs)
	{
		this.joueurs = joueurs;
	}
	
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
}
