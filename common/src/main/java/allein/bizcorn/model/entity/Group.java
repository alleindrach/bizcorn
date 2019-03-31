package allein.bizcorn.model.entity;


import allein.bizcorn.model.facade.IGroup;

import java.io.Serializable;

/**
 * <p>
 * 
 * </p>
 *
 * @author allein
 * @since 2019-03-29
 */
public class Group implements IGroup {

    private static final long serialVersionUID = 1L;

	/**
	 * 
	 */

	private String id;

	/**
	 * 
	 */

	private String groupName;



	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

}
