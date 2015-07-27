package fr.bludwarf.gangstasocca;

public class JoueurInconnuException extends Exception
{

	/**
	 * 
	 */
	private static final long serialVersionUID = -5389052238820377011L;

	public JoueurInconnuException()
	{
	}

	public JoueurInconnuException(String message)
	{
		super(message);
	}

	public JoueurInconnuException(Throwable cause)
	{
		super(cause);
	}

	public JoueurInconnuException(String message, Throwable cause)
	{
		super(message, cause);
	}

}
