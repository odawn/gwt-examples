
&lt;wiki:gadget url="https://wiki-gadgets.googlecode.com/git/gadgets/ads/banner2.xml" height="100" width="740" border="0" /&gt;

# Java Object Serialization #
> I found I could store my java object into mysql binary field and deserialize for later use. For example, caching an object that causes huge query load each time its requested. Or you can have the server rebuild the objects every night. I do this for my charts.

> ## Byte Serialization ##
> For server side use only. Use this to store a java object into a database and use it agian. The table's field must be binary (case sensitive)
```
public class Serialize {

	public Serialize() {
	}
	
	/**
	 * serialize an object - BASE64Encoder
	 * 
	 * @param o
	 * @return
	 */
	public String serialize(Object o) {
		
		if (o == null) {
			return null;
		}
		
		String s = null;
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			ObjectOutputStream oout = new ObjectOutputStream(baos);
			oout.writeObject(o);
			oout.close();
			byte[] buf = baos.toByteArray();
			s = new sun.misc.BASE64Encoder().encode(buf);
		} catch (IOException e) {
			e.printStackTrace();
		}
	    return s;
	}
	
	/**
	 * deserialize an object - BASE64Decoder
	 * 
	 * @param s
	 * @return
	 */
	public Object deserialize(String s) {
		
		if (s == null) {
			return null;
		}
		
		byte[] buf = null;
		try {
			buf = new sun.misc.BASE64Decoder().decodeBuffer(s);
		} catch (IOException e) {
			e.printStackTrace();
		}

		Object o = null;
		if (buf != null) {
			ObjectInputStream objectIn = null;
			try {
				objectIn = new ObjectInputStream(new ByteArrayInputStream(buf));
			} catch (IOException e) {
				e.printStackTrace();
			}
			try {
				o = objectIn.readObject();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (ClassNotFoundException e) {
				e.printStackTrace();
			} 
		}
		
		return o;
	}
}
```

> ## GWT serialzation ##
> > I tried serializing using gwt, but found there wasn't a deserialize method to use on the server side.


> ### Encoding ###
```
public class MySerializationPolicy extends SerializationPolicy {

	public MySerializationPolicy() {
		
	}
	
	@Override
	public boolean shouldDeserializeFields(Class<?> clazz) {
		return false;
	}

	@Override
	public boolean shouldSerializeFields(Class<?> clazz) {
		return false;
	}

	@Override
	public void validateDeserialize(Class<?> clazz)
			throws SerializationException {
	}

	@Override
	public void validateSerialize(Class<?> clazz) throws SerializationException {
	}

}



public class SerializeGwt {

	public static void main(String[] args) {
		
                // my object i wanted to serialize, which has data in it.
		DrillDownData drill = new DrillDownData();
		
		Class<?> responseClass = drill.getClass();

		// make a serialization policy of my own to use, normally deteremined by rpc request
		MySerializationPolicy serializationPolicy = new MySerializationPolicy();
		
		
		ServerSerializationStreamWriter stream = new ServerSerializationStreamWriter(serializationPolicy);

	    stream.prepareToWrite();
	    
	    if (responseClass != void.class) {
	      try {
			stream.serializeValue(drill, responseClass);
			} catch (SerializationException e) {
	
				e.printStackTrace();
			}
	    }

	    String bufferStr = stream.toString();
	      
		
		
		System.out.println("hash: " + bufferStr);
	}
}
```

> ### Decoding ###
> > I didn't want to spend the time to figure this out. It seemed that this was buried deep. Although, it looks doable with some hacking.


&lt;wiki:gadget url="https://wiki-gadgets.googlecode.com/git/gadgets/ads/banner2.xml" height="100" width="740" border="0" /&gt;
