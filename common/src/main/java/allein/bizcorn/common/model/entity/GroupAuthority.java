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

public class GroupAuthority implements Serializable {

    private static final long serialVersionUID = 1L;

	/**
	 * 
	 */

	private Long id;

	/**
	 * 
	 */

	private Long groupId;

	/**
	 * 
	 */
	private String authority;



	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getGroupId() {
		return groupId;
	}

	public void setGroupId(Long groupId) {
		this.groupId = groupId;
	}

	public String getAuthority() {
		return authority;
	}

	public void setAuthority(String authority) {
		this.authority = authority;
	}

}
