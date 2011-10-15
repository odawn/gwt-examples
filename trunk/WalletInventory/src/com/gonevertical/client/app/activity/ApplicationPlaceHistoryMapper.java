package com.gonevertical.client.app.activity;

import com.gonevertical.client.app.activity.places.SignInPlace;
import com.gonevertical.client.app.activity.places.WalletEditPlace;
import com.gonevertical.client.app.activity.places.WalletListPlace;
import com.google.gwt.place.shared.PlaceHistoryMapper;
import com.google.gwt.place.shared.PlaceTokenizer;
import com.google.gwt.place.shared.WithTokenizers;

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

@WithTokenizers({ 
  SignInPlace.Tokenizer.class, 
  WalletListPlace.Tokenizer.class, 
  WalletEditPlace.Tokenizer.class })
public interface ApplicationPlaceHistoryMapper /*<F>*/ extends PlaceHistoryMapper {
  
  /**
   * Sets the factory to be used to generate {@link PlaceTokenizer} instances.
   *
   * @param factory a factory of type F
   */
  //void setFactory(F factory);
  
}
