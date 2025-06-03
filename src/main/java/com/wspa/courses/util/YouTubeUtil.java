/* util/YouTubeUtil.java */
package com.wspa.courses.util;

public final class YouTubeUtil {

    private YouTubeUtil() {}

    /** Преобразует watch- или youtu.be-ссылку к формату /embed/{id} */
    public static String toEmbed(String url) {
        if (url == null) return null;

        if (url.contains("youtu.be/")) {                // short link
            String id = url.substring(url.lastIndexOf("/") + 1);
            return "https://www.youtube.com/embed/" + id;
        }
        if (url.contains("watch?v=")) {                 // long link
            return url.replace("watch?v=", "embed/");
        }
        return url;                                     // уже embed или внешний mp4
    }
}
