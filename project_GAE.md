
&lt;wiki:gadget url="https://wiki-gadgets.googlecode.com/git/gadgets/ads/banner2.xml" height="100" width="740" border="0" /&gt;

# My GAE Snippets #

## Query Date Range ##
> Here is how I query a date range with declaration of the import type Date, and then set the filter and the date to fiter. Don't forget to sort the query.

> [Source](http://code.google.com/p/gwt-examples/source/browse/trunk/Core/src/org/gonevertical/core/server/jdo/data/DataJoinJdo.java#490)
```
        public boolean deleteRecordsBefore(Date buildDate) {
		String qfilter = "joinUpdatedDate > buildDate";
		boolean success = false;
		PersistenceManager pm = sp.getPersistenceManager();
		try {
			Query q = pm.newQuery("select id from " + DataJoinJdo.class.getName());
			q.setFilter(qfilter);
			q.declareImports("import java.util.Date");
			q.declareParameters("Date buildDate");
			q.setOrdering("joinUpdatedDate desc");
			List<Key> ids = (List<Key>) q.execute(buildDate);
			Iterator<Key> itr = ids.iterator();
			while(itr.hasNext()) {
				Key idkey = itr.next();
				long id = idkey.getId();
				deleteById(id);
			}
			success = true;
			q.closeAll();
		} catch (Exception e) {
			e.printStackTrace();
			log.log(Level.SEVERE, "deleteRecordsBefore(): ", e);
		} finally {
			pm.close();
		}
		return success;
	}
```


&lt;wiki:gadget url="https://wiki-gadgets.googlecode.com/git/gadgets/ads/banner2.xml" height="100" width="740" border="0" /&gt;
