package l2open.loginserver;

import javolution.io.UTF8StreamReader;
import javolution.util.FastMap;
import javolution.xml.stream.XMLStreamConstants;
import javolution.xml.stream.XMLStreamException;
import javolution.xml.stream.XMLStreamReaderImpl;
import l2open.config.ConfigValue;
import l2open.database.DatabaseUtils;
import l2open.database.FiltredPreparedStatement;
import l2open.database.L2DatabaseFactory;
import l2open.database.ThreadConnection;
import l2open.gameserver.loginservercon.AdvIP;
import l2open.loginserver.gameservercon.GameServerInfo;
import l2open.util.GArray;
import l2open.util.Rnd;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.*;
import java.security.spec.RSAKeyGenParameterSpec;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Map;
import java.util.Map.Entry;
import java.util.logging.Logger;

public class GameServerTable
{
	private static Logger _log = Logger.getLogger(GameServerTable.class.getName());
	private static GameServerTable _instance;

	// Server Names Config
	static FastMap<Integer, String> _serverNames = new FastMap<Integer, String>().setShared(true);

	// Game Server Table
	private final Map<Integer, GameServerInfo> _gameServerTable = new FastMap<Integer, GameServerInfo>().setShared(true);
	public static Map<Integer, GameInfo> _game_proxy = new FastMap<Integer, GameInfo>().setShared(true);

	// RSA Config
	private KeyPair[] _keyPairs;

	public static void load() throws SQLException, GeneralSecurityException
	{
		if(_instance == null)
			_instance = new GameServerTable();
		else
			throw new IllegalStateException("Load can only be invoked a single time.");
	}

	public static GameServerTable getInstance()
	{
		return _instance;
	}

	public GameServerTable() throws SQLException, NoSuchAlgorithmException, InvalidAlgorithmParameterException
	{
		loadServerNames();
		_log.info("Loaded " + _serverNames.size() + " server names");

		//loadRegisteredGameServers();
		_log.info("Loaded " + _gameServerTable.size() + " registered Game Servers");

		loadRSAKeys();
		_log.info("Cached " + _keyPairs.length + " RSA keys for Game Server communication.");
	}

	private void loadRSAKeys() throws NoSuchAlgorithmException, InvalidAlgorithmParameterException
	{
		KeyPairGenerator keyGen = KeyPairGenerator.getInstance("RSA");
		RSAKeyGenParameterSpec spec = new RSAKeyGenParameterSpec(512, RSAKeyGenParameterSpec.F4);
		keyGen.initialize(spec);

		_keyPairs = new KeyPair[ConfigValue.RSAKeyPairs];
		for(int i = 0; i < _keyPairs.length; i++)
			_keyPairs[i] = keyGen.genKeyPair();
	}

	private void loadServerNames()
	{
		InputStream in = null;
		try
		{
			in = new FileInputStream("servername.xml");
			XMLStreamReaderImpl xpp = new XMLStreamReaderImpl();
			xpp.setInput(new UTF8StreamReader().setInput(in));
			for(int e = xpp.getEventType(); e != XMLStreamConstants.END_DOCUMENT; e = xpp.next())
				if(e == XMLStreamConstants.START_ELEMENT)
					if(xpp.getLocalName().toString().equals("server"))
					{
						Integer id = new Integer(xpp.getAttributeValue(null, "id").toString());
						String name = xpp.getAttributeValue(null, "name").toString();
						_serverNames.put(id, name);
					}
		}
		catch(FileNotFoundException e)
		{
			_log.warning("servername.xml could not be loaded: file not found");
		}
		catch(XMLStreamException xppe)
		{
			xppe.printStackTrace();
		}
		finally
		{
			try
			{
				if(in != null)
					in.close();
			}
			catch(Exception e)
			{}
		}
	}

	/*private void loadRegisteredGameServers() throws SQLException
	{
		ThreadConnection con = null;
		FiltredPreparedStatement statement = null;
		ResultSet rset = null;
		int id;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement("SELECT * FROM gameservers");
			rset = statement.executeQuery();
			GameServerInfo gsi;
			while(rset.next())
			{
				id = rset.getInt("server_id");
				gsi = new GameServerInfo(id, stringToHex(rset.getString("hexid")));
				_gameServerTable.put(id, gsi);
			}
		}
		finally
		{
			DatabaseUtils.closeDatabaseCSR(con, statement, rset);
		}
	}*/

	public Map<Integer, GameServerInfo> getRegisteredGameServers()
	{
		return _gameServerTable;
	}

	public GameServerInfo getRegisteredGameServerById(int id)
	{
		if(ConfigValue.LoginProxyEnable)
			for(GameServerInfo gsi : _gameServerTable.values())
				if(gsi.gi != null && gsi.gi._proxy.containsKey(id))
					return gsi;
		return _gameServerTable.get(id);
	}

	public boolean registerWithFirstAvailableId(GameServerInfo gsi)
	{
		// avoid two servers registering with the same "free" id
		synchronized (_gameServerTable)
		{
			for(Entry<Integer, String> entry : _serverNames.entrySet())
				if(!_gameServerTable.containsKey(entry.getKey()))
				{
					_gameServerTable.put(entry.getKey(), gsi);
					gsi.setId(entry.getKey());
					return true;
				}
		}
		return false;
	}

	public boolean register(int id, GameServerInfo gsi)
	{
		// avoid two servers registering with the same id
		synchronized (_gameServerTable)
		{
			if(!_gameServerTable.containsKey(id))
			{
				_gameServerTable.put(id, gsi);
				gsi.setId(id);
				return true;
			}
		}
		return false;
	}

	public void registerServerOnDB(GameServerInfo gsi)
	{
		this.registerServerOnDB(gsi.getHexId(), gsi.getId(), gsi.getExternalHost());
	}

	public void registerServerOnDB(byte[] hexId, int id, String externalHost)
	{
		ThreadConnection con = null;
		FiltredPreparedStatement statement = null;
		try
		{
			con = L2DatabaseFactory.getInstance().getConnection();
			statement = con.prepareStatement("INSERT INTO gameservers (hexid,server_id,host) values (?,?,?)");
			statement.setString(1, hexToString(hexId));
			statement.setInt(2, id);
			statement.setString(3, externalHost);
			statement.executeUpdate();
		}
		catch(SQLException e)
		{
			_log.warning("SQL error while saving gameserver: " + e);
		}
		finally
		{
			DatabaseUtils.closeDatabaseCS(con, statement);
		}
	}

	public String getServerNameById(int id)
	{
		return getServerNames().get(id);
	}

	public Map<Integer, String> getServerNames()
	{
		return _serverNames;
	}

	public KeyPair getKeyPair()
	{
		return _keyPairs[Rnd.get(_keyPairs.length)];
	}

	private byte[] stringToHex(String string)
	{
		return new BigInteger(string, 16).toByteArray();
	}

	private String hexToString(byte[] hex)
	{
		if(hex == null)
			return "null";
		return new BigInteger(hex).toString(16);
	}

	public GArray<String> status()
	{
		GArray<String> str = new GArray<String>();
		str.add("There are " + _gameServerTable.size() + " GameServers");
		for(GameServerInfo gsi : _gameServerTable.values())
			str.add(gsi.toString());
		return str;
	}

	public Boolean CheckSubNet(String ip, AdvIP advip)
	{
		String[] temp = ip.split("\\.");
		String[] temp2 = advip.bitmask.split("\\.");
		String result = "";
		for(int i = 0; i < temp.length; i++)
			result += (Integer.valueOf(temp[i]) & Integer.valueOf(temp2[i])) + ".";
		return result.equals(advip.ipmask.replace("\\.", "") + ".");
	}
}
