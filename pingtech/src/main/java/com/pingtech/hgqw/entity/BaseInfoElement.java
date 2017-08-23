package com.pingtech.hgqw.entity;

/** 用于保存数据采集界面上基础数据列表的信息项 */
public class BaseInfoElement {
	/** id */
	private String id;
	
	/**
	 * 服务器返回的id
	 */
	private String ids;
	
	/** 类别 */
	private String type;
	/** 显示的标题 */
	private String outlineTitle;
	/** 是否有父节点 */
	private boolean mhasParent;
	/** 是否有子节点 */
	private boolean mhasChild;
	/** 父节点信息 */
	private String parent;
	/** 所处级别 */
	private int level;
	/** 是否曾经采集过 */
	private boolean sended;
	/** 用户保存采集过的gps数据 */
	private String gpsData;
	/** 是否选中 */
	private boolean checked = false;
	/** 是否展开 */
	private boolean expanded;

	
	public String getIds() {
		return ids;
	}

	public void setIds(String ids) {
		this.ids = ids;
	}

	public boolean getChecked() {
		return checked;
	}

	public void setChecked(boolean check) {
		checked = check;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getOutlineTitle() {
		return outlineTitle;
	}

	public void setOutlineTitle(String outlineTitle) {
		this.outlineTitle = outlineTitle;
	}

	public boolean isMhasParent() {
		return mhasParent;
	}

	public void setMhasParent(boolean mhasParent) {
		this.mhasParent = mhasParent;
	}

	public boolean isMhasChild() {
		return mhasChild;
	}

	public void setMhasChild(boolean mhasChild) {
		this.mhasChild = mhasChild;
	}

	public String getParent() {
		return parent;
	}

	public void setParent(String parent) {
		this.parent = parent;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public boolean isSended() {
		return sended;
	}

	public void setSended(boolean sended) {
		this.sended = sended;
	}

	public void setGpsData(String data) {
		this.gpsData = data;
	}

	public String getGpsData() {
		return this.gpsData;
	}

	public boolean isExpanded() {
		return expanded;
	}

	public void setExpanded(boolean expanded) {
		this.expanded = expanded;
	}

	public BaseInfoElement(String id, String type, String outlineTitle, boolean mhasParent, boolean mhasChild,
			String parent, int level, boolean expanded , String ids) {
		super();
		this.id = id;
		if (type == null || type.length() == 0) {
			this.type = parent;
		} else {
			this.type = type;
		}
		this.outlineTitle = outlineTitle;
		this.mhasParent = mhasParent;
		this.mhasChild = mhasChild;
		this.parent = parent;
		this.level = level;
		this.expanded = expanded;
		this.sended = false;
		this.gpsData = "";
		this.ids = ids;
	}
}
