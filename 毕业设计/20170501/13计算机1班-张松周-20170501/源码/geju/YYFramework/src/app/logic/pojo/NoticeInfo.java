package app.logic.pojo;

import java.io.Serializable;

import android.R.integer;

/*
 * GZYY    2016-8-8  下午2:19:48
 */

public class NoticeInfo implements Serializable{

	private String msg_content;
	private String msg_create_time;
	private String msg_abstract;
	private int msg_type;

	private String msg_id;
	private String msg_title;
	private String msg_creator_id;
	private int msg_unread;
	private String msg_cover;

	private String msg_creator;


	private String msg_notice_img ;
	private String picture_url;
	private String friend_name ;

	public String getFriend_name() {
		return friend_name;
	}

	public void setFriend_name(String friend_name) {
		this.friend_name = friend_name;
	}

	public String getMsg_notice_img() {
		return msg_notice_img;
	}


	public void setMsg_notice_img(String msg_notice_img) {
		this.msg_notice_img = msg_notice_img;
	}

	public String getPicture_url() {
		return picture_url;
	}

	public void setPicture_url(String picture_url) {
		this.picture_url = picture_url;
	}

	public String getMsg_creator() {
		return msg_creator;
	}

	public void setMsg_creator(String msg_creator) {
		this.msg_creator = msg_creator;
	}

	public int getMsg_type() {
		return msg_type;
	}

	public void setMsg_type(int msg_type) {
		this.msg_type = msg_type;
	}

	public int getMsg_unread() {
		return msg_unread;
	}

	public void setMsg_unread(int msg_unread) {
		this.msg_unread = msg_unread;
	}
	public String getMsg_content() {
		return msg_content;
	}

	public void setMsg_content(String msg_content) {
		this.msg_content = msg_content;
	}

	public String getMsg_create_time() {
		return msg_create_time;
	}

	public void setMsg_create_time(String msg_create_time) {
		this.msg_create_time = msg_create_time;
	}

	public String getMsg_abstract() {
		return msg_abstract;
	}

	public void setMsg_abstract(String msg_abstract) {
		this.msg_abstract = msg_abstract;
	}

	public String getMsg_id() {
		return msg_id;
	}

	public void setMsg_id(String msg_id) {
		this.msg_id = msg_id;
	}

	public String getMsg_title() {
		return msg_title;
	}

	public void setMsg_title(String msg_title) {
		this.msg_title = msg_title;
	}

	public String getMsg_creator_id() {
		return msg_creator_id;
	}

	public void setMsg_creator_id(String msg_creator_id) {
		this.msg_creator_id = msg_creator_id;
	}

	public String getMsg_cover() {
		return msg_cover;
	}

	public void setMsg_cover(String msg_cover) {
		this.msg_cover = msg_cover;
	}

}
