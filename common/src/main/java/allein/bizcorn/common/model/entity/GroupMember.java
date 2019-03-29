package allein.bizcorn.common.model.entity;

import java.io.Serializable;

/**
 * <p>
 * 
 * </p>
 *
 * @author allein
 * @since 2019-03-29
 */

public class GroupMember implements Serializable {

    private static final long serialVersionUID = 1L;

	/**
	 * 
	 */

	private Integer id;

	/**
	 * 
	 */
	private String username;

	/**
	 * 
	 */

	private Long groupId;



	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public Long getGroupId() {
		return groupId;
	}

	public void setGroupId(Long groupId) {
		this.groupId = groupId;
	}

}
