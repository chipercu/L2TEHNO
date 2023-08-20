package l2open.loginserver.crypt;

public interface Crypt
{
	/**
	 * Сравнивает пароль и ожидаемый хеш
	 * @param password
	 * @param hash
	 * @return совпадает или нет
	 */
	public boolean compare(String password, String hash);

	/**
	 * Получает пароль и возвращает хеш
	 * @param password
	 * @return hash
	 */
	public String encrypt(String password) throws Exception;
}