package com.bahaskript.model;

import java.util.List;

public final class Content {

    private Content() {
    }

    public record Page(
            String eyebrow,
            String browserTitle,
            String heading,
            String subtitle,
            String frameHtml,
            List<NavLink> nav,
            List<Section> sections
    ) {
    }

    public record NavLink(String href, String label) {
    }

    public record Section(String id, String title, List<Block> blocks) {
    }

    public record Chip(String label, String value, boolean critical) {
    }

    public sealed interface Block permits
            ChipsBlock,
            WhoBlock,
            SublblBlock,
            CardBlock,
            Tip,
            RiskBlock,
            ProcessListBlock {
    }

    public record ChipsBlock(List<Chip> chips) implements Block {
    }

    public record WhoBlock(String html) implements Block {
    }

    public record SublblBlock(String text) implements Block {
    }

    public record CardBlock(List<CardElement> elements) implements Block {
    }

    public sealed interface CardElement permits
            ClientLine,
            Label,
            Message,
            Tip {
    }

    public record ClientLine(String html) implements CardElement {
    }

    public record Label(String text) implements CardElement {
    }

    public record Message(String plainText) implements CardElement {
    }

    public record Tip(String html) implements CardElement, Block {
    }

    public record RiskBlock(String tag, String html) implements Block {
    }

    public record ProcessListBlock(List<String> itemsHtml) implements Block {
    }
}
