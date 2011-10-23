package com.gonevertical.client.app.activity;

import com.google.gwt.place.shared.PlaceHistoryMapperWithFactory;
import com.google.gwt.place.shared.PlaceTokenizer;
import com.google.web.bindery.requestfactory.shared.RequestFactory;

/**
 * PlaceHistoryMapper interface is used to attach all places which the
 * PlaceHistoryHandler should be aware of. This is done via the @WithTokenizers
 * annotation or by extending PlaceHistoryMapperWithFactory and creating a
 * separate TokenizerFactory.
 */

/**
 * A {@link PlaceHistoryMapper} that can get its {@link PlaceTokenizer}
 * instances from a factory.
 * 
 * @param <F> factory type
 */
// @WithTokenizers({ SignInPlace.Tokenizer.class, WalletListPlace.Tokenizer.class, WalletEditPlace.Tokenizer.class })
public interface ApplicationPlaceHistoryMapper extends PlaceHistoryMapperWithFactory<RequestFactory> {
}
