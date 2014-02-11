package fr.bludwarf.gangstasocca.json;

import static org.junit.Assert.*;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.json.JSONObject;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

public class DoodleJSONParserTest
{
	
	private static DoodleJSONParser _parser;

	@BeforeClass
	public static void setUp()
	{
		final String data = "{\"location\":{\"address\":\"Avenue du Phare du Grand Lejeon, Melesse, France\",\"name\":\"Karting Cap Malo\",\"country\":\"FR\"},\"socialHookFacebook\":\"http://facebook.com/sharer.php?u=http%3A%2F%2Fdoodle.com%2Fuvbbrna677dfbw7e\",\"state\":\"OPEN\",\"fcOptions\":[{\"id\":0,\"allDay\":true,\"start\":1391472000,\"end\":1391558399},{\"id\":1,\"allDay\":true,\"start\":1391731200,\"end\":1391817599},{\"id\":2,\"allDay\":true,\"start\":1392076800,\"end\":1392163199},{\"id\":3,\"allDay\":true,\"start\":1392336000,\"end\":1392422399},{\"id\":4,\"allDay\":true,\"start\":1392681600,\"end\":1392767999},{\"id\":5,\"allDay\":true,\"start\":1392940800,\"end\":1393027199},{\"id\":6,\"allDay\":true,\"start\":1393286400,\"end\":1393372799},{\"id\":7,\"allDay\":true,\"start\":1393545600,\"end\":1393631999}],\"prettyUrl\":\"http://doodle.com/uvbbrna677dfbw7e\",\"type\":\"date\",\"showAds\":true,\"skip\":0,\"participants\":[{\"id\":\"1459768562\",\"emailHash\":\"1ac599e4b5830e9eac265721eb2a013b\",\"name\":\"Mickael Hamon (thon)\",\"userBehindParticipant\":\"7pf3u6atrewjdt6vwqcuhsvpup78lm92\",\"locked\":true,\"meetMePage\":\"\",\"avatar\":\"https://6a5edc300520d4037dd6-0732807511066685711db213ddc1d2df.ssl.cf2.rackcdn.com/56y4h7ga96zf9955\",\"preferences\":\"yyyyyyyy\"},{\"id\":\"889798414\",\"emailHash\":\"1f6fe4aca3aed1a349cb01fe9e79f33d\",\"name\":\"Matt LaRage (Thon)\",\"userBehindParticipant\":\"te00dhqvwrxd8qsa86whuy97ian6v0gn\",\"locked\":false,\"meetMePage\":\"\",\"avatar\":\"https://6a5edc300520d4037dd6-0732807511066685711db213ddc1d2df.ssl.cf2.rackcdn.com/57mgq43dd4i998kz\",\"preferences\":\"nnnnqqqq\"},{\"id\":\"634036973\",\"name\":\"Nicolas M (thon)\",\"locked\":false,\"preferences\":\"nnnnynyn\"},{\"id\":\"2130701008\",\"name\":\"Lotfi (Thon)\",\"locked\":false,\"preferences\":\"qqqqqqqq\"},{\"id\":\"1796413362\",\"name\":\"Morgan (Americain J)\",\"locked\":false,\"preferences\":\"ynnnqqqq\"},{\"id\":\"786012825\",\"name\":\"Dany K (Poulet)\",\"locked\":false,\"preferences\":\"yyyyyyyy\"},{\"id\":\"506050463\",\"name\":\"Thomas (club)\",\"locked\":false,\"preferences\":\"nnnnqqqq\"},{\"id\":\"228294189\",\"name\":\"JPB (rien)\",\"locked\":false,\"preferences\":\"nynnnnnn\"},{\"id\":\"947733808\",\"name\":\"Mohamed F(Poulet)\",\"locked\":false,\"preferences\":\"nnynqqqq\"},{\"id\":\"1343943041\",\"name\":\"Archange (club)\",\"locked\":false,\"preferences\":\"yyyyqqqq\"},{\"id\":\"1233394210\",\"name\":\"Romain (Americain J)\",\"locked\":false,\"preferences\":\"ynynynyn\"},{\"id\":\"101327628\",\"name\":\"Patrice (thon)\",\"locked\":false,\"preferences\":\"nnnnqqqq\"},{\"id\":\"2084306685\",\"name\":\"Achref (rien)\",\"locked\":false,\"preferences\":\"nnnnqqqq\"},{\"id\":\"1554501198\",\"name\":\"Daniel\",\"locked\":false,\"preferences\":\"ynynqqqq\"},{\"id\":\"995559697\",\"name\":\"Babs(THON)\",\"locked\":false,\"preferences\":\"ynnnnnnn\"},{\"id\":\"2117485110\",\"name\":\"Christian F (thon)\",\"locked\":false,\"preferences\":\"nnnnnnnn\"},{\"id\":\"506765915\",\"name\":\"Laurent LG (rien)\",\"locked\":false,\"preferences\":\"nynnqqqq\"},{\"id\":\"63854733\",\"name\":\"Aziz\",\"locked\":false,\"preferences\":\"nnnnqqqq\"},{\"id\":\"1622298417\",\"emailHash\":\"f3ab64ce28b922b3b4602b50cd3ae967\",\"name\":\"Eddy Z (Club)\",\"userBehindParticipant\":\"yi2iz5tn7f8w2nr0nlca85o75gqfu1z8\",\"locked\":true,\"meetMePage\":\"\",\"avatar\":\"https://6a5edc300520d4037dd6-0732807511066685711db213ddc1d2df.ssl.cf2.rackcdn.com/mnz3vnv9vz1gu1f5s016721y6o01z5wz\",\"preferences\":\"yynnnnnn\"},{\"id\":\"26182257\",\"name\":\"Stéphane(Poulet)\",\"locked\":false,\"preferences\":\"yyyyqqqq\"},{\"id\":\"378403986\",\"name\":\"Olivier T\",\"locked\":false,\"preferences\":\"yyyyqqqq\"},{\"id\":\"1713305206\",\"name\":\"Médoune (Thon)\",\"locked\":false,\"preferences\":\"nnnnqqqq\"},{\"id\":\"1535185994\",\"name\":\"Cyril (Poulet)\",\"locked\":false,\"preferences\":\"nnnnqqqq\"},{\"id\":\"836542681\",\"name\":\"Youssef I (Thon)\",\"locked\":false,\"preferences\":\"nnnnqqqq\"},{\"id\":\"1665641857\",\"name\":\"Amine Belhaj (thon)\",\"locked\":false,\"preferences\":\"nnnnqqqq\"},{\"id\":\"2035631344\",\"name\":\"Ali GT (thon)\",\"locked\":false,\"preferences\":\"ynnnqqqq\"},{\"id\":\"1170312371\",\"emailHash\":\"6d339ab8447d89c5751cb0ea01ef2454\",\"name\":\"Raphaël Limbach\",\"userBehindParticipant\":\"uy78qjin5p0rtaeogn0b1yji3oblmig9\",\"locked\":true,\"meetMePage\":\"\",\"avatar\":\"https://6a5edc300520d4037dd6-0732807511066685711db213ddc1d2df.ssl.cf2.rackcdn.com/xkw8k32d5rh4wqxt\",\"preferences\":\"nynnnnnn\"}],\"fcStartOfFirstOption\":1391472000,\"initiatorAvatar\":\"https://6a5edc300520d4037dd6-0732807511066685711db213ddc1d2df.ssl.cf2.rackcdn.com/2kpf9m292twq8mkt\",\"id\":\"uvbbrna677dfbw7e\",\"title\":\"Midi Soccer Mardi & Vendredi\",\"lastActivity\":\"il y a 4 heures\",\"socialHookTwitter\":\"http://twitter.com/share?text=Midi%20Soccer%20Mar...%20%7C%20Veuillez%20participer%20au%20sondage%20Doodle%20%3A&url=http%3A%2F%2Fdoodle.com%2Fuvbbrna677dfbw7e\",\"socialHookEmail\":\"mailto:?subject=Midi%20Soccer%20Mardi%20%26%20Vendredi&body=Je%20souhaite%20vous%20inviter%20au%20sondage%20Doodle%20%C2%AB%20Midi%20Soccer%20Mardi%20%26%20Vendredi%C2%BB.%0A%0AVeuillez%20suivre%20le%20lien%20afin%20de%20participer%20au%20sondage%20%3A%0Ahttp%3A%2F%2Fdoodle.com%2Fuvbbrna677dfbw7e\",\"isByInvitationOnly\":false,\"levels\":2,\"descriptionHTML\":\"Soccer Capgemini Capmalo\",\"features\":{\"pickSubCalendar\":false,\"smsLink\":false,\"hideAds\":false,\"avatar\":false,\"requireAuth\":false,\"useCustomDecoration\":false,\"hideDoodleFor\":false,\"useCustomURL\":false,\"useSSL\":false,\"useCustomLogo\":false,\"customTheme\":false,\"useCustomCSS\":false,\"quickReply\":false,\"extraInformation\":false},\"grantWrite\":false,\"ads\":[null,null,null,null,null,null,null,null],\"followEventsStream\":false,\"optionsAvailable\":\"yyyyyyyy\",\"eventLimit\":100,\"hasTimeZone\":false,\"fcEndOfLastOption\":1393632000,\"optionsHash\":\"94e689fc487c6653370e56aff86cc00f\",\"optionsText\":[\"mardi 4 février 2014\",\"vendredi 7 février 2014\",\"mardi 11 février 2014\",\"vendredi 14 février 2014\",\"mardi 18 février 2014\",\"vendredi 21 février 2014\",\"mardi 25 février 2014\",\"vendredi 28 février 2014\"],\"grantRead\":true,\"category\":\"ausgang\",\"initiatorName\":\"Mickael HAMON\",\"weightedOptionsHtml\":[[[\"f&#233;vrier&#160;2014\",8,\"\"]],[[\"mar.&#160;4\",1,\"d\"],[\"ven.&#160;7\",1,\"d\"],[\"mar.&#160;11\",1,\"d\"],[\"ven.&#160;14\",1,\"d\"],[\"mar.&#160;18\",1,\"d\"],[\"ven.&#160;21\",1,\"d\"],[\"mar.&#160;25\",1,\"d\"],[\"ven.&#160;28\",1,\"\"]]],\"skips\":[],\"hasQOptions\":true,\"example\":false,\"comments\":[]}";
		final DoodleJSONParser parser = new DoodleJSONParser(data);
		_parser = parser;
	}
	
	public DoodleJSONParser getParser()
	{
		return _parser;
	}

	@Test
	public void testGetParticipants()
	{
		List<String> parts = getParser().getParticipants();
		
		assertNotNull(parts);
		assertTrue(parts.contains("Mickael Hamon (thon)"));
		assertTrue(parts.contains("Matt LaRage (Thon)"));
		assertTrue(parts.contains("Nicolas M (thon)"));
		assertTrue(parts.contains("Lotfi (Thon)"));
		assertTrue(parts.contains("Morgan (Americain J)"));
		assertTrue(parts.contains("Dany K (Poulet)"));
		assertTrue(parts.contains("Thomas (club)"));
		assertTrue(parts.contains("JPB (rien)"));
		assertTrue(parts.contains("Mohamed F(Poulet)"));
		assertTrue(parts.contains("Archange (club)"));
		assertTrue(parts.contains("Romain (Americain J)"));
		assertTrue(parts.contains("Patrice (thon)"));
		assertTrue(parts.contains("Achref (rien)"));
		assertTrue(parts.contains("Daniel"));
		assertTrue(parts.contains("Babs(THON)"));
		assertTrue(parts.contains("Christian F (thon)"));
		assertTrue(parts.contains("Laurent LG (rien)"));
		assertTrue(parts.contains("Aziz"));
		assertTrue(parts.contains("Eddy Z (Club)"));
		assertTrue(parts.contains("Stéphane(Poulet)"));
		assertTrue(parts.contains("Olivier T"));
		assertTrue(parts.contains("Médoune (Thon)"));
		assertTrue(parts.contains("Cyril (Poulet)"));
		assertTrue(parts.contains("Youssef I (Thon)"));
		assertTrue(parts.contains("Amine Belhaj (thon)"));
		assertTrue(parts.contains("Ali GT (thon)"));
		assertTrue(parts.contains("Raphaël Limbach"));
	}

	@Test
	public void testGetDates()
	{
		final Calendar cal = Calendar.getInstance();
		
		for (final Date date : getParser().getDates())
		{
			cal.setTime(date);
			assertEquals(2014, cal.get(Calendar.YEAR));
			assertEquals(1, cal.get(Calendar.MONTH));
			final int day = cal.get(Calendar.DAY_OF_MONTH);
			assertTrue("jour min = 4 or " + day, day >= 4);
			assertTrue("jour max = 28 or " + day, day <= 28);
		}
	}

	@Test
	public void testGetColumn()
	{
		final Calendar cal = Calendar.getInstance();
		cal.set(2014, 1, 18); // 18/02/2014
		assertEquals(4, getParser().getColumn(cal.getTime()));

		cal.set(2014, 1, 3); // 03/02/2014
		assertEquals(-1, getParser().getColumn(cal.getTime()));

		cal.set(2014, 1, 4); // 04/02/2014
		assertEquals(0, getParser().getColumn(cal.getTime()));

		cal.set(2014, 1, 29); // 29/02/2014
		assertEquals(-1, getParser().getColumn(cal.getTime()));
	}

	@Test
	public void testGetParticipantsDate()
	{
		final Calendar cal = Calendar.getInstance();
		cal.set(2014, 1, 18); // 18/02/2014
		
		final List<String> parts = getParser().getParticipants(cal.getTime());
		assertEquals(4, parts.size());
		assertTrue(parts.contains("Mickael Hamon (thon)"));
		assertTrue(parts.contains("Nicolas M (thon)"));
		assertTrue(parts.contains("Dany K (Poulet)"));
		assertTrue(parts.contains("Romain (Americain J)"));
	}

}
