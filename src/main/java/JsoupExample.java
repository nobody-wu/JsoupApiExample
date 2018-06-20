
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.safety.Whitelist;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

/**
 * @Author: wujiapeng
 * @Description: 测试demo
 * @Date: created in 18:06 2018/6/20
 */
public class JsoupExample {

    public static void main(String[] args) {
        loadDocument();
        loadDocumentByFile();
        loadDocumentByString();
        getFavByHtml();
        getAllLinks();
        getAllImages();
        getUrlMeta();
        getFormItemByHtml();
        updateElementAttr();
        xssToHtml();
    }

    /**
     * 载入文件,从URL加载文档，使用Jsoup.connect()方法从URL加载HTML
     */
    private static void loadDocument() {
        try {
            Document document = Jsoup.connect("http://www.baidu.com").get();
            System.out.println(document.title());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 从文件加载文档
     */
    private static void loadDocumentByFile() {
        try {
            Document document = Jsoup.parse(new File("/Users/cornelius/workspaces/IDEAprojects/cornelius/jsoup-demo/file.html"), "utf-8");
            System.out.println(document.title());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 从String加载文档
     */
    private static void loadDocumentByString() {
        try {
            String html = "<!DOCTYPE html>\n" +
                    "<html lang=\"en\">\n" +
                    "<head>\n" +
                    "    <meta charset=\"UTF-8\">\n" +
                    "    <title>this is loadDocumentByString..</title>\n" +
                    "</head>\n" +
                    "<body>\n" +
                    "\n" +
                    "</body>\n" +
                    "</html>";
            Document document = Jsoup.parse(html);
            System.out.println(document.title());
//            System.out.println(document);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 通过选择期，获取HTML页面上的相关信息
     */
    private static void getFavByHtml() {
        String favImage = "Not Found..";
        try {
            Document document = Jsoup.connect("http://www.baidu.com").get();
            // 假设favicon图像将是HTML文档的<head>部分中的第一个图像
            Element element = document.head().select("link[href~=.*\\.(ico|png)]").first();
            if (Objects.isNull(element)) {
                element = document.head().select("meta[itemprop=image]").first();
                if (Objects.nonNull(element)) {
                    favImage = element.attr("content");
                }
            } else {
                favImage = element.attr("href");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println(favImage);
    }

    /**
     * 获取HTML页面中的所有链接
     */
    private static void getAllLinks() {
        try {
            Document document = Jsoup.connect("http://www.baidu.com").get();
            Elements elements = document.select("a[href]");
            elements.forEach(element -> {
                System.out.println("link:" + element.attr("href"));
                System.out.println("text:" + element.text());
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取HTML页面中的所有图像
     */
    private static void getAllImages() {

        try {
            Document document = Jsoup.connect("http://www.baidu.com").post();
            Elements elements = document.select("img[src~=(?i)\\.(png|jpe?g|gif)]");
            elements.forEach(image -> {
                System.out.println("src : " + image.attr("src"));
                System.out.println("height : " + image.attr("height"));
                System.out.println("width : " + image.attr("width"));
                System.out.println("alt : " + image.attr("alt"));
            });

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取URL的元信息
     * <p>
     * 元信息包括Google等搜索引擎用来确定网页内容的索引为目的。 它们以HTML页面的HEAD部分中的一些标签的形式存在。 要获取有关网页的元信息
     */
    private static void getUrlMeta() {
        try {
            Document document = Jsoup.connect("http://www.baidu.com").post();
            String description = document.select("meta[name=referrer]").get(0).attr("content");
            System.out.println("Meta description : " + description);
            String keywords = document.select("meta[http-equiv=Content-Type]").first().attr("content");
            System.out.println("Meta keyword : " + keywords);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 在HTML页面中获取表单属性
     * <p>
     * 在网页中获取表单输入元素非常简单。 使用唯一ID查找FORM元素; 然后找到该表单中存在的所有INPUT元素。
     */
    private static void getFormItemByHtml() {
        try {
            Document document = Jsoup.connect("http://www.baidu.com").get();
            Element form = document.getElementById("form");

            Elements inputElements = form.getElementsByTag("input");
            inputElements.forEach(element -> {
                String key = element.attr("name");
                String value = element.attr("value");
                System.out.println("Param name: " + key + " \nParam value: " + value);
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 更新元素的属性/内容
     *
     * 只要您使用上述方法找到您想要的元素; 可以使用Jsoup API来更新这些元素的属性或innerHTML。 例如，想更新文档中存在的“rel = nofollow”的所有链接。
     */
    private static void updateElementAttr(){
        try {
            Document document = Jsoup.connect("http://www.baidu.com").get();
            Elements elements = document.select("a[href]");
            System.out.println(elements.get(20).attr("href"));

            //单个更新
            elements.get(20).attr("href", "http://yyy.com");
            System.out.println(elements.get(20).attr("href"));
            System.out.println(elements.get(10).attr("href"));

            //全部更新
            elements.attr("href", "http://xxx.com");
            System.out.println(elements.get(20).attr("href"));
            System.out.println(elements.get(10).attr("href"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 消除不信任的HTML(以防止XSS)
     *
     * 假设在应用程序中，想显示用户提交的HTML片段。 例如 用户可以在评论框中放入HTML内容。
     * 这可能会导致非常严重的问题，如果您允许直接显示此HTML。 用户可以在其中放入一些恶意脚本，并将用户重定向到另一个脏网站。
     * 为了清理这个HTML，Jsoup提供Jsoup.clean()方法。 此方法期望HTML格式的字符串，并将返回清洁的HTML。 要执行此任务，Jsoup使用白名单过滤器。
     * jsoup白名单过滤器通过解析输入HTML(在安全的沙盒环境中)工作，然后遍历解析树，只允许将已知安全的标签和属性(和值)通过清理后输出。
     * 它不使用正则表达式，这对于此任务是不合适的。清洁器不仅用于避免XSS，还限制了用户可以提供的元素的范围：您可以使用文本，强元素，但不能构造div或表元素。
     */
    private static void xssToHtml(){
        String dirtyHTML = "<p><a href='http://www.yiibai.com/' onclick='sendCookiesToMe()'>Link</a></p>";
        String cleanHTML = Jsoup.clean(dirtyHTML, Whitelist.basic());
        System.out.println(cleanHTML);

        Document document = Jsoup.parse(dirtyHTML);
        System.out.println(document.select("p"));
    }


}
