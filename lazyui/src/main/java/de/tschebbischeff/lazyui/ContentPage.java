package de.tschebbischeff.lazyui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.view.View;
import android.widget.TextView;

/**
 * Superclass containing callbacks for changing content screens.
 */
public abstract class ContentPage {

    /**
     * The activity managing this content screen
     */
    private Activity activity = null;

    /**
     * The class acting as an interface between the contents. Can store and retreive defined data.
     */
    private ContentPageSharedData contentPageSharedData = null;

    /**
     * Whether this content is currently shown
     */
    private boolean active = false;

    /**
     * The title of the error message to be shown to the user
     */
    private String errorTitle = null;

    /**
     * The error message to be shown to the user
     */
    private String errorMessage = null;

    /**
     * The layout in which to display the error message
     */
    private static final int ERROR_LAYOUT_ID = R.layout.content_page_error;

    /**
     * The id of the text view containing the error title
     */
    private static final int ERROR_TITLE_TV_ID = R.id.content_page_error_title;

    /**
     * The id of the text view containing the error message
     */
    private static final int ERROR_MESSAGE_TV_ID = R.id.content_page_error_message;

    /**
     * Child classes can determine via this method if they are (still) the currently shown content.
     * @return True if this content is currently shown, False else
     */
    boolean isActive() {
        return this.active;
    }

    /**
     * Child classes can get the instance of the activity that manages these contents. May be null if {@link ContentPage#isActive()} returns false.
     */
    Activity getActivity() {
        return this.activity;
    }

    /**
     * Child classes can get an instance of {@link ContentPageSharedData}, which is the same instance across all content screens.
     */
    ContentPageSharedData getContentPageSharedData() {
        return this.contentPageSharedData;
    }

    /**
     * Child classes can set an error to display automatically instead of the contents layout.
     */
    void setError(String title, String message) {
        this.errorTitle = title;
        this.errorMessage = message;
    }

    /**
     * Dispatches the request for the current layout resource id.
     * May instead return an error page if the underlying child class reported an error to this parenting class.
     */
    public int dispatchGetLayoutResourceId() {
        if (this.errorTitle == null) {
            return this.getLayoutResourceId();
        } else {
            return ERROR_LAYOUT_ID;
        }
    }

    /**
     * Dispatches the request for intent filter actions to the corresponding child.
     * @return The intent filter actions this content is interested in
     */
    public String[] dispatchGetIntentFilterActions() {
        return this.getIntentFilterActions();
    }

    /**
     * Dispatches the create task to the corresponding child.
     */
    public void dispatchOnCreate(ContentPageSharedData contentPageSharedData, Activity activity) {
        this.contentPageSharedData = contentPageSharedData;
        this.activity = activity;
        this.onCreate();
    }

    /**
     * Dispatches the before show task to the corresponding child.
     *
     * @return True if successful
     */
    public boolean dispatchOnBeforeShow(boolean forced) {
        return this.onBeforeShow(forced);
    }

    /**
     * Dispatches the after show task to the corresponding child.
     */
    public void dispatchOnAfterShow() {
        this.active = true;
        if (this.errorTitle != null) {
            ((TextView) this.getActivity().findViewById(ERROR_TITLE_TV_ID)).setText(this.errorTitle);
            ((TextView) this.getActivity().findViewById(ERROR_MESSAGE_TV_ID)).setText(this.errorMessage);
            this.errorTitle = null;
            this.errorMessage = null;
        } else {
            this.onAfterShow();
        }
    }

    /**
     * Dispatches the before hide task to the corresponding child.
     *
     * @return True if successful
     */
    public boolean dispatchOnBeforeHide(boolean forced) {
        return this.onBeforeHide(forced);
    }

    /**
     * Dispatches the after hide task to the corresponding child.
     */
    public void dispatchOnAfterHide() {
        this.active = false;
        this.onAfterHide();
    }

    /**
     * Dispatches the destroy task to the corresponding child.
     */
    public void dispatchOnDestroy() {
        this.activity = null;
        this.contentPageSharedData = null;
        this.onDestroy();
    }

    /**
     * Used to return the layout id from the child in a programmatic way.
     *
     * @return The layout id from which to inflate when this content is shown
     */
    protected abstract int getLayoutResourceId();

    /**
     * Defines the filter for the intents this content wants to receive.
     * @return An array of actions, that this content should be able to receive
     */
    protected abstract String[] getIntentFilterActions();

    /**
     * Called during the activity's lifecycle's onCreate.
     */
    protected abstract void onCreate();

    /**
     * Called when this content is about to be shown.
     * The layout of this content is not yet inflated at this point.
     *
     * @return True if successful
     */
    protected abstract boolean onBeforeShow(boolean forced);

    /**
     * Called after this content is fully shown.
     * The layout of this content is inflated at this point.
     */
    protected abstract void onAfterShow();

    /**
     * Called when this content is about to be hidden.
     * The layout of this content is still inflated at this point.
     *
     * @return True if successful
     */
    protected abstract boolean onBeforeHide(boolean forced);

    /**
     * Called when this content is hidden.
     * The layout of this content not inflated anymore at this point.
     */
    protected abstract void onAfterHide();

    /**
     * Called during the activity's lifecycle's onDestroy.
     */
    protected abstract void onDestroy();

    /**
     * Called when a view in this content's layout is clicked.
     */
    public void onClick(View view) {
    }

    /**
     * Called when the result of a permissions request arrives.
     */
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
    }

    /**
     * Called when an intent is received.
     */
    public void onReceive(Context context, Intent intent) {
    }
}
