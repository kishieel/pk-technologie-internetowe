package pk.wieik.lab2;

import jakarta.servlet.ServletContext;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@WebServlet(name = "helloServlet", value = "/hello-servlet")
public class HelloServlet extends HttpServlet {
    protected final Map<String, User> users = new HashMap<>() {{
        put("user", new User("user", "password", "user"));
        put("admin", new User("admin", "password", "admin"));
    }};

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String page = request.getParameter("page");

        if (page == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Page not found");
            return;
        }

        if (page.equals("home") && !loggedIn(request)) {
            page = "index";
        }

        try {
            String template = load("/WEB-INF/template.html", request);
            String rendered = render(template, page, request);
            response.setContentType("text/html");
            response.getWriter().write(rendered);
        } catch (IOException e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Internal server error");
        }
    }

    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        String page = request.getParameter("page");

        if (page == null) {
            response.sendError(HttpServletResponse.SC_NOT_FOUND, "Page not found");
            return;
        }

        HttpSession session = request.getSession();
        String formType = request.getParameter("form-type");
        if (formType != null) {
            if (formType.equals("LOGIN")) {
                String username = request.getParameter("login");
                String password = request.getParameter("password");

                if (username == null || password == null) {
                    session.setAttribute("loginFailed", true);
                }

                User user = users.get(username);
                if (user == null || !user.password.equals(password)) {
                    session.setAttribute("loginFailed", true);
                } else {
                    session.setAttribute("loginFailed", false);
                    session.setAttribute("loggedIn", true);
                    session.setAttribute("isAdmin", user.getRole().equals("admin"));
                    session.setAttribute("username", user.getUsername());
                    page = "home";
                }
            } else if (formType.equals("LOGOUT")) {
                session.invalidate();
            } else if (formType.equals("THEME")) {
                String theme = request.getParameter("theme");

                if (theme == null || !Arrays.asList("light", "dark").contains(theme)) {
                    theme = "light";
                }

                ServletContext application = getServletConfig().getServletContext();
                application.setAttribute("theme", theme);
            }
        }

        if (page.equals("home") && !loggedIn(request)) {
            page = "index";
        }

        try {
            String template = load("/WEB-INF/template.html", request);
            String rendered = render(template, page, request);
            response.setContentType("text/html");
            response.getWriter().write(rendered);
        } catch (IOException e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Internal server error");
        }
    }

    public void destroy() {
    }

    protected String load(String path, HttpServletRequest request) throws IOException {
        InputStream in = request.getServletContext().getResourceAsStream(path);

        if (in == null) {
            return null;
        }

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(in))) {
            StringBuilder sb = new StringBuilder();
            String line;

            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }

            return sb.toString();
        }
    }

    protected String render(String template, String page, HttpServletRequest request) {
        Pattern pattern = Pattern.compile("\\[\\[(.*?)\\]\\]");
        Matcher matcher = pattern.matcher(template);

        StringBuilder sb = new StringBuilder();
        while (matcher.find()) {
            String match = matcher.group(1);
            String replacement = "";

            try {
                if (match.equals("CONTENT")) {
                    replacement = load("/WEB-INF/" + page + ".html", request);
                } else if (match.equals("NAVIGATION")) {
                    replacement = load("/WEB-INF/navigation.html", request);
                } else if (match.equals("LOGIN") && loggedIn(request)) {
                    replacement = load("/WEB-INF/logout.html", request);
                } else if (match.equals("LOGIN") && !loggedIn(request)) {
                    replacement = load("/WEB-INF/login.html", request);
                } else if (match.equals("USERNAME") && loggedIn(request)) {
                    replacement = username(request);
                } else if (match.equals("THEME_FORM") && isAdmin(request)) {
                    replacement = load("/WEB-INF/theme-form.html", request);
                } else if (match.equals("LOGIN_FAILED") && loginFailed(request)) {
                    replacement = load("/WEB-INF/login-failed.html", request);
                } else if (match.equals("USER_HOME") && loggedIn(request)) {
                    replacement = load("/WEB-INF/user-home.html", request);
                } else if (match.equals("LIGHT_CHECKED")) {
                    replacement = theme().equals("light") ? "checked" : "";
                } else if (match.equals("DARK_CHECKED")) {
                    replacement = theme().equals("dark") ? "checked" : "";
                } else if (match.equals("THEME")) {
                    replacement = theme();
                }

                replacement = render(replacement, page, request);
            } catch (IOException e) {
                e.printStackTrace();
            }

            matcher.appendReplacement(sb, replacement);
        }
        matcher.appendTail(sb);

        return sb.toString();
    }

    protected boolean loggedIn(HttpServletRequest request) {
        HttpSession session = request.getSession();
        Object value = session.getAttribute("loggedIn");

        return value != null && (boolean) value;
    }

    protected boolean isAdmin(HttpServletRequest request) {
        HttpSession session = request.getSession();
        Object value = session.getAttribute("isAdmin");

        return value != null && (boolean) value;
    }

    protected boolean loginFailed(HttpServletRequest request) {
        HttpSession session = request.getSession();
        Object value = session.getAttribute("loginFailed");

        return value != null && (boolean) value;
    }

    protected String username(HttpServletRequest request) {
        HttpSession session = request.getSession();
        Object value = session.getAttribute("username");

        return value != null ? (String) value : "";
    }

    protected String theme() {
        ServletContext application = getServletConfig().getServletContext();
        Object value = application.getAttribute("theme");

        return value != null ? (String) value : "light";
    }
}