package fr.bludwarf.gangstasocca;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.JOptionPane;

import fr.bludwarf.commons.StringUtils;
import fr.bludwarf.gangstasocca.exceptions.MatcherException;

public class DoodleElementMatcher
{
	static final String MSG_TYPE = "Nom doodle \"%s\" de type %d";
	
	/** Log */
	protected static org.apache.log4j.Logger LOG = org.apache.log4j.Logger.getLogger(DoodleElementMatcher.class);

	/** Pseudos entrés par l'utilisateur */
	protected static Map<String, String> NOMS_DOODLE = new HashMap<String, String>();

	/**
	 * @param nomDoodle
	 * @param joueurs
	 * @return
	 * @throws MatcherException si impossible de départager
	 */
	public static String getElementByNomDoodle(final String nomDoodle, final List<String> joueurs)
			throws Exception
	{
		return getElementByNomDoodle(nomDoodle, joueurs, false);
	}

	/**
	 * @param nomDoodle
	 * @param joueurs
	 * @param prompt si erreur alors on demande à l'utilisateur
	 * @return <code>null</code> si aucun choix
	 * @throws MatcherException si impossible de départager
	 */
	public static String getElementByNomDoodle(final String nomDoodle, final Collection<String> joueurs,
			boolean prompt) throws MatcherException
	{		
		// Pseudo déjà connu ?
		if (NOMS_DOODLE.containsKey(nomDoodle))
		{
			return NOMS_DOODLE.get(nomDoodle);
		}
		
		// Type de pseudo :
		
		// 1 - Un seul mot
		final String[] tokens = nomDoodle.split(" +");
		if (tokens.length == 1)
		{
			if (LOG.isDebugEnabled()) LOG.debug(String.format(MSG_TYPE, nomDoodle, 1));
			
			// Le prénom nom contient le pseudo ?
			final Collection<String> joueursPre = getElementsMatchContains(nomDoodle, joueurs);
			if (joueursPre.size() == 1)
			{
				return (String) joueursPre.toArray()[0];
			}
			
			return getElementByPseudoWithLeven(nomDoodle, joueurs);
		}
		
		// 2 - Prénom + nom (ou initiales)
		else if (tokens.length == 2)
		{
			if (LOG.isDebugEnabled()) LOG.debug(String.format(MSG_TYPE, nomDoodle, 2));
			
			Collection<String> joueursPre = getElementsMatchPrefix(tokens[0], joueurs);
			if (joueursPre.size() == 1)
			{
				return (String) joueursPre.toArray()[0];
			}
			
			joueursPre = getElementsMatchPrefix(tokens[1], joueursPre);
			
			if (joueursPre.size() != 1)
			{
				if (prompt)
					return prompt(String.format("A quel joueur/sandwich correspond le pseudo/dwich :\n%s ?", nomDoodle), nomDoodle, joueursPre, false);
				else
					throw new MatcherException(String.format("Impossible de trouver un seul \"%s\" dans la liste des joueurs", nomDoodle));
			}
			return (String) joueursPre.toArray()[0];
		}
		
		if (prompt)
			return prompt(String.format("A quel joueur/sandwich correspond le pseudo/dwich :\n%s ?", nomDoodle), nomDoodle, joueurs, false);
		else
			throw new MatcherException(String.format("Type de pseudo inconnu pour \"%s\"", nomDoodle));
	}

	/**
	 * La liste des joueurs qui ont sont le plus proche de ce pseudo en comparant par préfixe
	 * @param pseudo
	 * @param elements
	 * @return
	 */
	protected static List<String> getElementsMatchTokens(String pseudo, List<String> elements)
	{
		return null;
		// TODO
	}

	/**
	 * La liste des joueurs qui ont sont le plus proche de ce pseudo en comparant par préfixe pour chaque token du prénomNom (lowercase)
	 * @param nomDoodle
	 * @param elements
	 * @return
	 */
	public static List<String> getElementsMatchPrefix(String nomDoodle, Collection<String> elements)
	{
		Map<Integer, ArrayList<String>> matchedPrefix = new HashMap<Integer, ArrayList<String>>();
		final String pseudoLow = nomDoodle.toLowerCase();
		
		int dMax = 0;
		for (final String joueur : elements)
		{
			for (final String token : joueur.toLowerCase().split(" +"))
			{
				final int d = StringUtils.getCommonPrefix(new String[]{pseudoLow, token}).length();
				
				if (d >= dMax)
				{
					dMax = d;
					ArrayList<String> list = matchedPrefix.get(d);
					if (list == null) list = new ArrayList<String>();
					if (!list.contains(joueur)) list.add(joueur);
					matchedPrefix.put(d, list);
				}
			}
		}
		
		if (LOG.isDebugEnabled())
		{
			LOG.debug(String.format("Le(s) joueur(s) qui correspond(ent) le plus au pseudo \"%1$s\" est(sont) \"%2$s\" (préfixe=\"%3$s\")",
				nomDoodle,
				matchedPrefix.get(dMax),
				nomDoodle.substring(0, dMax)));
		}
		
		return matchedPrefix.get(dMax);
	}

	/**
	 * Pseudo contenu dans prenom nom (ignoreCase)
	 * @param nomDoodle
	 * @param elements
	 * @return
	 */
	public static List<String> getElementsMatchContains(String nomDoodle, Collection<String> elements)
	{
		List<String> contains = new ArrayList<String>();
		final String pseudoLow = nomDoodle.toLowerCase();
		
		for (final String element : elements)
		{
			for (final String token : element.split(" +"))
			{
				if (token.toLowerCase().contains(pseudoLow))
				{
					contains.add(element);
					break;
				}
			}
		}
		
		if (LOG.isDebugEnabled())
		{
			LOG.debug(String.format("Le(s) élément(s) qui correspond(ent) le plus au nom Doodle \"%1$s\" est(sont) \"%2$s\"",
				nomDoodle,
				contains));
		}
		
		return contains;
	}

	/**
	 * @param nomDoodle
	 * @param elements
	 * @return
	 */
	public static String getElementByPseudoWithLeven(final String nomDoodle, final Collection<String> elements)
	{
		
		if (elements.size() == 1)
		{
			return (String) elements.toArray()[0];
		}
		
		String matchedElement = null; // élément le plus proche du pseudo
		double levenMax = 0; // correspondance la plus élevée %
		for (final String element : elements)
		{
			// On découpe le nom du élément en token ainsi que le pseudo et on prend le matching leven max
			double leven = 0;
			for (final String tokenP : nomDoodle.split(" +"))
			{
				for (final String tokenJ : element.split(" +"))
				{
					final double levenToken = StringUtils.getLevenshteinMatching(tokenP, tokenJ);
					if (levenToken > leven)
					{
						leven = levenToken;
					}
				}
			}
			
			// Si leven > on remplace
			// si = alors on compare le prénom/nom en entier (et plus par token)
			if (leven > levenMax || leven > 0 && leven == levenMax &&
					StringUtils.getLevenshteinMatching(nomDoodle, matchedElement) > StringUtils.getLevenshteinMatching(nomDoodle, element))
			{
				levenMax = leven;
				matchedElement = element;
			}
		}
		
		if (LOG.isDebugEnabled())
		{
			LOG.debug(String.format("L'élément qui correspond le plus au pseudo \"%1$s\" est \"%2$s\" (corres. à %3$d%%)",
				nomDoodle,
				matchedElement,
				Math.round(levenMax)));
		}
		return matchedElement;
	}

	public DoodleElementMatcher()
	{
		super();
	}
	
	public static String prompt(String question, String nomDoodle, List<String> elements)
	{
		return prompt(question, nomDoodle, elements, false);
	}

	public static String prompt(String question, String nomDoodle, Set<String> nomsDoodle,
			boolean autoSelect)
	{
		return prompt(question, nomDoodle, new ArrayList<String>(nomsDoodle), autoSelect);
	}

	public static String prompt(String question, String nomDoodle, Collection<String> elements, boolean autoSelect)
	{
		Object[] possibilities = elements.toArray();
		
		if (possibilities.length == 0)
		{
			return null;
		}
		
		Object selected = possibilities[0];
		
		if (autoSelect)
		{
			try
			{
				selected = getElementByNomDoodle(nomDoodle, elements, false);
			} catch (Exception e)
			{
				LOG.warn("Impossible de sélectionner automatiquement le bon élement dans la liste", e);
				selected = possibilities[0];
			}
		}
		
		String s = (String)JOptionPane.showInputDialog(
            null,
            question,
            "Question",
            JOptionPane.QUESTION_MESSAGE,
            null,
            possibilities,
            selected);
		
		if (StringUtils.isNotEmpty(s))
		{
			NOMS_DOODLE.put(nomDoodle, s);
		}
		
		return s;
	}
}