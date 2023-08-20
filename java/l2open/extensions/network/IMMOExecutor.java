package l2open.extensions.network;


public interface IMMOExecutor<T extends MMOClient>
{
	public void execute(Runnable r);
}