public interface TCPConnectionListener {

    /**
     * Спрацьовує, коли TCP з'єднання готове для використання.
     * @param tcpConnection готове TCP з'єднання
     */
    void onConnectionReady(TCPConnection tcpConnection);

    /**
     * Спрацьовує, коли отримано нове повідомлення через TCP з'єднання.
     * @param tcpConnection TCP з'єднання, через яке отримано повідомлення
     * @param value отримане значення (повідомлення)
     */
    void onReceiveString(TCPConnection tcpConnection, String value);

    /**
     * Спрацьовує, коли з'єднання було розірвано або втрачено з'єднання з сервером.
     * @param tcpConnection розірване TCP з'єднання
     */
    void onDisconnect(TCPConnection tcpConnection);

    /**
     * Спрацьовує, коли виникає виключна ситуація (помилка) під час роботи з TCP з'єднанням.
     * @param tcpConnection TCP з'єднання, при виникненні помилки
     * @param e виключна ситуація (помилка)
     */
    void onExepction(TCPConnection tcpConnection, Exception e);

}
