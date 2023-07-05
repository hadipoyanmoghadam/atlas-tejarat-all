package net.sf.hibernate.id;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: Nov 12, 2007
 * Time: 3:19:59 PM
 * To change this template use File | Settings | File Templates.
 */

import java.io.Serializable;
import java.sql.SQLException;
import java.util.Properties;

import net.sf.hibernate.HibernateException;
import net.sf.hibernate.dialect.Dialect;
import net.sf.hibernate.engine.SessionImplementor;
import net.sf.hibernate.type.Type;
import net.sf.hibernate.util.PropertiesHelper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;


public class AtlasTableHiLoGenerator extends TableGenerator {

    public static final String MAX_LO = "max_lo";
    private long hi;
    private int lo;
    private int maxLo;
    private Class returnClass;
    private static final Log log = LogFactory.getLog(AtlasTableHiLoGenerator.class);

    public AtlasTableHiLoGenerator() {
    }

    public void configure(Type type, Properties params, Dialect d) {
        super.configure(type, params, d);
        maxLo = PropertiesHelper.getInt("max_lo", params, 32767);
        lo = maxLo + 1;
        returnClass = type.getReturnedClass();
        System.out.println("MAX_LO = " + MAX_LO);
        System.out.println("lo = " + lo);
    }

    public synchronized Serializable generate(SessionImplementor session, Object obj)
            throws SQLException, HibernateException {
        if (lo > maxLo) {
            int hival = ((Integer) super.generate(session, obj)).intValue();
            lo = 1;
            hi = hival * (maxLo + 1);
            log.debug("new hi value: " + hival);
        }
        return IdentifierGeneratorFactory.createNumber(hi + (long) (lo++), returnClass);
    }

    /*
        static Class _mthclass$(String x0)
        {
            return Class.forName(x0);
            ClassNotFoundException x1;
            x1;
            throw new NoClassDefFoundError(x1.getMessage());
        }

    */

}
