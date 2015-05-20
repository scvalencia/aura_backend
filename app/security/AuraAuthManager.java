package security;

import security.Encriptar;

/**
 * Created by scvalencia on 4/17/15.
 */

public class AuraAuthManager {

    public final static String CAESAR_CIPHER = "CAESAR_CIPHER";

    private String algorithm;
    private Encriptar instance;

    public AuraAuthManager(String algorithm) {
        if(algorithm.equals(CAESAR_CIPHER)) {
            instance = new Encriptar();
            this.algorithm = CAESAR_CIPHER;
        }
    }

    public String auraEncrypt(String word) {
        if(this.algorithm.equals(CAESAR_CIPHER)) {

            return caesarEncrypt(word);
        }
        else
            return null;
    }

    public String auraDecrypt(String word) {
        if(this.algorithm.equals(CAESAR_CIPHER))
            return caesarDecrypt(word);
        else
            return null;
    }

    private String caesarEncrypt(String word) {
        return instance.encriptando(word);
    }

    private String caesarDecrypt(String word) {
        return instance.desencriptando(word);
    }

    public String getAlgorithm() {
        return algorithm;
    }

    public void setAlgorithm(String algorithm) {
        this.algorithm = algorithm;
    }

    public Encriptar getInstance() {
        return instance;
    }

    public void setInstance(Encriptar instance) {
        this.instance = instance;
    }
}
