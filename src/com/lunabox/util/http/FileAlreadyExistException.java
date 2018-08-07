package com.lunabox.util.http;

public class FileAlreadyExistException extends Exception
{
	private static final long serialVersionUID = 1L;

	public FileAlreadyExistException(String message)
	{

		super(message);
	}

}
