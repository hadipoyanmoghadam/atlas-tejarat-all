package dpi.atlas.model.tj.entity;

/**
 * Created by IntelliJ IDEA.
 * User: Shahram Boroon
 * Date: Sep 18, 2006
 * Time: 3:23:22 PM
 * To change this template use File | Settings | File Templates.
 */

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.apache.commons.lang.builder.ToStringBuilder;

import java.io.Serializable;


/**
 * @author Hibernate CodeGenerator
 */
public class Style implements Serializable {

    /**
     * identifier field
     */
    private int styleId;

    /**
     * identifier field
     */
    private String name;

    /**
     * identifier field
     */
    private String headerFilename;

    /**
     * identifier field
     */
    private String menuFilename;

    /**
     * identifier field
     */
    private String mainFilename;

    /**
     * identifier field
     */
    private String footerFilename;

    /**
     * full constructor
     */
    public Style(int styleId, String name, String headerFilename, String menuFilename, String mainFilename, String footerFilename) {
        this.styleId = styleId;
        this.name = name;
        this.headerFilename = headerFilename;
        this.menuFilename = menuFilename;
        this.mainFilename = mainFilename;
        this.footerFilename = footerFilename;
    }

    /**
     * default constructor
     */
    public Style() {
    }

    public int getStyleId() {
        return this.styleId;
    }

    public void setStyleId(int styleId) {
        this.styleId = styleId;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHeaderFilename() {
        return this.headerFilename;
    }

    public void setHeaderFilename(String headerFilename) {
        this.headerFilename = headerFilename;
    }

    public String getMenuFilename() {
        return this.menuFilename;
    }

    public void setMenuFilename(String menuFilename) {
        this.menuFilename = menuFilename;
    }

    public String getMainFilename() {
        return this.mainFilename;
    }

    public void setMainFilename(String mainFilename) {
        this.mainFilename = mainFilename;
    }

    public String getFooterFilename() {
        return this.footerFilename;
    }

    public void setFooterFilename(String footerFilename) {
        this.footerFilename = footerFilename;
    }

    public String toString() {
        return new ToStringBuilder(this)
                .append("styleId", getStyleId())
                .append("name", getName())
                .append("headerFilename", getHeaderFilename())
                .append("menuFilename", getMenuFilename())
                .append("mainFilename", getMainFilename())
                .append("footerFilename", getFooterFilename())
                .toString();
    }

    public boolean equals(java.lang.Object other) {
        if ((this == other)) return true;
        if (!(other instanceof Style)) return false;
        Style castOther = (Style) other;
        return new EqualsBuilder()
                .append(this.getStyleId(), castOther.getStyleId())
                .append(this.getName(), castOther.getName())
                .append(this.getHeaderFilename(), castOther.getHeaderFilename())
                .append(this.getMenuFilename(), castOther.getMenuFilename())
                .append(this.getMainFilename(), castOther.getMainFilename())
                .append(this.getFooterFilename(), castOther.getFooterFilename())
                .isEquals();
    }

    public int hashCode() {
        return new HashCodeBuilder()
                .append(getStyleId())
                .append(getName())
                .append(getHeaderFilename())
                .append(getMenuFilename())
                .append(getMainFilename())
                .append(getFooterFilename())
                .toHashCode();
    }
}
