package igbokey.igwe.amanda;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.inputmethodservice.Keyboard.Key;
import android.inputmethodservice.KeyboardView;
import android.os.Build;
import android.util.AttributeSet;
import android.util.TypedValue;
import java.util.List;

@SuppressWarnings("ALL")
public class CustomKeyboardView extends KeyboardView {

    private boolean caps;
    private final Context context;
    private String idOfQwertyKeyboard;

    private boolean longpress = false;

    public CustomKeyboardView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
    }

    public void setIdOfQwertyKeyboard(String idOfQwertyKeyboard) {
        this.idOfQwertyKeyboard = idOfQwertyKeyboard;
    }

    public void changeCaps(boolean caps) {
        this.caps = caps;
    }

    private boolean StringChecker(String a, String b) {
        return a.equalsIgnoreCase(b);
    }

    @SuppressWarnings("deprecation")
    @Override
    public void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (!longpress) {
            //Paint for little red symbols indicating Popup Characters
            Paint paint = new Paint();
            paint.setTextAlign(Paint.Align.CENTER);
            paint.setTextSize(getDefaultTextSize(context) / (float)2.0);
            paint.setColor(Color.RED);

            //Paint for custom Arial Font
            Paint mPaint = new Paint();
            mPaint.setTextAlign(Paint.Align.CENTER);
            mPaint.setTextSize(getDefaultTextSize(context) * (float)1.3);
            mPaint.setColor(Color.BLACK);

            //Plain white Rectacle to overdraw exsting keys (as we want to use custom font)
            Paint rectPaint = new Paint();
            rectPaint.setColor(Color.WHITE);

            //Get Font based on Android Version
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                mPaint.setTypeface(context.getResources().getFont(R.font.arial));
            } else {
                mPaint.setTypeface(Typeface.createFromAsset(context.getAssets(),"arial.ttf"));
            }

            List<Key> keys = getKeyboard().getKeys();
            for(Key key: keys) {

                //Overdraw all keys with blank white Rect
                Rect rect = new Rect(key.x, key.y, key.x + key.width, key.y + key.height);
                if (key.label != null) {
                    canvas.drawRect(rect, rectPaint);
                }

                //Draw little red symbol on following keys, but just on IGBO Keyboard
                if (!StringChecker(getKeyboard().toString(), idOfQwertyKeyboard)) {
                    if(key.label != null) {
                        if (key.label.equals("e")
                                || key.label.equals("u")
                                || key.label.equals("i")
                                || key.label.equals("o")
                                || key.label.equals("kp")
                                || key.label.equals("kw")
                                || key.label.equals("gb")
                                || key.label.equals("gh")
                                || key.label.equals("gw")
                                || key.label.equals("nw")
                                || key.label.equals("ny")
                                || key.label.equals("a")) {
                            canvas.drawText("∞", key.x + (key.width - (float)(key.width * 0.5)), key.y + 30, paint);
                        } else if (key.label.equals("sh")) {
                            canvas.drawText("s", key.x + (key.width - (float)(key.width * 0.5)), key.y + 30, paint);
                        }
                    }
                } else {
                    if (key.label != null) {
                        canvas.drawRect(rect, rectPaint);
                    }
                }

                //Draw custom font on keys
                if (key.label != null) {
                    String keyLabel = key.label.toString();
                    if (caps && !keyLabel.equalsIgnoreCase("Space")) {
                        keyLabel = keyLabel.toUpperCase();
                    }
                    canvas.drawText(keyLabel, key.x + (key.width / 2), (float) (key.y + (key.height / 1.5)), mPaint);
                }
            }

        } else {
            longpress = false;
        }

    }

    private static int getDefaultTextSize(Context context) {
        TypedValue typedValue = new TypedValue();
        context.getTheme().resolveAttribute(android.R.attr.textAppearance, typedValue, true);
        int[] textSizeAttr = new int[] { android.R.attr.textSize };
        TypedArray typedArray = context.obtainStyledAttributes(typedValue.data, textSizeAttr);
        int textSize = typedArray.getDimensionPixelSize(0, -1);
        typedArray.recycle();
        return textSize;
    }


    @Override
    protected boolean onLongPress(Key popupKey) {

        if (popupKey.popupCharacters != null) {
            longpress = true;
        } else {
            longpress = false;
        }
        return super.onLongPress(popupKey);
    }


}

