/**
 * 
 */
package com.pingtech.hgqw.utils.xml;

/**
 * @author pingtech
 * 
 *         灏佽绫讳技hashmap镄勭畻娉曪紝鐢ㄤ簬淇濆瓨鏁版嵁镄勬搷浣?
 * 
 */
public class HashBuild {

	private Object[] hashBuild = null;

	private int curr = 0;

	public HashBuild(int i) {
		hashBuild = new Object[i + 1];
	}

	/**
	 * 镙稿绩绠楁硶镄勪綅缃?
	 * 
	 * @param key
	 * @param value
	 */
	public void put(String key, Object value) {
		if (key != null && !key.equals("")) {
			if (value == null) {
				String[] s = { key, "" };
				if (hashBuild.length > curr) {
					hashBuild[curr] = s;
				}
				curr++;
			} else {
				if (value instanceof HashBuild) {
					if (hashBuild.length > curr) {
						Object[] hashBuild1 = new Object[2];
						hashBuild1[0] = key;
						hashBuild1[1] = (HashBuild) value;
						hashBuild[curr] = hashBuild1;
					}
					curr++;
				} else {
					String[] s = { key, String.valueOf(value) };
					if (hashBuild.length > curr) {
						hashBuild[curr] = s;
					}
					curr++;
				}
			}

		}
	}

	public Object[] get() {
		return hashBuild;
	}

}
