package de.tschebbischeff.lazyui;

/**
 * An empty handler for the default empty content page. Does nothing.
 */
class ContentPageEmpty extends ContentPage {

    @Override
    protected int getLayoutResourceId() {
        return R.layout.content_page_empty;
    }

    @Override
    protected void onCreate() {
    }

    @Override
    protected boolean onBeforeShow(boolean forced) {
        return true;
    }

    @Override
    protected void onAfterShow() {
    }

    @Override
    protected boolean onBeforeHide(boolean forced) {
        return true;
    }

    @Override
    protected void onAfterHide() {
    }

    @Override
    protected void onDestroy() {
    }

    @Override
    protected String[] getIntentFilterActions() {
        return new String[0];
    }

}
