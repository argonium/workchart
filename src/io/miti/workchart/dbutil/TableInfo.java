package io.miti.workchart.dbutil;

/**
 * Class to hold information read from the database info file.
 */
public final class TableInfo implements Comparable<TableInfo>
{
  public String nickName = null;
	public String userName = null;
	public String password = null;
	public String url = null;
	
	/**
	 * Default constructor.
	 */
	public TableInfo()
	{
		super();
	}
	
	
	/**
	 * Constructor.
	 * 
	 * @param sNick nickname
	 * @param sUser user name
	 * @param sPass password
	 * @param sUrl URL
	 */
	public TableInfo(final String sNick, final String sUser,
	                 final String sPass, final String sUrl)
	{
	  nickName = sNick;
	  userName = sUser;
	  password = sPass;
	  url = sUrl;
	}
	
	
	/**
	 * Override of toString().
	 */
	@Override
	public String toString()
	{
		return userName + "/" + password + "/" + url;
	}
	
	
  @Override
  public int compareTo(final TableInfo o)
  {
    return nickName.compareTo(o.nickName);
  }
}
