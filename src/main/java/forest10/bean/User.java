package forest10.bean;

import lombok.Data;

/**
 * @author Forest10
 * 2017/12/7
 */
@Data
public class User {

	private String name;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
