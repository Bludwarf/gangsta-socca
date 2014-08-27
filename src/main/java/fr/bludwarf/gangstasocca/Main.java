package fr.bludwarf.gangstasocca;

import java.awt.Desktop;
import java.io.File;
import java.net.URI;

import fr.bludwarf.commons.io.FileUtils;
import fr.bludwarf.gangstasocca.ical.Meeting;
import fr.bludwarf.gangstasocca.output.MatchWriter;
import fr.bludwarf.gangstasocca.output.MeetingWriter;

public class Main
{
	public static void main(final String[] args) throws Exception
	{
		final JoueursRepository rep = JoueursRepository.getInstance();
		rep.load();
		final DoodleConnector con = new DoodleConnector();
		
//		// Texte
//		final File file = new File("./prochainMatch.txt");
//		MatchWriter.writeProchainMatch(con, file);
		// HTML
		final File file = new File("prochainMatch.html.template");
		final Match match = con.getProchainMatch();
		final File outFile = MatchWriter.writeProchainMatchHTML(match, file);
		
		// Ajout du match
		MatchesRepository.getInstance().add(match);
		
		rep.save();
		
		// Ouverture du fichier HTML auto
//		OSUtils.browse(match.getMail());
//		Runtime.getRuntime().exec(match.getMail());
		final Desktop desktop = Desktop.getDesktop();
		desktop.mail(new URI(match.getMail()));
		desktop.open(outFile);
		
		
		
		final GangstaSoccaProperties props = GangstaSoccaProperties.getInstance();
		
		// Création d'un meeting Outlook
		final File ical = new File(props.getString("ical.file"));
		final String orga = props.getString("match.orga");
		final Meeting meeting = new Meeting(match.getDate(), match.getDateFin());
		meeting.setParticipants(match.getEmails());
		meeting.setOrga(orga);
		meeting.setTitre(match.getTitre());
		meeting.setDescription(props.getString("ical.description"));
		FileUtils.writeStringToFile(ical, MeetingWriter.buildCalendar(meeting));
	}
}
