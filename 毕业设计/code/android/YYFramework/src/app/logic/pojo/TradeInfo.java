package app.logic.pojo;

import java.util.List;

/**
 * Created by apple on 17/4/26.
 */

public class TradeInfo {

    private int id;
    private String update_time;
    private String name;
    private String create_time;
    private String is_del;
    private String parent_id;
    private List<TradeInfo> child_node;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUpdate_time() {
        return update_time;
    }

    public void setUpdate_time(String update_time) {
        this.update_time = update_time;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCreate_time() {
        return create_time;
    }

    public void setCreate_time(String create_time) {
        this.create_time = create_time;
    }

    public String getIs_del() {
        return is_del;
    }

    public void setIs_del(String is_del) {
        this.is_del = is_del;
    }

    public String getParent_id() {
        return parent_id;
    }

    public void setParent_id(String parent_id) {
        this.parent_id = parent_id;
    }

    public List<TradeInfo> getChild_node() {
        return child_node;
    }

    public void setChild_node(List<TradeInfo> child_node) {
        this.child_node = child_node;
    }
}
