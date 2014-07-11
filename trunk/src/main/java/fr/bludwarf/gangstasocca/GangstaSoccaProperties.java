package fr.bludwarf.gangstasocca;

import org.apache.commons.configuration.ConfigurationException;

import fr.bludwarf.commons.PropertiesConfiguration;

public class GangstaSoccaProperties extends PropertiesConfiguration
{

	private static final String DEFAULT_FILE = "GangstaSocca.properties";
	
	/** instance */
	private static GangstaSoccaProperties _instance = null;

	private GangstaSoccaProperties() throws ConfigurationException
	{
		super(DEFAULT_FILE);
	}

	/**
	 * @return l'instance de GangstaSoccaProperties
	 */
	public static final synchronized GangstaSoccaProperties getInstance()
	{
		if (_instance == null)
		{
			try
			{
				_instance = new GangstaSoccaProperties();
			} catch (ConfigurationException e)
			{
				e.printStackTrace();
			}
		}
		return _instance;
	}

}
