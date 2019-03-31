package allein.bizcorn.model.entity;

import allein.bizcorn.model.facade.IPersistentLogin;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 * 
 * </p>
 *
 * @author allein
 * @since 2019-03-29
 */

public class PersistentLogin implements IPersistentLogin {

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
	private String series;

	/**
	 * 
	 */
	private String token;

	/**
	 * 
	 */

	private Date lastUsed;



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

	public String getSeries() {
		return series;
	}

	public void setSeries(String series) {
		this.series = series;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public Date getLastUsed() {
		return lastUsed;
	}

	public void setLastUsed(Date lastUsed) {
		this.lastUsed = lastUsed;
	}

}
