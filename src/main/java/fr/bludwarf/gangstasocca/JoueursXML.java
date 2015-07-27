package fr.bludwarf.gangstasocca;

import java.util.ArrayList;
import java.util.Date;
import java.util.Set;
import java.util.TreeSet;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;
import org.simpleframework.xml.core.Persist;

import fr.bludwarf.commons.xml.ElementXML;
import fr.bludwarf.gangstasocca.stats.Stats;

@Root(name="joueurs")
public class JoueursXML extends ElementXML<Set<Joueur>>
{
	
	/** URL du Doodle courant */
	@Attribute
	String doodle;
	
	/** Email de l'orga */
	@Attribute(name="email-organisateur")
	String emailOrga;
	
	@ElementList(inline=true, entry="joueur")
	ArrayList<JoueurXML> joueurs;
	
	private static class JoueurXML extends ElementXML<Joueur>
	{
		
		@Attribute
		String nom;
		
		@Attribute(required=false)
		String email;
		
		@Attribute(required=false)
		Double elo = null;
		
		@Attribute(name = "dernier-match", required=false)
		Date dernierMatch;
		
		@Attribute(name = "nb-matches", required = false)
		Integer nbMatches = null;
		
		
		@ElementList(required=false, inline=true, entry="pseudo")
		ArrayList<String> pseudos;

		@Override
		public void fromObject(Joueur obj) throws Exception
		{
			nom = obj.getNom();
			email = obj.getEmail();
			pseudos = new ArrayList<String>(obj.getPseudos());
		}

		@Override
		public Joueur toObject() throws Exception
		{
			final Joueur obj = new Joueur(nom);
			obj.setEmail(email);
			
			if (pseudos != null)
			{
				for (final String pseudo : pseudos)
				{
					obj.addPseudo(pseudo);
				}
			}
			
			return obj;
		}
		
	}

	@Override
	public void fromObject(Set<Joueur> obj) throws Exception
	{
		joueurs = new ArrayList<JoueursXML.JoueurXML>();
		for (final Joueur joueur : obj)
		{
			final JoueurXML jXML = new JoueurXML();
			jXML.fromObject(joueur);
			joueurs.add(jXML);
		}
		
		final JoueursRepository repo = JoueursRepository.getInstance();
		doodle = repo.getDoodleURL();
		emailOrga = repo.getEmailOrga();
	}

	@Override
	public Set<Joueur> toObject() throws Exception
	{
		Set<Joueur> obj = new TreeSet<Joueur>();
		
		if (joueurs != null)
		{
			for (final JoueurXML jXML : joueurs)
			{
				final Joueur j = jXML.toObject();
				obj.add(j);
			}
		}
		
		// ATTENTION : On met à jour aussi l'URL par défaut du Connecteur
		if (doodle != null)
		{
			final JoueursRepository repo = JoueursRepository.getInstance();
			repo.setDoodleURL(doodle);
			repo.setEmailOrga(emailOrga);
		}
		
		return obj;
	}
	
	@Persist
	protected void persist()
	{
		// TODO : stats : ELO et nb matches
	}

}
