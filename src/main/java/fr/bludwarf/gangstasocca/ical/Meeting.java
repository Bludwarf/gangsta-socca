package fr.bludwarf.gangstasocca.ical;

import java.util.Collection;
import java.util.Date;

import fr.bludwarf.commons.StringUtils;

public class Meeting
{
	private Date start;
	private Date end;
	private Collection<String> participants;
	private String orga;
	private String titre;
	private String description;

	public Meeting(Date start, Date end)
	{
		this.start = start;
		this.end = end;
	}
	
	public void setParticipants(Collection<String> participants)
	{
		this.participants = participants;
	}
	
	public Collection<String> getParticipants()
	{
		return participants;
	}
	
	public String getEmailsParticipants()
	{
		return StringUtils.join(participants, ";");
	}
	
	public Date getDebut()
	{
		return start;
	}
	
	public Date getFin()
	{
		return end;
	}

	public String getOrga()
	{
		return orga;
	}

	public void setOrga(String orga)
	{
		this.orga = orga;
	}

	public String getTitre()
	{
		return titre;
	}

	public void setTitre(String titre)
	{
		this.titre = titre;
	}

	public String getDescription()
	{
		return description;
	}

	public void setDescription(String description)
	{
		this.description = description;
	}
	
	
}
