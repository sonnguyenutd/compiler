package boa.types.proto;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import boa.types.BoaInt;
import boa.types.BoaProtoTuple;
import boa.types.BoaString;
import boa.types.BoaTime;
import boa.types.BoaType;

/**
 * A {@link BoaProtoTuple}.
 * 
 * @author rdyer
 * 
 */
public class BugProtoTuple extends BoaProtoTuple {
	private final static List<BoaType> members = new ArrayList<BoaType>();
	private final static Map<String, Integer> names = new HashMap<String, Integer>();

	static {
		names.put("id", 0);
		members.add(new BoaInt());

		names.put("reporter", 1);
		members.add(new PersonProtoTuple());

		names.put("reported_date", 2);
		members.add(new BoaTime());

		names.put("closed_date", 3);
		members.add(new BoaTime());

		names.put("summary", 4);
		members.add(new BoaString());

		names.put("description", 5);
		members.add(new BoaString());

		names.put("status", 6);
		members.add(new BoaInt());

		names.put("severity", 7);
		members.add(new BoaString());
	}

	/**
	 * Construct a ProjectProtoTuple.
	 */
	public BugProtoTuple() {
		super(members, names);
	}

	@Override
	public String toJavaType() {
		return "boa.types.Bugs.Bug";
	}
}