package com.deliverooo.domain;

public class Package {

	private String pkgName;
	private Integer weight;	
	private Integer distance;
	
	public Package(String name, int weight, int distance) {
		super();
		this.pkgName = name;
		this.weight = weight;
		this.distance = distance;
	}
	
	public String getPkgName() {
		return pkgName;
	}

	public void setPkgName(String pkgName) {
		this.pkgName = pkgName;
	}

	public int getWeight() {
		return weight;
	}

	public void setWeight(int weight) {
		this.weight = weight;
	}

	public Integer getDistance() {
		return distance;
	}

	public void setDistance(int distance) {
		this.distance = distance;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((pkgName == null) ? 0 : pkgName.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Package other = (Package) obj;
		if (pkgName == null) {
			if (other.pkgName != null)
				return false;
		} else if (!pkgName.equals(other.pkgName))
			return false;
		return true;
	}
	
	
	
}
