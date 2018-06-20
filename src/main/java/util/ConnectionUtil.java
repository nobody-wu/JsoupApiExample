package util;

import org.jsoup.Connection;
import org.jsoup.Jsoup;

import javax.net.ssl.*;
import java.security.SecureRandom;
import java.security.Security;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Map;

/**
 * 解析、获取connection工具
 *
 * @author wujiapeng
 */
public class ConnectionUtil {

    static {
        trustEveryone();
    }

    /**
     * 使用https访问
     */
    private static void trustEveryone() {
        try {
            Security.addProvider(new com.sun.net.ssl.internal.ssl.Provider());
            HttpsURLConnection.setDefaultHostnameVerifier(new HostnameVerifier() {
                public boolean verify(String hostname, SSLSession session) {
                    return true;
                }
            });

            SSLContext context = SSLContext.getInstance("TLS");
            context.init(null, new X509TrustManager[]{new X509TrustManager() {
                public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                }

                public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                }

                public X509Certificate[] getAcceptedIssuers() {
                    return new X509Certificate[0];
                }
            }}, new SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(context.getSocketFactory());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取连接地址
     *
     * @param url 访问地址
     * @return 返回Connection
     */
    public static Connection getConnection(String url, Map<String, String> cookies) {
        Connection connection = Jsoup
                .connect(url)
                .timeout(60000)
                .ignoreContentType(true)
                .header("accept-language", "zh-cn,zh;q=0.9,en;q=0.8")
                .header("user-agent", "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_13_3) AppleWebKit/604.5.6 (KHTML, like Gecko) Version/11.0.3 Safari/604.5.6");
        if (cookies != null) {
            connection.cookies(cookies);
        }
        return connection;
    }

    /**
     * 获取连接地址
     *
     * @param url 访问地址
     * @return 返回Connection
     */
    public static Connection getConnection(String url) {
        return getConnection(url, null);
    }

}
