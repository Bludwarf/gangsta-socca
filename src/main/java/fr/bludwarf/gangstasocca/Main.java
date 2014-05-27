package fr.bludwarf.gangstasocca;

import java.awt.Desktop;
import java.io.File;
import java.net.URI;

import fr.bludwarf.gangstasocca.output.MatchWriter;

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
		
		rep.save();
		
		// Ouverture du fichier HTML auto
//		OSUtils.browse(match.getMail());
//		Runtime.getRuntime().exec(match.getMail());
		final Desktop desktop = Desktop.getDesktop();
		desktop.mail(new URI(match.getMail()));
		desktop.open(outFile);
	}
}
