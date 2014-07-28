package fr.bludwarf.gangstasocca;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.TreeSet;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;
import org.simpleframework.xml.core.Commit;

import fr.bludwarf.commons.xml.ElementXML;

@Root(name = "matches")
public class Matches extends ElementXML<TreeSet<Match>> implements Iterable<Match>
{
	
	@Attribute
	int nb;
	
	@Attribute
	Date debut;
	
	@Attribute
	Date fin;
	
	/**
	 * Matches triés par date
	 */
	@ElementList(inline = true, entry = "match")
	ArrayList<Match> matches;

	@Override
	public void fromObject(TreeSet<Match> obj) throws Exception
	{
		nb = obj.size();
		debut = obj.first().getDate();
		fin = obj.last().getDate();
		matches = new ArrayList<Match>(obj.size());
		matches.addAll(obj);
	}

	@Override
	public TreeSet<Match> toObject() throws Exception
	{
		if (matches == null)
		{
			return new TreeSet<Match>();
		}
		return new TreeSet<Match>(matches);
	}
	
	@Commit
	public void commit()
	{
		nb = matches.size();
		for (final Match match : matches)
		{
			if (debut == null || match.getDate().compareTo(debut) < 0) debut = match.getDate();
			if (fin   == null || match.getDate().compareTo(fin  ) > 0) fin   = match.getDate();
		}
	}
	
	public ArrayList<Match> getMatches(Joueur joueur) throws Exception
	{
		ArrayList<Match> mj = new ArrayList<Match>();
		for (final Match match : matches)
		{
			if (match.contains(joueur))
			{
//				mj.add(match.toObject());
				mj.add(match);
			}
		}
		return mj;
	}

	public Iterator<Match> iterator()
	{
		return matches.iterator();
	}

	public Match matchPrécédent(Match match)
	{
		final int i = matches.indexOf(match);
		if (i <= 0) return null;
		return matches.get(i - 1);
	}

	public Match last()
	{
		return matches.get(matches.size() - 1);
	}

}
