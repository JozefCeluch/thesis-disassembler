import java.lang.*;
import java.lang.Integer;
import java.util.List;
import java.util.Set;
import java.lang.Number;
import java.lang.Object;
import java.lang.Short;
import java.util.HashMap;
import java.util.HashSet;

class ComplexVariableNames {

	public List<HashMap<Integer, Set<String>>> complexListField;
	private static HashSet<String> staticSet;

	public void methods(){
		List<HashMap<Integer, Set<String>>> localComplexList = complexListField;
		Integer smallNum = new Integer(2);
		Number number = smallNum;
		java.lang.Comparable<?> comparable = smallNum;
	}

}