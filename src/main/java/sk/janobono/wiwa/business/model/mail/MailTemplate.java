package sk.janobono.wiwa.business.model.mail;

public enum MailTemplate {

    BASE("MailBaseTemplate", true);

    private final String template;
    private final boolean html;

    MailTemplate(final String template, final boolean html) {
        this.template = template;
        this.html = html;
    }

    public String getTemplate() {
        return template;
    }

    public boolean getHtml() {
        return html;
    }
}
