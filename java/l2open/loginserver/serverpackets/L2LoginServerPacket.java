/*
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2, or (at your option) any later version. This
 * program is distributed in the hope that it will be useful, but WITHOUT ANY
 * WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR
 * A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the Free Software Foundation, Inc., 59 Temple
 * Place - Suite 330, Boston, MA 02111-1307, USA.
 * http://www.gnu.org/copyleft/gpl.html
 */
package l2open.loginserver.serverpackets;

import l2open.config.ConfigValue;
import l2open.extensions.network.SendablePacket;
import l2open.loginserver.L2LoginClient;

import java.util.logging.Logger;

/**
 *
 * @author KenM
 */
public abstract class L2LoginServerPacket extends SendablePacket<L2LoginClient>
{
	public static Logger _log = Logger.getLogger(L2LoginServerPacket.class.getName());

	@Override
	protected int getHeaderSize()
	{
		return 2;
	}

	protected abstract void write_impl();

	@Override
	protected void write()
	{
		try
		{
			
			if(ConfigValue.DebugServerPackets)
				_log.info("LoginServer send to Client("+getClient().toString()+") packets: " + getType());
			write_impl();
		}
		catch(Throwable t)
		{
			t.printStackTrace();
		}
	}

	@Override
	protected void writeHeader(int dataSize)
	{
		writeH(dataSize + getHeaderSize());
	}

	public String getType()
	{
		return "[S] " + getClass().getSimpleName();
	}
}
