package hello;

public class Account {
	private String username;
	private String password;
	private String host;
	
	public Account(String username, String password, String host)
	{
		this.username = username;
		this.password = password;
		this.host = host;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String host) {
		this.host = host;
	}
	
	
}
