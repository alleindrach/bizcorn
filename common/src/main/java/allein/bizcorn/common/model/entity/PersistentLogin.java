package allein.bizcorn.common.model.entity;

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

public class PersistentLogin implements Serializable {

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
	private String series;

	/**
	 * 
	 */
	private String token;

	/**
	 * 
	 */

	private Date lastUsed;



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
