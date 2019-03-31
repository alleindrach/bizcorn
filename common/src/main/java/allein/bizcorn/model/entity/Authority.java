package allein.bizcorn.model.entity;

import allein.bizcorn.model.facade.IAuthority;

import java.io.Serializable;

/**
 * <p>
 * 
 * </p>
 *
 * @author allein
 * @since 2019-03-29
 */
public class Authority implements IAuthority {

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
	private String authority;



	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getAuthority() {
		return authority;
	}

	public void setAuthority(String authority) {
		this.authority = authority;
	}

}
