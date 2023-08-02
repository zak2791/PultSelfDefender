package ru.startandroid.SelfDefender;

import android.widget.EditText;
import android.widget.TextView;

public class verificationCode {
    EditText etID;
    TextView tvCode;

    static int rec(int num){
        int dig = 0;
        String str_num = Integer.toString(num);
        for(int i = 0; i < str_num.length(); i++){
            dig +=  Character.getNumericValue(str_num.charAt(i));
        }
        if(dig > 9){
            return rec(dig);
        }else{
            return dig;
        }
    }

    static String gen_code(String ID) {
        String myString = ID;
        int lenString = myString.length();
        int[] myListStart = new int[lenString];
        int[] myListEnd = new int[lenString];
        for(int i=0; i < lenString; i++){
            myListStart[i] = (int) myString.charAt(i);
        }
        String code = "";
        for(int i=0; i < lenString; i++){
            if(i < lenString - 1){
                myListEnd[i] = myListStart[i] + myListStart[i + 1];
            }else{
                myListEnd[i] = myListStart[i] + myListStart[0];
            }
            code = code.concat(Integer.toString(rec(myListEnd[i])));
        }
        return code;
    }
}
