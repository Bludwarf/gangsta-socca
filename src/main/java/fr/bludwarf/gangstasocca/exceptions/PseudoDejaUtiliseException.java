package fr.bludwarf.gangstasocca.exceptions;

public class PseudoDejaUtiliseException extends Exception
{

	/**
	 * 
	 */
	private static final long serialVersionUID = 3565852505849757601L;

	public PseudoDejaUtiliseException()
	{
	}

	public PseudoDejaUtiliseException(String message)
	{
		super(message);
	}

	public PseudoDejaUtiliseException(Throwable cause)
	{
		super(cause);
	}

	public PseudoDejaUtiliseException(String message, Throwable cause)
	{
		super(message, cause);
	}

}
