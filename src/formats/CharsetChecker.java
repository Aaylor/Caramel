package formats;

import java.nio.charset.Charset;
import java.nio.charset.CharsetEncoder;

/**
 *  Classe chargeant en mémoire des Charset permettant de vérifier si les
 *  chaîne de caractères sont bien encodée dans le Charset demandé.
 */
public class CharsetChecker {
    private static CharsetEncoder latin1Encoder
            = Charset.forName("ISO-8859-1").newEncoder();

    /**
     * Vérifie si la chaîne de caractère est bien encodée en latin-1.
     * @param str Chaîne de caractère à tester.
     * @return true si la chaîne est bien encodée.
     */
    public static boolean isCorrectlyEncoded(String str) {
        return latin1Encoder.canEncode(str);
    }
}
