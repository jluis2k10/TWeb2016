package es.jperez2532.components;

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.tagext.SimpleTagSupport;
import java.io.IOException;
import java.io.Writer;
import java.math.BigDecimal;

/**
 * Genera las estrellas con la puntuación de la película para mostrarla en la vista
 * del catálogo.
 */
public class ScoreBadge extends SimpleTagSupport {

    private BigDecimal score;

    /**
     * Genera el html con la puntuación (estrellas) de las películas que se muestran en
     * la vista del catálogo.
     * @throws JspException en caso de producirse algún error escribiendo en el buffer de salida
     */
    @Override
    public void doTag() throws JspException {
        int roundScore = score.setScale(0, BigDecimal.ROUND_HALF_UP).intValueExact();
        Writer out = getJspContext().getOut();
        try {
            out.write("<span class=\"badge\">");
            for (int i = 0; i < 5; i++) {
                if (roundScore > i)
                    out.write("<i class=\"material-icons tiny amber-text\">star</i>");
                else
                    out.write("<i class=\"material-icons tiny\">star</i>");
            }
            out.write("</span>");
        } catch (IOException e) {
            throw new JspException("Error en la librería de generar badge con puntuación: " + e);
        }
    }

    /**
     * Devuelve la puntuación de la película.
     * @return la puntuación de la película
     */
    public BigDecimal getScore() {
        return score;
    }

    /**
     * Establece la puntuación de la película.
     * @param score la puntuación de la película
     */
    public void setScore(BigDecimal score) {
        this.score = score;
    }
}
