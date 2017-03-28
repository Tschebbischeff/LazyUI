package de.tschebbischeff.lazyui;

/**
 * To use Lazy UI you will need to implement this interface to handle all callbacks from lazy UI properly.
 * Provide an implementation of this class to the main Lazy UI class: {@link ContentPageLibrary}.
 */
public interface ILazyUiCallbacks {

    /**
     * This method is called to inform the implementing class, that the intents filtered are changed.
     * (Most probably due to a changing content page)
     * @param filterActions The filter actions, the content page wants to receive
     */
    void refreshBroadcastReceiverFilter(String[] filterActions);
}
