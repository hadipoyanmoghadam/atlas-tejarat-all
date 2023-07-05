package branch.dpi.atlas.service.cm.handler.pg.saf;

/**
 * Created by IntelliJ IDEA.
 * User: Administrator
 * Date: Nov 30, 2007
 * Time: 7:59:36 PM
 * To change this template use File | Settings | File Templates.
 */
public interface SAFSourceMBean {
    void setPickNum(int pickNum);

    int getPickNum();

    void setDelay(long delay);

    long getDelay();

}
