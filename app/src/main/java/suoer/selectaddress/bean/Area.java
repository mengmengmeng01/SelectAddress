package suoer.selectaddress.bean;


public class Area {
	private String id;//ID
	private String name;//名称
	private int level;//级别 从1开始
	private String pid;//父节点ID
	private int plevel;//父节点级别

	public String getId() {
		return id;
	}

	public int getLevel() {
		return level;
	}

	public String getPid() {
		return pid;
	}

	public String getName() {
		return name;
	}

	public int getPlevel() {
		return plevel;
	}

	public void setId(String id) {
		this.id = id;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setPid(String pid) {
		this.pid = pid;
	}

	public void setPlevel(int plevel) {
		this.plevel = plevel;
	}

	public Area(String id, String name, int level, String pid, int plevel) {
		this.id = id;
		this.name = name;
		this.level = level;
		this.pid = pid;
		this.plevel = plevel;
	}

	@Override
	public String toString() {
		return "Area2{" +
				"id='" + id + '\'' +
				", name='" + name + '\'' +
				", level='" + level + '\'' +
				", plevel='" + plevel + '\'' +
				", pid='" + pid + '\'' +
				'}';
	}
}
