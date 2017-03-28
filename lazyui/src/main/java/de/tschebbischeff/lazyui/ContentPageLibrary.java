package de.tschebbischeff.lazyui;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import java.util.HashMap;
import java.util.Iterator;

/**
 * This class provides the API for the LazyUI by storing {@link ContentPage}s with an associated menu item.
 * The menu item is saved by layout ID.
 */
public class ContentPageLibrary {

    /**
     * All content pages accessible by the corresponding menu items id
     */
    private HashMap<Integer, ContentPage> contentPages;
    /**
     * The id of the currently shown content, to manage callbacks.
     */
    private int selectedContent = 0;
    /**
     * The linear layout in which the content pages are inflated.
     */
    private LinearLayout contentWrapper = null;
    /**
     * The layout inflater to use for inflating the content pages.
     */
    private LayoutInflater layoutInflater = null;
    /**
     * The class handling callbacks from Lazy UI
     */
    private ILazyUiCallbacks callbackHandler = null;
    /**
     * The class defining which data can be shared between the content pages
     */
    private ContentPageSharedData contentPageSharedData = null;

    /**
     * Create a new library of content pages.
     *
     * @param contentWrapperLayout The linear layout which should contain the lazy UI.
     *                             (All content is removed from this layout before the content pages are shown)
     */
    @SuppressLint("UseSparseArrays")
    public ContentPageLibrary(LinearLayout contentWrapperLayout, LayoutInflater layoutInflater, ContentPageSharedData contentPageSharedData, ILazyUiCallbacks callbackHandler) {
        this.contentPages = new HashMap<>();
        this.contentPages.put(0, new ContentPageEmpty());
        this.contentWrapper = contentWrapperLayout;
        this.layoutInflater = layoutInflater;
        this.contentPageSharedData = contentPageSharedData;
        this.callbackHandler = callbackHandler;
    }

    /**
     * Adds a new content page to the library. The page is automatically associated with the provided menu item.
     * @param menuItemResourceId The id of the menu item, the content page is linked to
     * @param newPage An object representing the content page. Usually just extend {@link ContentPage}
     */
    public void addContentPage(int menuItemResourceId, ContentPage newPage, Activity activity) {
        if (menuItemResourceId > 0) {
            removeContentPage(menuItemResourceId);
            this.contentPages.put(menuItemResourceId, newPage);
            newPage.dispatchOnCreate(this.contentPageSharedData, activity);
        }
    }

    /**
     * Removes the page associated with a menu item. To replace a menu item with a different content page,
     * you can simply re-add a different content page with the same menu item id!
     * Fails silently (if there was no existing association before)
     * @param menuItemResourceId The id of the menu item, of which to remove the association
     */
    public void removeContentPage(int menuItemResourceId) {
        if (this.contentPages.containsKey(menuItemResourceId) && menuItemResourceId > 0) {
            this.contentPages.get(menuItemResourceId).dispatchOnDestroy();
            this.contentPages.remove(menuItemResourceId);
        }
    }

    /**
     * Shows a content page based on the given menu item.
     *
     * @param menuItemResourceId The registered resource id of a menu item with which a content page is associated
     * @param activity The activity containing the wrapper layout in which to inflate the new content
     * @return Whether the content page could be shown or not (If the content page is not shown, this can have multiple reasons)
     */
    public boolean show(int menuItemResourceId, Activity activity) {
        if (!this.contentPages.containsKey(menuItemResourceId)) return false;
        ContentPage currentPage = this.contentPages.get(this.selectedContent);
        ContentPage nextPage = this.contentPages.get(menuItemResourceId);
        //LinearLayout contentWrapper = (LinearLayout) activity.findViewById(R.id.content_wrapper);
        if (contentWrapper != null && layoutInflater != null) {
            if (currentPage.dispatchOnBeforeHide(false)) {
                contentWrapper.removeAllViews();
                currentPage.dispatchOnAfterHide();
                if (nextPage.dispatchOnBeforeShow(false)) {
                    layoutInflater.inflate(nextPage.dispatchGetLayoutResourceId(), contentWrapper);
                    selectedContent = menuItemResourceId;
                    nextPage.dispatchOnAfterShow();
                    this.callbackHandler.refreshBroadcastReceiverFilter(nextPage.dispatchGetIntentFilterActions());
                    return true;
                } else {
                    currentPage.dispatchOnBeforeShow(true);
                    layoutInflater.inflate(currentPage.dispatchGetLayoutResourceId(), contentWrapper);
                    currentPage.dispatchOnAfterShow();
                    this.callbackHandler.refreshBroadcastReceiverFilter(currentPage.dispatchGetIntentFilterActions());
                    return false;
                }
            }
        }
        return false;
    }

    /**
     * Dispatches the onAfterShow event to the currently selected content
     */
    public void dispatchOnResume() {
        ContentPage currentPage = this.contentPages.get(this.selectedContent);
        currentPage.dispatchOnBeforeShow(true);
        if (this.contentWrapper != null) {
            this.layoutInflater.inflate(currentPage.dispatchGetLayoutResourceId(), contentWrapper);
        }
        //Resume even if there was an error in refreshing the UI (failsafe, will trigger only a visual bug)
        currentPage.dispatchOnAfterShow();
    }

    /**
     * Dispatches the onBeforeHide event to the currently selected content
     */
    public void dispatchOnPause() {
        ContentPage currentPage = this.contentPages.get(this.selectedContent);
        currentPage.dispatchOnBeforeHide(true);
        if (this.contentWrapper != null) {
            this.contentWrapper.removeAllViews();
        }
        currentPage.dispatchOnAfterHide();
    }

    /**
     * Dispatches clicks to the currently shown content
     * @param view The clicked view
     */
    public void dispatchOnClick(View view) {
        this.contentPages.get(this.selectedContent).onClick(view);
    }

    /**
     * Dispatches request results of permissions to the currently shown content
     * @param requestCode The request code supplied at the request of the permissions.
     * @param permissions The permissions requested.
     * @param grantResults The results of the request per permission.
     */
    public void dispatchOnRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        this.contentPages.get(this.selectedContent).onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    /**
     * Dispatches received intents to the currently shown content
     * @param context The context
     * @param intent The received intent
     */
    public void dispatchOnReceive(Context context, Intent intent) {
        this.contentPages.get(this.selectedContent).onReceive(context, intent);
    }
}
