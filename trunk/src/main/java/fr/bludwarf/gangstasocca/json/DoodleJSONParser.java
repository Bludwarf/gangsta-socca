package fr.bludwarf.gangstasocca.json;

import static org.apache.commons.lang.time.DateUtils.isSameDay;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.apache.commons.lang.time.DateUtils;
import org.json.JSONArray;
import org.json.JSONObject;

public class DoodleJSONParser
{
	
	public static final DateFormat DF_OUT = SimpleDateFormat.getDateInstance();
	
	private static List<JSONObject> _participantsJSON;
	
	/** valeur de l'objet JSON <code>doodleJS.data.poll</code> */
	private JSONObject _data;
	
	private List<String> _participants;

	private List<Date> _dates;

	public DoodleJSONParser(final String data)
	{
		_data = new JSONObject(data);
	}

	/**
	 * @param data valeur de l'objet JSON <code>doodleJS.data.poll</code>
	 * @return la liste des participants (JSON)
	 */
	public List<JSONObject> getParticipantsJSON()
	{
		if (_participantsJSON == null)
		{
			final JSONArray parts = getArray("participants");
			
			final List<JSONObject> participants = new ArrayList<JSONObject>();
			
			for (int i = 0; i < parts.length(); ++i)
			{
				final JSONObject part = (JSONObject) parts.get(i);
				participants.add(part);
//				System.out.println("PART = " + part);
//				System.out.println("PART.name = " + getParticipant(part));
			}
			
			_participantsJSON = participants;
		}
		
		return _participantsJSON;
	}

	/**
	 * @param json
	 * @return
	 */
	public JSONArray getArray(final String key)
	{
		return (JSONArray) getData().get(key);
	}

	/**
	 * @param data valeur de l'objet JSON <code>doodleJS.data.poll</code>
	 * @return la liste des participants
	 */
	public static List<String> getParticipants(final List<JSONObject> participantsJSON)
	{		
		final List<String> participants = new ArrayList<String>();
		
		for (JSONObject partJSON : participantsJSON)
		{
			participants.add(getParticipant(partJSON));
		}
		
		return participants;
	}

	/**
	 * @param participantJSON
	 * @return
	 */
	public static String getParticipant(JSONObject participantJSON)
	{
		return participantJSON.getString("name");
	}

	/**
	 * @param data valeur de l'objet JSON <code>doodleJS.data.poll</code>
	 * @return la liste des participants
	 */
	public List<String> getParticipants()
	{
		if (_participants == null)
		{
			_participants = getParticipants(
				getParticipantsJSON());
		}
		return _participants;
	}
	
	public JSONObject getData()
	{
		return _data;
	}
	
	public List<Date> getDates()
	{
		if (_dates == null)
		{
			final JSONArray datesJSON = getArray("fcOptions");
			final Calendar cal = Calendar.getInstance();
			List<Date> dates = new ArrayList<Date>();
			
			for (int i = 0; i < datesJSON.length(); ++i)
			{
				final JSONObject dateJSON = (JSONObject) datesJSON.get(i);
				final long milli = dateJSON.getLong("start") * 1000;
				cal.setTimeInMillis(milli);
				final Date date = cal.getTime();
				dates.add(date);
				System.out.println("date = " + DF_OUT.format(date));
			}
			
			_dates = dates;
		}
		return _dates;
	}
	
	public int getColumn(final Date date)
	{
		final List<Date> dates = getDates();
		for (int i = 0; i < dates.size(); ++i)
		{
			final Date datei = dates.get(i);
			if (isSameDay(datei, date))
			{
				return i;
			}
		}
		return -1;
	}

	/**
	 * @param data valeur de l'objet JSON <code>doodleJS.data.poll</code>
	 * @return la liste des participants
	 */
	public List<String> getParticipants(final Date date)
	{
		final int col = getColumn(date);		
		final List<String> participants = new ArrayList<String>();
		
		if (col == -1)
			return participants;
		
		for (JSONObject part : getParticipantsJSON())
		{
			if (isPresent(part, col))
			{
				participants.add(getParticipant(part));
			}
		}
		
		return participants;
	}
	
	/**
	 * @return la date du prochain match dans le Doodle, <code>null</code> si plus aucune partie
	 */
	public Date getProchainMatch()
	{
		final Calendar cal = Calendar.getInstance();
		final Date now = cal.getTime();
		
		for (final Date date : getDates())
		{
			if (now.getTime() <= date.getTime() || DateUtils.isSameDay(now, date))
			{
				System.out.println("Date du prochain match = " + DF_OUT.format(date));
				return date;
			}
		}
		
		return null;
	}

	/**
	 * @param part
	 * @param col
	 * @return <code>true</code> si le participant a coché "Oui" pour cette colonne
	 */
	public static boolean isPresent(JSONObject part, final int col)
	{
		return part.getString("preferences").charAt(col) == 'y';
	}
}
