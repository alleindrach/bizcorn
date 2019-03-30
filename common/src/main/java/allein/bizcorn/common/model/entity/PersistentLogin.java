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

	private Long id;

	/**
	 * 
	 */
	private Long userId;

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



	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
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
