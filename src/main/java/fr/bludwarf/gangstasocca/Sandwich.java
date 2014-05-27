package fr.bludwarf.gangstasocca;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import javax.swing.JOptionPane;

import fr.bludwarf.gangstasocca.exceptions.PseudoDejaUtiliseException;

public class Sandwich implements Comparable<Sandwich>
{

	private String _nom;
	private Set<String> _nomsDoodle;
	
	public static Sandwich PAS_DE_SANDWICH = new Sandwich("(pas de sandwich)");
	
	static
	{
		PAS_DE_SANDWICH.addNomDoodle("rien");
	}

	public Sandwich(final String nomComplet)
	{
		_nom = nomComplet;
	}
	
	public String getNom()
	{
		return _nom;
	}

	public Set<String> getNomsDoodle()
	{
		if (_nomsDoodle == null)
		{
			_nomsDoodle = new HashSet<String>();
		}
		return _nomsDoodle;
	}

	public static Sandwich createFromNomDoodle(String nomDoodle,
			SandwichRepository sandwichRepository) throws PseudoDejaUtiliseException, Exception
	{
		if (sandwichRepository.containsNomDoodle(nomDoodle))
		{
			throw new PseudoDejaUtiliseException(String.format(
				"Impossible de créer un nouveau sandwich car le nom Doodle \"%s\" est déjà utilisé par \"%s\"",
				nomDoodle,
				sandwichRepository.getSandwichByNomDoodle(nomDoodle)));
		}
		final String nom = promptNom(nomDoodle);
		if (nom == null)
			return null;
		
		return new Sandwich(nom);
	}

	public void addNomDoodle(String nomDoodle)
	{
		getNomsDoodle().add(nomDoodle);
	}

	public void addNomsDoodle(ArrayList<String> nomsDoodle)
	{
		for (final String nomDoodle : nomsDoodle)
		{
			addNomDoodle(nomDoodle);
		}
	}

	/**
	 * @return
	 */
	private static String promptNom(final String nomDoodle)
	{
		return JOptionPane.showInputDialog(
		    null,
		    String.format("Quel est le nom complet du sandwich identifié par \"%s\" dans le Doodle ?", nomDoodle),
		    nomDoodle);
	}
	
	@Override
	public String toString()
	{
		return getNom();
	}

	public int compareTo(Sandwich o)
	{
		return getNom().compareTo(o.getNom());
	}
	
	public static SortedMap<Sandwich, Integer> getSandwiches(final Set<Joueur> joueurs)
	{
		SortedMap<Sandwich, Integer> dwiches = new TreeMap<Sandwich, Integer>();
		for (final Joueur joueur : joueurs)
		{
			final Sandwich dwich = joueur.getSandwich();
			
			if (isSandwich(dwich))
			{
				int n = 1;
				if (dwiches.containsKey(dwich))
				{
					n += dwiches.get(dwich);
				}
				dwiches.put(dwich, n);
			}
		}
		return dwiches;
	}

	/**
	 * @param dwich
	 * @return <code>true</code> si le sandwich n'est pas null et pas "(aucun sandwich)"
	 */
	public static boolean isSandwich(final Sandwich dwich)
	{
		return dwich != null && dwich.compareTo(PAS_DE_SANDWICH) != 0;
	}
}
