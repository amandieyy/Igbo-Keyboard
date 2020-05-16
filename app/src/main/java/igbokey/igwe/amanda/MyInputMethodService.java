package igbokey.igwe.amanda;

import android.inputmethodservice.InputMethodService;
import android.inputmethodservice.Keyboard;
import android.inputmethodservice.KeyboardView;
import android.media.AudioManager;
import android.os.Vibrator;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.InputConnection;
import com.vdurmont.emoji.EmojiManager;

@SuppressWarnings("ALL")
public class MyInputMethodService extends InputMethodService implements KeyboardView.OnKeyboardActionListener {

    private boolean caps = false;
    private Keyboard igbo_keyboard;
    private Keyboard symbols_keyboard;
    private Keyboard emojis_keyboard;
    private Keyboard qwerty_keyboard;

    private CustomKeyboardView keyboardView;

    private AudioManager am;
    private Vibrator v;

    @Override
    public View onCreateInputView() {
        // Creating all Keyboard members
        igbo_keyboard = new Keyboard(this, R.xml.igbo_keyboard);
        symbols_keyboard = new Keyboard(this, R.xml.symbols);
        emojis_keyboard = new Keyboard(this, R.xml.emoji);
        qwerty_keyboard = new Keyboard(this, R.xml.qwerty);

        keyboardView = (CustomKeyboardView) getLayoutInflater().inflate(R.layout.keyboard_view, null);
        //Send ID of querty Kewboard to KeyboardView
        //It is needed there to prevent drawing red markers (like on IGO keyboard) on keys of standard English QWERTY keyboard
        keyboardView.setIdOfQwertyKeyboard(qwerty_keyboard.toString());

        keyboardView.setKeyboard(igbo_keyboard);
        keyboardView.setOnKeyboardActionListener(this);

        return keyboardView;
    }


    @SuppressWarnings("deprecation")
    @Override
    public void onKey(int primaryCode, int[] keyCodes) {

        playSound(primaryCode);
        InputConnection ic = getCurrentInputConnection();

        if (ic == null) return;
        switch (primaryCode) {
            case Keyboard.KEYCODE_DELETE:
                CharSequence selectedText = ic.getSelectedText(0);
                //Check if there is selection
                if (TextUtils.isEmpty(selectedText)) {
                    //If there is a Emoji before Cursor, 2 characters have to be deleted as Emojis contain 2 characters
                    //If not, just delete 1 character before Cursor
                    if(EmojiManager.isEmoji(getCurrentInputConnection().getTextBeforeCursor(2, 0).toString())) {
                        ic.deleteSurroundingText(2, 0);
                    } else {
                        ic.deleteSurroundingText(1, 0);
                    }
                } else {
                    // delete the selection
                    ic.commitText("", 1);
                }
                break;
            case Keyboard.KEYCODE_SHIFT:
                caps = !caps;
                //keyboard.setShifted(caps);
                keyboardView.invalidateAllKeys();
                keyboardView.changeCaps(caps);
                break;
            case Keyboard.KEYCODE_MODE_CHANGE:
                if (keyboardView != null) {
                    Keyboard current = keyboardView.getKeyboard();
                    if (current == symbols_keyboard || current == emojis_keyboard) {
                        keyboardView.setKeyboard(igbo_keyboard);
                    } else {
                        keyboardView.setKeyboard(symbols_keyboard);
                    }
                }
                break;
            case 9996:
                if (keyboardView != null) {
                    Keyboard current = keyboardView.getKeyboard();
                    if (current == symbols_keyboard || current == emojis_keyboard) {
                        keyboardView.setKeyboard(igbo_keyboard);
                    } else {
                        keyboardView.setKeyboard(emojis_keyboard);
                    }
                }
                break;
                //Primary codes for special characters
            case 9980:
                handleSpecialCharacters("ch");
                break;
            case 9981:
                handleSpecialCharacters("kw");
                break;
            case 9982:
                handleSpecialCharacters("kp");
                break;
            case 9983:
                handleSpecialCharacters("nw");
                break;
            case 9984:
                handleSpecialCharacters("ny");
                break;
            case 9985:
                handleSpecialCharacters("gb");
                break;
            case 9986:
                handleSpecialCharacters("gh");
                break;
            case 9987:
                handleSpecialCharacters("gw");
                break;
            case 9988:
                handleSpecialCharacters("sh");
                break;
            case 9991:
                //choose Keyboard, EN for example
                //InputMethodManager imeManager = (InputMethodManager) getApplicationContext().getSystemService(INPUT_METHOD_SERVICE);
                //imeManager.showInputMethodPicker();
                if (keyboardView != null) {
                    Keyboard current = keyboardView.getKeyboard();
                    if (current == qwerty_keyboard) {
                        keyboardView.setKeyboard(igbo_keyboard);
                    } else {
                        keyboardView.setKeyboard(qwerty_keyboard);
                    }
                }
                break;
            default:
                char code = (char)primaryCode;
                if (Character.isLetter(code)) {
                    if(caps){
                        code = Character.toUpperCase(code);
                    }
                    ic.commitText(String.valueOf(code), 1);
                } else {
                    ic.commitText(String.valueOf(Character.toChars(primaryCode)), 1);
                }

        }
    }

    private void handleSpecialCharacters(String character) {
        InputConnection ic = getCurrentInputConnection();
        if (caps) {
            ic.commitText(character.toUpperCase(), 1);
        } else {
            ic.commitText(character.toLowerCase(), 1);
        }
    }


    @Override
    public void onPress(int primaryCode) {

    }

    @Override
    public void onRelease(int primaryCode) { }

    @Override
    public void onText(CharSequence text) { }

    @Override
    public void swipeLeft() { }

    @Override
    public void swipeRight() { }

    @Override
    public void swipeDown() { }

    @Override
    public void swipeUp() { }


    private void playSound(int keyCode){

        v = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        am = (AudioManager)getSystemService(AUDIO_SERVICE);

        if (v != null && am != null) {
            v.vibrate(20);
            switch(keyCode){
                case 32:
                    am.playSoundEffect(AudioManager.FX_KEYPRESS_SPACEBAR);
                    break;
                case Keyboard.KEYCODE_DONE:
                case 10:
                    am.playSoundEffect(AudioManager.FX_KEYPRESS_RETURN);
                    break;
                case Keyboard.KEYCODE_DELETE:
                    am.playSoundEffect(AudioManager.FX_KEYPRESS_DELETE);
                    break;
                default: am.playSoundEffect(AudioManager.FX_KEYPRESS_STANDARD);
            }
        }

    }


}
