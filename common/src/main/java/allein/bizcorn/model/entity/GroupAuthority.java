package allein.bizcorn.model.entity;

import allein.bizcorn.model.facade.IGroupAuthority;

import java.io.Serializable;

/**
 * <p>
 * 
 * </p>
 *
 * @author allein
 * @since 2019-03-29
 */

public class GroupAuthority implements IGroupAuthority {

    private static final long serialVersionUID = 1L;

	/**
	 * 
	 */

	private String id;

	/**
	 * 
	 */

	private String groupId;

	/**
	 * 
	 */
	private String authority;



	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getGroupId() {
		return groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	public String getAuthority() {
		return authority;
	}

	public void setAuthority(String authority) {
		this.authority = authority;
	}

}
