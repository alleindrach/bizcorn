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

	private Long id;

	/**
	 * 
	 */

	private String groupName;



	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}

}
