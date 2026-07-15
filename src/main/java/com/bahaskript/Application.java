package com.bahaskript;

import com.bahaskript.data.ScriptData;
import com.bahaskript.render.HtmlRenderer;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public final class Application {

    private Application() {
    }

    public static void main(String[] args) throws IOException {
        Mode mode = Mode.parse(args);
        String html = new HtmlRenderer().render(ScriptData.page());

        switch (mode) {
            case Render render -> writeToFile(render.output(), html);
            case Serve serve -> serve(serve.port(), html);
        }
    }

    private static void writeToFile(Path path, String html) throws IOException {
        if (path.getParent() != null) {
            Files.createDirectories(path.getParent());
        }
        Files.writeString(path, html, StandardCharsets.UTF_8);
        System.out.println("Wrote " + html.length() + " chars to " + path.toAbsolutePath());
    }

    private static void serve(int port, String html) throws IOException {
        byte[] body = html.getBytes(StandardCharsets.UTF_8);
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
        server.createContext("/", new PageHandler(body));
        server.setExecutor(null);
        server.start();
        System.out.println("bahaskript is up: http://localhost:" + port + "/");
        System.out.println("Ctrl+C to stop.");
    }

    private static final class PageHandler implements HttpHandler {
        private final byte[] body;

        private PageHandler(byte[] body) {
            this.body = body;
        }

        @Override
        public void handle(HttpExchange exchange) throws IOException {
            String path = exchange.getRequestURI().getPath();
            try {
                if (!"/".equals(path) && !"/index.html".equals(path)) {
                    byte[] nf = "not found".getBytes(StandardCharsets.UTF_8);
                    exchange.getResponseHeaders().set("Content-Type", "text/plain; charset=utf-8");
                    exchange.sendResponseHeaders(404, nf.length);
                    try (OutputStream os = exchange.getResponseBody()) {
                        os.write(nf);
                    }
                    return;
                }

                exchange.getResponseHeaders().set("Content-Type", "text/html; charset=utf-8");
                exchange.getResponseHeaders().set("Cache-Control", "no-store");
                exchange.sendResponseHeaders(200, body.length);
                try (OutputStream os = exchange.getResponseBody()) {
                    os.write(body);
                }
            } finally {
                exchange.close();
            }
        }
    }

    private sealed interface Mode permits Render, Serve {
        static Mode parse(String[] args) {
            int port = 8080;
            Path output = null;
            for (int i = 0; i < args.length; i++) {
                String a = args[i];
                switch (a) {
                    case "--port", "-p" -> {
                        if (i + 1 >= args.length) {
                            throw new IllegalArgumentException("--port needs a value");
                        }
                        port = Integer.parseInt(args[++i]);
                    }
                    case "--out", "-o" -> {
                        if (i + 1 >= args.length) {
                            throw new IllegalArgumentException("--out needs a value");
                        }
                        output = Paths.get(args[++i]);
                    }
                    case "--help", "-h" -> {
                        System.out.println("""
                                bahaskript

                                Usage:
                                  bahaskript                    serve on http://localhost:8080/
                                  bahaskript --port 9000        serve on the given port
                                  bahaskript --out page.html    render once to a file and exit
                                """);
                        System.exit(0);
                    }
                    default -> throw new IllegalArgumentException("unknown argument: " + a);
                }
            }
            return output == null ? new Serve(port) : new Render(output);
        }
    }

    private record Render(Path output) implements Mode {
    }

    private record Serve(int port) implements Mode {
    }
}
