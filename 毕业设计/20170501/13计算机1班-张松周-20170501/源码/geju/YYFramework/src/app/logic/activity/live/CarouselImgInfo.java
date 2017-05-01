package app.logic.activity.live;

/**
 * Created by Administrator on 2017/3/30 0030.
 */

public class CarouselImgInfo {

    private String carousel_id ;
    private String update_time ;
    private String descr ;
    private String create_time ;
    private String image ;
    private String address ; //跳转链接

    public CarouselImgInfo(){

    }

    public String getCarousel_id() {
        return carousel_id;
    }

    public void setCarousel_id(String carousel_id) {
        this.carousel_id = carousel_id;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getUpdate_time() {
        return update_time;
    }

    public void setUpdate_time(String update_time) {
        this.update_time = update_time;
    }

    public String getDescription() {
        return descr;
    }

    public void setDescription(String description) {
        this.descr = description;
    }

    public String getCreate_time() {
        return create_time;
    }

    public void setCreate_time(String create_time) {
        this.create_time = create_time;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
