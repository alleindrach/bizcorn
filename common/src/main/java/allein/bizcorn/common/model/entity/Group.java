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
public class Group implements Serializable {

    private static final long serialVersionUID = 1L;

	/**
	 * 
	 */

	private Integer id;

	/**
	 * 
	 */

	private String groupName;



	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

}
