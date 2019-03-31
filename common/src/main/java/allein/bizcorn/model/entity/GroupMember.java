package allein.bizcorn.model.entity;

import allein.bizcorn.model.facade.IGroupMember;

import java.io.Serializable;

/**
 * <p>
 * 
 * </p>
 *
 * @author allein
 * @since 2019-03-29
 */

public class GroupMember implements IGroupMember {

    private static final long serialVersionUID = 1L;

	/**
	 * 
	 */

	private String id;

	/**
	 * 
	 */
	private String userId;

	/**
	 * 
	 */

	private String groupId;



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

}
