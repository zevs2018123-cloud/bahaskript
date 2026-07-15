package com.bahaskript.render;

import com.bahaskript.model.Content.Block;
import com.bahaskript.model.Content.CardBlock;
import com.bahaskript.model.Content.CardElement;
import com.bahaskript.model.Content.Chip;
import com.bahaskript.model.Content.ChipsBlock;
import com.bahaskript.model.Content.ClientLine;
import com.bahaskript.model.Content.Label;
import com.bahaskript.model.Content.Message;
import com.bahaskript.model.Content.NavLink;
import com.bahaskript.model.Content.Page;
import com.bahaskript.model.Content.ProcessListBlock;
import com.bahaskript.model.Content.RiskBlock;
import com.bahaskript.model.Content.Section;
import com.bahaskript.model.Content.SublblBlock;
import com.bahaskript.model.Content.Tip;
import com.bahaskript.model.Content.WhoBlock;

public final class HtmlRenderer {

    private static final String COPY_BUTTON = "Скопировать";
    private static final String COPY_DONE = "Скопировано ✓";

    private static final String STYLE = """
            :root{
              --bg:#101418; --panel:#171d23; --panel2:#1d242c; --line:#2a333d;
              --gold:#d9a441; --gold-dim:#8a6a2e; --red:#c4554d; --green:#4e8d7c;
              --text:#e8e4da; --dim:#8b93a0;
            }
            *{margin:0;padding:0;box-sizing:border-box}
            html{scroll-behavior:smooth;-webkit-text-size-adjust:100%}
            body{background:var(--bg);color:var(--text);font-family:'Golos Text',system-ui,sans-serif;line-height:1.55}
            .wrap{max-width:720px;margin:0 auto;padding:20px 16px 90px}
            .eyebrow{font-family:'JetBrains Mono',monospace;font-size:11px;letter-spacing:.18em;text-transform:uppercase;color:var(--gold);margin-bottom:10px}
            h1{font-family:'Unbounded',sans-serif;font-weight:700;font-size:clamp(21px,5.5vw,30px);line-height:1.15;margin-bottom:8px}
            .sub{color:var(--dim);font-size:14px;max-width:58ch}
            .frame{background:var(--panel2);border:1px solid var(--line);border-radius:12px;padding:13px 15px;margin-top:14px;font-size:13px}
            .frame b{color:var(--gold)}
            .nav{position:sticky;top:0;z-index:9;background:linear-gradient(180deg,var(--bg) 82%,transparent);padding:12px 0 14px;margin-top:12px;display:flex;gap:8px;overflow-x:auto;-webkit-overflow-scrolling:touch}
            .nav a{flex:0 0 auto;font-family:'JetBrains Mono',monospace;font-size:11.5px;color:var(--dim);text-decoration:none;border:1px solid var(--line);border-radius:99px;padding:7px 12px;background:var(--panel)}
            .nav a:hover,.nav a:focus-visible{color:var(--gold);border-color:var(--gold-dim);outline:none}
            section{margin-top:34px;scroll-margin-top:70px}
            h2{font-family:'Unbounded',sans-serif;font-weight:500;font-size:17.5px;margin-bottom:10px}
            .chips{display:grid;gap:6px;margin-bottom:12px}
            .chip{font-size:12.5px;background:var(--panel2);border:1px solid var(--line);border-radius:9px;padding:8px 11px;color:var(--dim)}
            .chip b{color:var(--text)}
            .chip.crit b{color:var(--gold)}
            .who{font-size:12.5px;color:var(--dim);margin:-4px 0 12px}
            .who b{color:var(--gold)}
            .sublbl{font-family:'JetBrains Mono',monospace;font-size:11px;letter-spacing:.16em;text-transform:uppercase;color:var(--gold);margin:18px 0 10px}
            .card{background:var(--panel);border:1px solid var(--line);border-radius:14px;padding:16px;margin-bottom:12px}
            .lbl{font-family:'JetBrains Mono',monospace;font-size:10.5px;letter-spacing:.14em;text-transform:uppercase;color:var(--dim);margin-bottom:8px}
            .client{font-size:13.5px;background:var(--bg);border-left:3px solid var(--red);padding:9px 12px;border-radius:0 8px 8px 0;margin-bottom:10px}
            .client b{color:var(--red)}
            .msg{position:relative;background:#0c1013;border:1px solid var(--line);border-radius:10px;padding:13px 14px 42px;font-family:'JetBrains Mono',monospace;font-size:13px;line-height:1.6;white-space:pre-wrap;word-wrap:break-word}
            .msg+.msg,.msg+.lbl{margin-top:10px}
            .lbl+.msg{margin-top:0}
            .cp{position:absolute;right:9px;bottom:9px;background:transparent;border:1px solid var(--gold-dim);color:var(--gold);border-radius:8px;font:500 12px 'Golos Text',sans-serif;padding:6px 11px;cursor:pointer}
            .cp:focus-visible{outline:2px solid var(--gold);outline-offset:2px}
            .cp.done{background:var(--green);border-color:var(--green);color:#0c1013}
            .tip{font-size:12.5px;color:var(--dim);background:var(--panel2);border:1px solid var(--line);border-left:3px solid var(--gold-dim);border-radius:0 9px 9px 0;padding:9px 12px;margin:10px 0 0}
            .tip b{color:var(--text)}
            .rl{background:var(--panel2);border:1px solid var(--line);border-left:3px solid var(--red);border-radius:0 10px 10px 0;padding:12px 14px;margin-bottom:9px;font-size:13px}
            .rl b{color:var(--red)}
            ul.proc{margin:6px 0 0 18px;font-size:13.5px}
            ul.proc li{margin-bottom:7px}
            ul.proc b{color:var(--gold)}
            """;

    private static final String SCRIPT = """
            document.querySelectorAll('.cp').forEach(btn=>{
              btn.addEventListener('click',()=>{
                const box=btn.parentElement.cloneNode(true);
                box.querySelectorAll('.cp').forEach(b=>b.remove());
                const text=box.textContent.trim();
                const done=()=>{const old=btn.textContent;btn.textContent='%s';btn.classList.add('done');setTimeout(()=>{btn.textContent=old;btn.classList.remove('done');},1600);};
                if(navigator.clipboard&&navigator.clipboard.writeText){navigator.clipboard.writeText(text).then(done).catch(()=>fallback(text,done));}
                else fallback(text,done);
              });
            });
            function fallback(text,done){
              const ta=document.createElement('textarea');ta.value=text;ta.style.position='fixed';ta.style.opacity='0';
              document.body.appendChild(ta);ta.select();try{document.execCommand('copy');done();}catch(e){}document.body.removeChild(ta);
            }
            """.formatted(COPY_DONE);

    public String render(Page page) {
        StringBuilder out = new StringBuilder(64 * 1024);
        out.append("<!DOCTYPE html>\n");
        out.append("<html lang=\"ru\">\n");
        appendHead(out, page.browserTitle());
        out.append("<body>\n");
        out.append("<div class=\"wrap\">\n\n");

        out.append("<div class=\"eyebrow\">").append(escape(page.eyebrow())).append("</div>\n");
        out.append("<h1>").append(escape(page.heading())).append("</h1>\n");
        out.append("<p class=\"sub\">").append(escape(page.subtitle())).append("</p>\n\n");
        out.append("<div class=\"frame\">").append(page.frameHtml()).append("</div>\n\n");

        appendNav(out, page.nav());

        for (Section section : page.sections()) {
            appendSection(out, section);
        }

        out.append("\n</div>\n");
        out.append("<script>\n").append(SCRIPT).append("</script>\n");
        out.append("</body>\n</html>\n");
        return out.toString();
    }

    private void appendHead(StringBuilder out, String title) {
        out.append("<head>\n");
        out.append("<meta charset=\"UTF-8\">\n");
        out.append("<meta name=\"viewport\" content=\"width=device-width, initial-scale=1.0\">\n");
        out.append("<title>").append(escape(title)).append("</title>\n");
        out.append("<link rel=\"preconnect\" href=\"https://fonts.googleapis.com\">\n");
        out.append("<link rel=\"preconnect\" href=\"https://fonts.gstatic.com\" crossorigin>\n");
        out.append("<link href=\"https://fonts.googleapis.com/css2?family=Unbounded:wght@500;700&family=Golos+Text:wght@400;500;600&family=JetBrains+Mono:wght@400;500&display=swap\" rel=\"stylesheet\">\n");
        out.append("<style>\n").append(STYLE).append("</style>\n");
        out.append("</head>\n");
    }

    private void appendNav(StringBuilder out, java.util.List<NavLink> nav) {
        out.append("<nav class=\"nav\" aria-label=\"Этапы\">\n");
        for (NavLink link : nav) {
            out.append("<a href=\"").append(escape(link.href())).append("\">")
                    .append(escape(link.label())).append("</a>");
        }
        out.append("\n</nav>\n");
    }

    private void appendSection(StringBuilder out, Section section) {
        out.append("\n<section id=\"").append(escape(section.id())).append("\">\n");
        out.append("<h2>").append(escape(section.title())).append("</h2>\n");
        for (Block block : section.blocks()) {
            appendBlock(out, block);
        }
        out.append("</section>\n");
    }

    private void appendBlock(StringBuilder out, Block block) {
        switch (block) {
            case ChipsBlock cb -> appendChips(out, cb);
            case WhoBlock wb -> out.append("<p class=\"who\">").append(wb.html()).append("</p>\n");
            case SublblBlock sb -> out.append("<div class=\"sublbl\">").append(escape(sb.text())).append("</div>\n");
            case CardBlock cb -> appendCard(out, cb);
            case RiskBlock rb -> out.append("<div class=\"rl\"><b>").append(escape(rb.tag())).append("</b> ")
                    .append(rb.html()).append("</div>\n");
            case ProcessListBlock plb -> appendProcessList(out, plb);
        }
    }

    private void appendChips(StringBuilder out, ChipsBlock cb) {
        out.append("<div class=\"chips\">\n");
        for (Chip chip : cb.chips()) {
            out.append("<div class=\"chip");
            if (chip.critical()) {
                out.append(" crit");
            }
            out.append("\"><b>").append(escape(chip.label())).append(":</b> ")
                    .append(escape(chip.value())).append("</div>\n");
        }
        out.append("</div>\n");
    }

    private void appendCard(StringBuilder out, CardBlock cb) {
        out.append("<div class=\"card\">\n");
        for (CardElement element : cb.elements()) {
            switch (element) {
                case ClientLine cl -> out.append("<div class=\"client\">").append(cl.html()).append("</div>\n");
                case Label l -> out.append("<div class=\"lbl\">").append(escape(l.text())).append("</div>\n");
                case Message m -> appendMessage(out, m);
                case Tip t -> out.append("<div class=\"tip\">").append(t.html()).append("</div>\n");
            }
        }
        out.append("</div>\n");
    }

    private void appendMessage(StringBuilder out, Message m) {
        out.append("<div class=\"msg\">")
                .append(escape(m.plainText()))
                .append("<button class=\"cp\" type=\"button\">")
                .append(COPY_BUTTON)
                .append("</button></div>\n");
    }

    private void appendProcessList(StringBuilder out, ProcessListBlock plb) {
        out.append("<div class=\"card\">\n<ul class=\"proc\">\n");
        for (String itemHtml : plb.itemsHtml()) {
            out.append("<li>").append(itemHtml).append("</li>\n");
        }
        out.append("</ul>\n</div>\n");
    }

    private static String escape(String s) {
        if (s == null || s.isEmpty()) {
            return "";
        }
        StringBuilder sb = new StringBuilder(s.length() + 16);
        for (int i = 0; i < s.length(); i++) {
            char c = s.charAt(i);
            switch (c) {
                case '&' -> sb.append("&amp;");
                case '<' -> sb.append("&lt;");
                case '>' -> sb.append("&gt;");
                case '"' -> sb.append("&quot;");
                case '\'' -> sb.append("&#39;");
                default -> sb.append(c);
            }
        }
        return sb.toString();
    }
}
