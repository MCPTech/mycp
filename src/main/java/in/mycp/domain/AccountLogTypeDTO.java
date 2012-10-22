package in.mycp.domain;

public class AccountLogTypeDTO {

	private int id;
	private String name;
	
	
	
	public AccountLogTypeDTO() {
		super();
	}
	public AccountLogTypeDTO(int id, String name) {
		super();
		this.id = id;
		this.name = name;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	
	
}
