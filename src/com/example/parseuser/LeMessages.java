package com.example.parseuser;

public class LeMessages 
{
	String username;
	String message;
	String status; 
	
	public LeMessages(String username, String message, String status)
	{
		this.username = username;
		this.message = message;
		this.status = status;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
	
	

}
