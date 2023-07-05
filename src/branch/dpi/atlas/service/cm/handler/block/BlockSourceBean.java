package branch.dpi.atlas.service.cm.handler.block;

/**
 * User: F.Heydari
 * Date: Jun 9, 2020
 * Time: 10:17 AM
 */
public interface BlockSourceBean {
    void setPickNum(int pickNum);

    int getPickNum();

    void setDelay(long delay);

    long getDelay();

}