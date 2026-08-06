package com.nlizzard.horizonhub.utils;

import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;
import org.springframework.stereotype.Component;

/**
 * 富文本 XSS 净化器。
 * <p>
 * 用 jsoup 白名单清洗用户提交的 HTML 正文（文章/评论），剥离 {@code <script>}、
 * 内联事件（onclick 等）、javascript: 协议等存储型 XSS 载荷，只保留论坛富文本需要的
 * 结构、排版、图片、链接等标签。图片/链接采用相对路径（指向本系统 /api/file/...），
 * 因此开启 {@code preserveRelativeLinks} 以避免相对 URL 被误删。
 * <p>
 * 安全权衡：不放开任意内联 {@code style}（任意 CSS 可用于点击劫持/隐藏覆盖），仅保留
 * 标签结构带来的格式。
 */
@Component
public class HtmlSanitizer {

    /**
     * 论坛富文本白名单：relaxed（含图片、表格、列表、标题、链接等常用标签），保留相对路径。
     */
    private static final Safelist SAFELIST = Safelist.relaxed()
            .preserveRelativeLinks(true);

    /**
     * 清洗 HTML；入参为 null/空时原样返回。
     */
    public String clean(String html) {
        if (html == null || html.isEmpty()) {
            return html;
        }
        return Jsoup.clean(html, SAFELIST);
    }
}
