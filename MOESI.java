import java.util.LinkedHashMap;
import java.util.Scanner;

public class MOESI {
	public static LinkedHashMap<Integer, LinkedHashMap<Integer, String>> cacheList = new LinkedHashMap<Integer, LinkedHashMap<Integer, String>>();
	public static LinkedHashMap<Integer, String> cache0 = new LinkedHashMap<Integer, String>();
	public static LinkedHashMap<Integer, String> cache1 = new LinkedHashMap<Integer, String>();
	public static LinkedHashMap<Integer, String> cache2 = new LinkedHashMap<Integer, String>();

	private static final String READ = "r";
	private static final String WRITE = "w";
	public static final String OWNED = "OWNED";
	public static final String MODIFIED = "MODIFIED";
	public static final String SHARED = "SHARED";
	public static final String EXCLUSIVE = "EXCLUSIVE";
	public static final String INVALID = "INVALID";
	public static final String HITDIRTY = "HITDIRTY";
	public static final String HIT = "HIT";
	public static final String MISS = "MISS";

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		initialize();
		Scanner in = new Scanner(System.in);
		readCommand(in);
		while (true) {
			readCommand(in);
		}

	}

	/**
	 * Initialize the cache's initials stage
	 */
	private static void initialize() {
		cacheList.put(0, cache0);
		cacheList.put(1, cache1);
		cacheList.put(2, cache2);
		for (int i = 0; i < 4; i++) {
			cache0.put(i, INVALID);
			cache1.put(i, INVALID);
			cache2.put(i, INVALID);
		}
	}

	/**
	 * @param input string
	 * @return NA
	 */
	private static String readCommand(Scanner in) {
		System.out
				.println("\nEnter the input (Cache#,Command,Line#) or Press any key to exit:");
		String input = in.next();
		try {
			Integer cacheNumber = (int) input.charAt(0) - 48;
			String command = Character.toString(input.charAt(1));
			Integer lineAddress = (int) input.charAt(2) - 48;
			if (cacheNumber < 0 || cacheNumber > 2 || lineAddress < 0
					|| lineAddress > 3) {
				System.out.println("ERROR: Invalid Input!");
				return null;
			}
			if (command.equals(READ)) {
				System.out.println("\nReading line " + lineAddress
						+ " from Cache " + cacheNumber);
				process(cacheNumber, command, lineAddress);
			} else if (command.equals(WRITE)) {
				System.out.println("\nWriting line " + lineAddress
						+ " to Cache " + cacheNumber);
				process(cacheNumber, command, lineAddress);
			} else {
				System.out.println("ERROR: Invalid Command!");
				return null;
			}

		} catch (Exception e) {
			System.exit(0);
		}

		return input;
	}

	/**
	 * Process the read and write commands
	 * @param cacheNumber
	 * @param command
	 * @param lineAddress
	 */
	private static void process(Integer cacheNumber, String command,
			Integer lineAddress) {
		if (cacheNumber.equals(0)) {
			probe(0, 1, 2, command, lineAddress);
		} else if (cacheNumber.equals(1)) {
			probe(1, 0, 2, command, lineAddress);

		} else if (cacheNumber.equals(2)) {
			probe(2, 0, 1, command, lineAddress);
		}
	}

	/**
	 * @param cache
	 * @param adjacent1
	 * @param adjacent2
	 * @param command
	 * @param lineAddress
	 */
	private static void probe(Integer cache, Integer adjacent1,
			Integer adjacent2, String command, Integer lineAddress) {
		if (command.equals(READ)) {
			procRead(cache, adjacent1, adjacent2, lineAddress);
		} else if (command.equals(WRITE)) {
			procWrite(cache, adjacent1, adjacent2, lineAddress);
		}

	}

	/**
	 * @param cacheCurrent
	 * @param cacheAdjacent1
	 * @param cacheAdjacent2
	 * @param lineAddress
	 */
	public static void procRead(Integer cacheCurrent, Integer cacheAdjacent1,
		Integer cacheAdjacent2, Integer lineAddress) {
		boolean probeReadStatus = false;
		String procStatus = "";
		
		// Get the caches
		LinkedHashMap<Integer, String> cache = cacheList.get(cacheCurrent);
		LinkedHashMap<Integer, String> adjacent1 = cacheList
				.get(cacheAdjacent1);
		LinkedHashMap<Integer, String> adjacent2 = cacheList
				.get(cacheAdjacent2);

		// Store previous states of cache
		String prev = cache.get(lineAddress);
		String prev_adjacent1 = adjacent1.get(lineAddress);
		String prev_adjacent2 = adjacent2.get(lineAddress);

		if (cache.get(lineAddress).equals(INVALID)) {
			procStatus = MISS;
			// ProbRead
			broadcastBusRead(cache, adjacent1, adjacent2,
					cache.get(lineAddress), lineAddress);
			probeReadStatus = true;

		} else if (cache.get(lineAddress).equals(EXCLUSIVE)) {
			procStatus = HIT;
		} else if (cache.get(lineAddress).equals(MODIFIED)) {
			procStatus = HITDIRTY;
		} else if (cache.get(lineAddress).equals(OWNED)) {
			procStatus = HITDIRTY;
		} else if (cache.get(lineAddress).equals(SHARED)) {
			procStatus = HIT;
		}

		String status = "";
		if (probeReadStatus) {
			if (prev_adjacent1.equals(INVALID)) {
				status = MISS;
			} else {
				if (prev_adjacent1.equals(MODIFIED)
						|| prev_adjacent1.equals(OWNED)) {
					status = HITDIRTY;
				} else {
					status = HIT;
				}
			}
			printBusBroadcastCacheStatus(cacheCurrent, cacheAdjacent1,
					status, prev, prev_adjacent1, lineAddress, "Read");
			if (prev_adjacent2.equals(INVALID)) {
				status = MISS;
			} else {
				if (prev_adjacent2.equals(MODIFIED)
						|| prev_adjacent2.equals(OWNED)) {
					status = HITDIRTY;
				} else {
					status = HIT;
				}
			}
			printBusBroadcastCacheStatus(cacheCurrent, cacheAdjacent2,
					status, prev, prev_adjacent2, lineAddress, "Read");
		}
		printCacheProcStatus(cacheCurrent, procStatus, prev, lineAddress,
				"Read");

	}

	/**
	 * @param cache
	 * @param adjacent1
	 * @param adjacent2
	 * @param state
	 * @param lineAddress
	 */
	private static void broadcastBusRead(LinkedHashMap<Integer, String> cache,
			LinkedHashMap<Integer, String> adjacent1,
			LinkedHashMap<Integer, String> adjacent2, String state,
			Integer lineAddress) {
		switch (state) {
		case INVALID:
			if (adjacent1.get(lineAddress).equals(INVALID)
					&& adjacent2.get(lineAddress).equals(INVALID)) {
				cache.put(lineAddress, EXCLUSIVE);
			} else if (adjacent1.get(lineAddress).equals(MODIFIED)) {
				cache.put(lineAddress, SHARED);
				adjacent1.put(lineAddress, OWNED);
			} else if (adjacent2.get(lineAddress).equals(MODIFIED)) {
				cache.put(lineAddress, SHARED);
				adjacent2.put(lineAddress, OWNED);
			} else if (adjacent1.get(lineAddress).equals(EXCLUSIVE)) {
				cache.put(lineAddress, SHARED);
				adjacent1.put(lineAddress, SHARED);
			} else if (adjacent2.get(lineAddress).equals(EXCLUSIVE)) {
				cache.put(lineAddress, SHARED);
				adjacent2.put(lineAddress, SHARED);
			} else if (adjacent1.get(lineAddress).equals(OWNED)) {
				cache.put(lineAddress, SHARED);
			} else if (adjacent2.get(lineAddress).equals(OWNED)) {
				cache.put(lineAddress, SHARED);
			}
			break;

		}

	}

	/**
	 * @param cacheCurrent
	 * @param cacheAdjacent1
	 * @param cacheAdjacent2
	 * @param lineAddress
	 */
	public static void procWrite(Integer cacheCurrent, Integer cacheAdjacent1,
		Integer cacheAdjacent2, Integer lineAddress) {
		boolean probeWriteStatus = false;
		String procStatus = "";
		
		// Get the caches
		LinkedHashMap<Integer, String> cache = cacheList.get(cacheCurrent);
		LinkedHashMap<Integer, String> adjacent1 = cacheList
				.get(cacheAdjacent1);
		LinkedHashMap<Integer, String> adjacent2 = cacheList
				.get(cacheAdjacent2);

		// Store previous states of cache
		String prev = cache.get(lineAddress);
		String prev_adjacent1 = adjacent1.get(lineAddress);
		String prev_adjacent2 = adjacent2.get(lineAddress);

		if (cache.get(lineAddress).equals(INVALID)) {
			cache.put(lineAddress, MODIFIED);
			procStatus = MISS;
			
			// ProbWrite
			broadcastBusWrite(cache, adjacent1, adjacent2,
					cache.get(lineAddress), lineAddress);
			probeWriteStatus = true;

		} else if (cache.get(lineAddress).equals(EXCLUSIVE)) {
			cache.put(lineAddress, MODIFIED);
			procStatus = HIT;

		} else if (cache.get(lineAddress).equals(MODIFIED)) {
			procStatus = HITDIRTY;

		} else if (cache.get(lineAddress).equals(OWNED)) {
			cache.put(lineAddress, MODIFIED);
			procStatus = HITDIRTY;
			
			// ProbWrite
			broadcastBusWrite(cache, adjacent1, adjacent2,
					cache.get(lineAddress), lineAddress);
			probeWriteStatus = true;

		} else if (cache.get(lineAddress).equals(SHARED)) {
			cache.put(lineAddress, MODIFIED);
			procStatus = HIT;
			
			// ProbWrite
			broadcastBusWrite(cache, adjacent1, adjacent2,
					cache.get(lineAddress), lineAddress);
			probeWriteStatus = true;
		}

		String status = "";
		// Block printOutput
		if (probeWriteStatus) {
			if (prev_adjacent1.equals(INVALID)) {
				status = MISS;
			} else {
				if (prev_adjacent1.equals(MODIFIED)
						|| prev_adjacent1.equals(OWNED)) {
					status = HITDIRTY;
				} else {
					status = HIT;
				}
			}
			printBusBroadcastCacheStatus(cacheCurrent, cacheAdjacent1,
					status, prev, prev_adjacent1, lineAddress, "Write");
			if (prev_adjacent2.equals(INVALID)) {
				status = MISS;
			} else {
				if (prev_adjacent2.equals(MODIFIED)
						|| prev_adjacent2.equals(OWNED)) {
					status = HITDIRTY;
				} else {
					status = HIT;
				}
			}
			printBusBroadcastCacheStatus(cacheCurrent, cacheAdjacent2,
					status, prev, prev_adjacent2, lineAddress, "Write");
		}
		printCacheProcStatus(cacheCurrent, procStatus, prev, lineAddress,
				"Write");
	}

	/**
	 * @param cache
	 * @param adjacent1
	 * @param adjacent2
	 * @param state
	 * @param lineAddress
	 */
	private static void broadcastBusWrite(LinkedHashMap<Integer, String> cache,
			LinkedHashMap<Integer, String> adjacent1,
			LinkedHashMap<Integer, String> adjacent2, String state,
			Integer lineAddress) {
		adjacent1.put(lineAddress, INVALID);
		adjacent2.put(lineAddress, INVALID);

	}

	/**
	 * @param cacheCurrent
	 * @param cacheAdjacent
	 * @param status
	 * @param prev
	 * @param prev_adjacent
	 * @param lineAddress
	 * @param operation
	 * @param probeStatus 
	 */
	public static void printBusBroadcastCacheStatus(Integer cacheCurrent,
			Integer cacheAdjacent, String status, String prev,
			String prev_adjacent, Integer lineAddress, String operation) {
		LinkedHashMap<Integer, String> cache = cacheList.get(cacheCurrent);
		LinkedHashMap<Integer, String> adjacent1 = cacheList.get(cacheAdjacent);
		System.out.print("Cache " + cacheAdjacent + "=>\t");
		System.out.print("Prob" + operation + ":" + cacheAdjacent + ", ");
		System.out.print("Bus" + operation + ": " + status + ", ");
		System.out.print(prev_adjacent + "->" + adjacent1.get(lineAddress));
		System.out.println("");
	}

	/**
	 * @param cacheCurrent
	 * @param procStatus
	 * @param prev
	 * @param lineAddress
	 * @param operation
	 */
	public static void printCacheProcStatus(Integer cacheCurrent,
			String procStatus, String prev, Integer lineAddress,
			String operation) {
		LinkedHashMap<Integer, String> cache = cacheList.get(cacheCurrent);
		System.out.print("Cache " + cacheCurrent + "=>\t");
		System.out.print("Proc" + operation + ": " + procStatus + ", ");
		System.out.println(prev + "->" + cache.get(lineAddress));
	}

}
