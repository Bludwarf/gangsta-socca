package fr.bludwarf.gangstasocca;

import java.util.ArrayList;
import java.util.SortedSet;
import java.util.TreeSet;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import fr.bludwarf.commons.xml.ElementXML;

@Root(name="joueurs")
public class JoueursXML extends ElementXML<SortedSet<Joueur>>
{
	
	/** URL du Doodle courant */
	@Attribute
	String doodle;
	
	@ElementList(inline=true, entry="joueur")
	ArrayList<JoueurXML> joueurs;
	
	private static class JoueurXML extends ElementXML<Joueur>
	{
		
		@Attribute
		String nom;
		
		@Attribute(required=false)
		String email;
		
		@ElementList(required=false, inline=true, entry="pseudo")
		ArrayList<String> pseudos;

		@Override
		public void fromObject(Joueur obj) throws Exception
		{
			// TODO : pseudos
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
	public void fromObject(SortedSet<Joueur> obj) throws Exception
	{
		joueurs = new ArrayList<JoueursXML.JoueurXML>();
		for (final Joueur joueur : obj)
		{
			final JoueurXML jXML = new JoueurXML();
			jXML.fromObject(joueur);
			joueurs.add(jXML);
		}
		
		doodle = DoodleConnector.URL;
	}

	@Override
	public SortedSet<Joueur> toObject() throws Exception
	{
		SortedSet<Joueur> obj = new TreeSet<Joueur>();
		for (final JoueurXML jXML : joueurs)
		{
			final Joueur j = jXML.toObject();
			obj.add(j);
		}
		
		// ATTENTION : On met à jour aussi l'URL par défaut du Connecteur
		if (doodle != null) DoodleConnector.URL = doodle;
		
		return obj;
	}

}
