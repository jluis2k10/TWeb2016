package es.jperez2532.jsptags;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Sort;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.SimpleTagSupport;
import java.io.IOException;
import java.io.Writer;

/**
 * Genera los enlaces para la paginación
 */
public class PaginationLinks extends SimpleTagSupport {

    private String url;
    private int limit = 2;       // Número de enlaces a mostrar a cada lado de la página actual
    private String sort = "";    // Orden de los elementos en la página
    private String prev = "Ant";
    private String next = "Sig";
    private String liCSS = "";   // Estilo CSS de los elementos de la lista
    private Page page;

    /**
     * Crea los enlaces para la paginación de resultados.
     *
     * Es una paginación "clásica" con enlaces a la página Anterior y a la página Siguiente,
     * enlaces a la primera y a la última página, y un máximo de @limit enlaces tanto a la
     * derecha como a la izquierda de la página actual. Por ejemplo, si estamos en la página
     * 8, tenemos un @limit de 2 y hay un total de 20 páginas se mostraría lo siguiente:
     *
     *  < 1 ... 6 7 8 9 10 ... 20 >
     *
     * Si por el contrario estamos entre las 5 primeras páginas se mostraría:
     *
     *  < 1 2 3 4 5 ... 20 >
     *
     * Y similarmente para las 5 últimas páginas:
     *
     *  < 1 ... 16 17 18 19 20 >
     *
     * @throws JspException en caso de producirse algún error escribiendo en el buffer de salida
     */
    @Override
    public void doTag() throws JspException {
        if (page.getTotalPages() == 1)
            return; // Todos los elementos se pueden mostrar en una sola página.
        Writer out = getJspContext().getOut();
        int currPage = page.getNumber();
        int maxPages = limit*2 + 1; // Máximo de enlaces a otras páginas (contando la página actual)

        // String con el cual generar la parte del enlace que indica cómo ordenar los elementos
        for(Sort.Order order: page.getSort()) {
            sort += order.getProperty() + "," + order.getDirection().toString();
        }

        // Construimos los enlaces de la paginación. 4 casos posibles:
        try {
            out.write("<ul class=\"pagination\">");

            // Enlace "ir a página anterior"
            if (page.hasPrevious())
                out.write(doLink(currPage - 1, getPrev(), getLiCSS(), false));

            /*
             *  CASO 1: El número total de páginas es menor o igual que el límite
             *  de enlaces que podemos mostrar.
             *  -> Mostramos los enlaces a todas las páginas
             */
            if (maxPages >= page.getTotalPages()) {
                // Páginas
                for (int i = 0; i < page.getTotalPages(); i++) {
                    if (i == currPage)
                        out.write(doLink(i, String.valueOf(i + 1), "active disabled", true));
                    else
                        out.write(doLink(i, String.valueOf(i + 1), getLiCSS(), false));
                }
            }
            /*
             * CASO 2: Existen más páginas de las que podemos mostrar en los enlaces.
             * Estamos dentro de las primeras @limit*2+1 páginas.
             * -> Mostrar los enlaces a las páginas 1..@limit*2+1 (más el de la última página)
             */
            else if (currPage - limit <= 0) {
                // Páginas
                for (int i = 0; i < maxPages; i++) {
                    if (i == currPage)
                        out.write(doLink(i, String.valueOf(i+1), "active disabled", true));
                    else
                        out.write(doLink(i, String.valueOf(i+1), getLiCSS(), false));
                }
                // Puntos y última página
                if (page.getTotalPages()-1 != maxPages)
                    out.write(doLink(0, "...", "disabled", true));
                out.write(doLink(page.getTotalPages()-1, String.valueOf(page.getTotalPages()), getLiCSS(), false));
            }
            /*
             * CASO 3: Existen más páginas de las que podemos mostrar en los enlaces.
             * Estamos dentro de las últimas @limit*2+1 páginas.
             * -> Mostrar los enlaces a las últimas @limit*2+1 páginas (más el de la primera página)
             */
            else if (page.getTotalPages() - currPage <= limit + 1) {
                // Primera página y puntos
                out.write(doLink(0, "1", getLiCSS(), false));
                if (page.getTotalPages()-1 != maxPages)
                    out.write(doLink(0, "...", "disabled", true));
                // Páginas
                for (int i = page.getTotalPages() - maxPages; i < page.getTotalPages(); i++) {
                    if (i == currPage)
                        out.write(doLink(i, String.valueOf(i+1), "active disabled", true));
                    else
                        out.write(doLink(i, String.valueOf(i+1), getLiCSS(), false));
                }
            }
            /*
             * CASO 4: Existen más páginas de las que podemos mostrar en los enlaces.
             * Estamos en una página intermedia, la cual tiene una distancia > @limit tanto de
             * la primera como de la última página.
             * -> Mostrar @limit enlaces anteriores a la página actual y @limit enlaces posteriores
             * a la página actual (más los enlaces a la primera y última páginas)
             */
            else {
                // Primera página y puntos (si necesario)
                out.write(doLink(0, "1", getLiCSS(), false));
                if (currPage-limit > 1)
                    out.write(doLink(0, "...", "disabled", true));

                // Páginas
                for (int i = currPage-limit; i <= currPage+limit; i++)
                    if (i == currPage)
                        out.write(doLink(i, String.valueOf(i+1), "active disabled", true));
                    else
                        out.write(doLink(i, String.valueOf(i+1), getLiCSS(), false));

                // Puntos (si necesario) y última página
                if (currPage+limit < page.getTotalPages()-limit)
                    out.write(doLink(0, "...", "disabled", true));
                out.write(doLink(page.getTotalPages()-1, String.valueOf(page.getTotalPages()), getLiCSS(), false));
            }

            // Enlace "ir a página siguiente"
            if (page.hasNext())
                out.write(doLink(currPage + 1, getNext(), getLiCSS(), false));

            out.write("</ul>");
        } catch (IOException e) {
            throw new JspException("Error en la librería de paginación: " + e);
        }
    }

    /**
     * Genera el hiperenlace de cada botón de la paginación.
     *
     * @param page     número de la página a enlazar
     * @param linkText texto que aparece en el botón
     * @param cssClass clase css del botón
     * @param disabled true si el enlace no debe ser clicable
     * @return String con el enlace formado
     */
    private String doLink(int page, String linkText, String cssClass, boolean disabled) {
        StringBuilder link = new StringBuilder("<li");

        if (cssClass != null) {
            link.append(" class=\"")
                    .append(cssClass)
                    .append("\"");
        }

        if (disabled) {
            link.append(">")
                    .append("<a>")
                    .append(linkText)
                    .append("</a>");
        } else {
            link.append(">")
                    .append("<a href=\"")
                    .append(this.getUrl())
                    .append("pagina=")
                    .append(page)
                    .append("&ver=")
                    .append(this.page.getSize())
                    .append("&sort=")
                    .append(sort)
                    .append("\">")
                    .append(linkText)
                    .append("</a>");
        }

        link.append("</li>");
        return link.toString();
    }

    /**
     * Devuelve la url "base" de la paginación.
     * @return la url "base" de la paginación
     */
    public String getUrl() {
        return url;
    }

    /**
     * Establece la url "base" de la paginación.
     * @param url la url "base" de la paginación
     */
    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * Devuelve la forma en la que están ordenados los elementos de la página.
     * @return orden de los elementos de la página
     */
    public String getSort() {
        return sort;
    }

    /**
     * Establece la forma en la que se ordenan los elementos en la página.
     * @param sort forma en la que se deben ordenar los elementos en la página
     */
    public void setSort(String sort) {
        this.sort = sort;
    }

    /**
     * Devuelve el texto para el enlace a la página anterior.
     * @return texto para el enlace a la página anterior
     */
    public String getPrev() {
        return prev;
    }

    /**
     * Establece el texto para el enlace a la página anterior.
     * @param prev texto para el enlace a la página anterior
     */
    public void setPrev(String prev) {
        this.prev = prev;
    }

    /**
     * Decuelve el texto para el enlace a la página siguiente.
     * @return texto para el enlace a la página siguiente
     */
    public String getNext() {
        return next;
    }

    /**
     * Establece el texto para el enlace a la página siguiente.
     * @param next texto para el enlace a la página siguiente
     */
    public void setNext(String next) {
        this.next = next;
    }

    /**
     * Devuelve el estilo css para los items (li) de la paginación.
     * @return estilo css para los items (li) de la paginación
     */
    public String getLiCSS() {
        return liCSS;
    }

    /**
     * Establece el estilo css para los items (li) de la paginación.
     * @param liCSS estilo css para los items (li) de la paginación
     */
    public void setLiCSS(String liCSS) {
        this.liCSS = liCSS;
    }

    /**
     * Devuelve la página ({@link Page}) actual.
     * @return la página ({@link Page}) actual
     */
    public Page getPage() {
        return page;
    }

    /**
     * Establece la página ({@link Page}) actual.
     * @param page la página ({@link Page}) actual
     */
    public void setPage(Page page) {
        this.page = page;
    }
}
