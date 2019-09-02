package customProtocol.model;

import java.util.Random;

/*
 *
 * @author IcyTeck
 */
public class Quotes {

    //@ KILLED PLAYER 
    //# ATTAKER
    public static String[] data = new String[]{
        "@ you were murdered by #.",
        "# beasted @.",
        "# is too angry today, killed @.",
        "# slaughtered @.",
        "# just 123'd @.",
        "# KO'ed @.",
        "WoW! # is bloodthirsty after killing @ can somone stop him!?",
        "@ was brutally murdered by #",
        "# makes @ disappear from map.",
        "@ should take some PvP lessons from #.",
        "# stole @'s cookies.",
        "# broke @'s heart.",
        "# sent @ to ground.",
        "# just burried @.",
        "# wiped the floor with @'s face.",
        "@ is cleaning #'s shoes.",
        "@ failed to kill #.",
        "@ became #'s apprentice",
        "# says: Anyone wants to be floored like @?",
        "# reap @'s soul"
    };

    public static String[] answerQuotes = new String[]{
        "Hello @, yes I am. Want some pizza?",
        "I'am here @, Sorry i was just tidying up.",
        "Yes.",
        "Maybe I am maybe I am not. To be or not to be. That is the question.",
        "Yes. What is your favorite drink?",};

    public static String getRQuote() {
        int idx = new Random().nextInt(answerQuotes.length);
        String random = (answerQuotes[idx]);
        return random;
    }

    public static String getQuote() {
        int idx = new Random().nextInt(data.length);
        String random = (data[idx]);
        return random;
    }
}
