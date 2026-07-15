package com.bahaskript.data;

import com.bahaskript.model.Content.Block;
import com.bahaskript.model.Content.CardBlock;
import com.bahaskript.model.Content.CardElement;
import com.bahaskript.model.Content.Chip;
import com.bahaskript.model.Content.ChipsBlock;
import com.bahaskript.model.Content.ClientLine;
import com.bahaskript.model.Content.Label;
import com.bahaskript.model.Content.Message;
import com.bahaskript.model.Content.NavLink;
import com.bahaskript.model.Content.ProcessListBlock;
import com.bahaskript.model.Content.RiskBlock;
import com.bahaskript.model.Content.Section;
import com.bahaskript.model.Content.SublblBlock;
import com.bahaskript.model.Content.Tip;
import com.bahaskript.model.Content.WhoBlock;

import java.util.List;

final class Dsl {

    private Dsl() {
    }

    static Section section(String id, String title, Block... blocks) {
        return new Section(id, title, List.of(blocks));
    }

    static ChipsBlock chips(Chip... items) {
        return new ChipsBlock(List.of(items));
    }

    static Chip chip(String label, String value) {
        return new Chip(label, value, false);
    }

    static Chip critChip(String label, String value) {
        return new Chip(label, value, true);
    }

    static WhoBlock who(String html) {
        return new WhoBlock(html);
    }

    static SublblBlock sublbl(String text) {
        return new SublblBlock(text);
    }

    static CardBlock card(CardElement... elements) {
        return new CardBlock(List.of(elements));
    }

    static ClientLine client(String html) {
        return new ClientLine(html);
    }

    static Label label(String text) {
        return new Label(text);
    }

    static Message msg(String plain) {
        return new Message(plain.strip());
    }

    static Tip tip(String html) {
        return new Tip(html);
    }

    static RiskBlock risk(String tag, String html) {
        return new RiskBlock(tag, html);
    }

    static ProcessListBlock procList(String... itemsHtml) {
        return new ProcessListBlock(List.of(itemsHtml));
    }

    static NavLink nav(String href, String label) {
        return new NavLink(href, label);
    }
}
