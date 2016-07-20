package fr.bludwarf.gangstasocca.tirages;

import java.util.Collection;
import java.util.List;

import fr.bludwarf.gangstasocca.Joueur;

public interface TirageAuSort
{
	public List<Joueur> tirer(int nb, Collection<Joueur> joueurs) throws Exception;
	public List<Joueur> getJoueurs() throws Exception;
	public List<Joueur> getRemplaçants() throws Exception;
}
