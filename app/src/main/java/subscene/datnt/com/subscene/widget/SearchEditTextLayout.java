package subscene.datnt.com.subscene.widget;

import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.content.Context;
import android.os.Build;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;

import subscene.datnt.com.subscene.R;
import subscene.datnt.com.subscene.utils.AnimUtils;
import subscene.datnt.com.subscene.utils.CommonUtils;
import subscene.datnt.com.subscene.utils.Globals;

public class SearchEditTextLayout extends FrameLayout {

    private static final float EXPAND_MARGIN_FRACTION_START = 0.8f;
    private static final int ANIMATION_DURATION = 200;
    private int mTopMargin;
    private int mBottomMargin;
    private int mLeftMargin;
    private int mRightMargin;
    private float mCollapsedElevation;
    protected boolean mIsExpanded = false;
    protected boolean mIsFadedOut = false;
    private View mCollapsed;
    private View mExpanded;
    private EditText mSearchView;
    private View mCollapsedSearchBox;
    private View mOverflowButtonView;
    private View mBackButtonView;
    private View mClearButtonView;
    private ValueAnimator mAnimator;
    private Callback mCallback;
    /**
     * Listener for the back button next to the search view being pressed
     */
    public interface Callback {
        void onBackButtonClicked();

        void onSearchViewClicked();
    }

    public SearchEditTextLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public void setCallback(Callback listener) {
        mCallback = listener;
    }

    @Override
    protected void onFinishInflate() {
        mTopMargin = (int) CommonUtils.convertDpToPixel(Globals.TOP_MARGIN, getContext());
        mBottomMargin = (int) CommonUtils.convertDpToPixel(Globals.BOTTOM_MARGIN, getContext());
        mLeftMargin = (int) CommonUtils.convertDpToPixel(Globals.LEFT_MARGIN, getContext());
        mRightMargin = (int) CommonUtils.convertDpToPixel(Globals.RIGHT_MARGIN, getContext());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mCollapsedElevation = getElevation();
        }
        mCollapsed = findViewById(R.id.search_box_collapsed);
        mExpanded = findViewById(R.id.search_box_expanded);
        mSearchView = (EditText) mExpanded.findViewById(R.id.search_view);
        mCollapsedSearchBox = findViewById(R.id.search_box_start_search);
        mOverflowButtonView = findViewById(R.id.dialtacts_options_menu_button);
        mBackButtonView = findViewById(R.id.search_back_button);
        mClearButtonView = findViewById(R.id.search_close_button);
        // Convert a long click into a click to expand the search box, and then long click on the
        // search view. This accelerates the long-press scenario for copy/paste.
        mCollapsedSearchBox.setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                mCollapsedSearchBox.performClick();
                mSearchView.performLongClick();
                return false;
            }
        });

        mSearchView.setOnFocusChangeListener(new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    showInputMethod(v);
                } else {
                    hideInputMethod(v);
                }
            }
        });

        mSearchView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCallback != null) {
                    mCallback.onSearchViewClicked();
                }
            }
        });

        mSearchView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mClearButtonView.setVisibility(TextUtils.isEmpty(s) ? View.GONE : View.VISIBLE);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        findViewById(R.id.search_close_button).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mSearchView.setText(null);
            }
        });

        findViewById(R.id.search_back_button).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCallback != null) {
                    mCallback.onBackButtonClicked();
                }
            }
        });

        super.onFinishInflate();
    }

    public static void showInputMethod(View view) {
        final InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(
                Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.showSoftInput(view, 0);
        }
    }

    public static void hideInputMethod(View view) {
        final InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(
                Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    @Override
    public boolean dispatchKeyEventPreIme(KeyEvent event) {
        return super.dispatchKeyEventPreIme(event);
    }

    public void fadeIn() {
        AnimUtils.fadeIn(this, ANIMATION_DURATION);
        mIsFadedOut = false;
    }

    public void setVisible(boolean visible) {
        if (visible) {
            setAlpha(1);
            setVisibility(View.VISIBLE);
            mIsFadedOut = false;
        } else {
            setAlpha(0);
            setVisibility(View.GONE);
            mIsFadedOut = true;
        }
    }

    public void expand(boolean animate, boolean requestFocus) {
        updateVisibility(true /* isExpand */);

        if (animate) {
            AnimUtils.crossFadeViews(mExpanded, mCollapsed, ANIMATION_DURATION);
            mAnimator = ValueAnimator.ofFloat(EXPAND_MARGIN_FRACTION_START, 0f);
            setMargins(EXPAND_MARGIN_FRACTION_START);
            prepareAnimator(true);
        } else {
            mExpanded.setVisibility(View.VISIBLE);
            mExpanded.setAlpha(1);
            setMargins(0f);
            mCollapsed.setVisibility(View.GONE);
        }
        // Set 9-patch background. This owns the padding, so we need to restore the original values.
        int paddingTop = this.getPaddingTop();
        int paddingStart = this.getPaddingStart();
        int paddingBottom = this.getPaddingBottom();
        int paddingEnd = this.getPaddingEnd();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            setElevation(0);
        }
        setPaddingRelative(paddingStart, paddingTop, paddingEnd, paddingBottom);

        if (requestFocus) {
            mSearchView.requestFocus();
        }
        mIsExpanded = true;
    }

    public void collapse(boolean animate) {
        updateVisibility(false /* isExpand */);

        if (animate) {
            AnimUtils.crossFadeViews(mCollapsed, mExpanded, ANIMATION_DURATION);
            mAnimator = ValueAnimator.ofFloat(0f, 1f);
            prepareAnimator(false);
        } else {
            mCollapsed.setVisibility(View.VISIBLE);
            mCollapsed.setAlpha(1);
            setMargins(1f);
            mExpanded.setVisibility(View.GONE);
        }

        mIsExpanded = false;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            setElevation(mCollapsedElevation);
        }
        setBackgroundResource(R.drawable.rounded_corner);
    }

    /**
     * Updates the visibility of views depending on whether we will show the expanded or collapsed
     * search view. This helps prevent some jank with the crossfading if we are animating.
     *
     * @param isExpand Whether we are about to show the expanded search box.
     */
    private void updateVisibility(boolean isExpand) {
        int collapsedViewVisibility = isExpand ? View.GONE : View.VISIBLE;
        int expandedViewVisibility = isExpand ? View.VISIBLE : View.GONE;
        mCollapsedSearchBox.setVisibility(collapsedViewVisibility);
        mOverflowButtonView.setVisibility(collapsedViewVisibility);
        mBackButtonView.setVisibility(expandedViewVisibility);
        if (TextUtils.isEmpty(mSearchView.getText())) {
            mClearButtonView.setVisibility(View.GONE);
        } else {
            mClearButtonView.setVisibility(expandedViewVisibility);
        }
    }

    private void prepareAnimator(final boolean expand) {
        if (mAnimator != null) {
            mAnimator.cancel();
        }

        mAnimator.addUpdateListener(new AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                final Float fraction = (Float) animation.getAnimatedValue();
                setMargins(fraction);
            }
        });

        mAnimator.setDuration(ANIMATION_DURATION);
        mAnimator.start();
    }

    public boolean isExpanded() {
        return mIsExpanded;
    }

    public boolean isFadedOut() {
        return mIsFadedOut;
    }

    /**
     * Assigns margins to the search box as a fraction of its maximum margin size
     *
     * @param fraction How large the margins should be as a fraction of their full size
     */
    private void setMargins(float fraction) {
        LayoutParams params = (LayoutParams) getLayoutParams();
        params.topMargin = (int) (mTopMargin * fraction);
        params.bottomMargin = (int) (mBottomMargin * fraction);
        params.leftMargin = (int) (mLeftMargin * fraction);
        params.rightMargin = (int) (mRightMargin * fraction);
        requestLayout();
    }
}
