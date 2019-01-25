package tools;

public abstract class ReflectionsEquality {
	
	@Override
	public boolean equals(Object object) {
		return Tools.equals(this, object);
	}
	
}
