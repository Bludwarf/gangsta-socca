package fr.bludwarf.gangstasocca.output;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Properties;

import fr.bludwarf.commons.StringBuilder;
import fr.bludwarf.commons.io.FileUtils;
import fr.bludwarf.gangstasocca.GangstaSoccaProperties;
import fr.bludwarf.gangstasocca.ical.Meeting;

public class MeetingWriter
{
    //define somewhere the icalendar date format
    private static SimpleDateFormat SDF_ICAL = new SimpleDateFormat("yyyyMMdd'T'HHmm'00'");
    
	// TODO : utiliser JavaMail
	/*
	 * <dependency>
	<groupId>javax.mail</groupId>
	<artifactId>mail</artifactId>
	<version>1.4</version>
</dependency>

	 */
//	private BodyPart buildHtmlTextPart() throws MessagingException {
//		 
//        MimeBodyPart descriptionPart = new MimeBodyPart();
// 
//        //Note: even if the content is spcified as being text/html, outlook won't read correctly tables at all
//        // and only some properties from div:s. Thus, try to avoid too fancy content
//        String content = "<font size="\"2\"">simple meeting invitation</font>";
//        descriptionPart.setContent(content, "text/html; charset=utf-8");
// 
//        return descriptionPart;
//    }
// 
// 
//    private BodyPart buildCalendarPart() throws Exception {
// 
//        BodyPart calendarPart = new MimeBodyPart();
// 
//        Calendar cal = Calendar.getInstance();
//        cal.add(Calendar.DAY_OF_MONTH, 1);
//        Date start = cal.getTime();
//        cal.add(Calendar.HOUR_OF_DAY, 3);
//        Date end = cal.getTime();
// 
//        //check the icalendar spec in order to build a more complicated meeting request
//        String calendarContent = buildCalendar(start, end);
// 
//        calendarPart.addHeader("Content-Class", "urn:content-classes:calendarmessage");
//        calendarPart.setContent(calendarContent, "text/calendar;method=CANCEL");
// 
//        return calendarPart;
//    }

	/**
	 * @param start
	 * @param end
	 * @param mailto "mailto:***" 
	 * @param orga email de l'orga
	 * @return
	 * @throws IOException 
	 */
	public static String buildCalendar(Meeting meeting) throws IOException
	{		
		final Properties props = new Properties();
		props.setProperty("start",			SDF_ICAL.format(meeting.getDebut()));
		props.setProperty("end",			SDF_ICAL.format(meeting.getFin()));
		props.setProperty("titre",			meeting.getTitre());
		props.setProperty("orga",			meeting.getOrga());
		props.setProperty("description",	meeting.getDescription());
		
		StringBuilder sb = new StringBuilder();
		final String participantFormat = GangstaSoccaProperties.getInstance().getString("ical.participant");
		for (final String email : meeting.getParticipants())
		{
			sb.appendLine(String.format(participantFormat, email));
		}
		props.setProperty("participants",	sb.toString());
		
		return FileUtils.readTemplate(GangstaSoccaProperties.getInstance().getString("ical.template"), props);
	}
}
