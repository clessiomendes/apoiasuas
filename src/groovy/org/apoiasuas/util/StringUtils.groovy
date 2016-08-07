package org.apoiasuas.util

import com.google.common.base.CaseFormat
import org.codehaus.plexus.util.Base64

import java.text.Normalizer
import java.util.regex.Pattern

/**
 * Created by home64 on 12/04/2015.
 */
class StringUtils {
    public static final Pattern PATTERN_TEM_NUMEROS = Pattern.compile("(.)*(\\d)(.)*")
    public static final Pattern PATTERN_TEM_LETRAS = Pattern.compile("(.)*[a-zA-Z]+(.)*")
    public static final String PATTERN_URL = '(http|https)://([\\w_-]+(?:(?:\\.[\\w_-]+)+))([\\w.,@?^=%&:/~+#-]*[\\w@?^=%&/~+#-])?'
//    public static final String PATTERN_URL = "(http|ftp|https)://([\\w_-]+(?:(?:\\.[\\w_-]+)+))([\\w.,@?^=%&:/~+#-]*[\\w@?^=%&/~+#-])?"

    public static String firstLowerCase(String value) {
        return value[0].toLowerCase() + value.substring(1)
    }
    public static String upperToCamelCase(String value) {
        return CaseFormat.UPPER_UNDERSCORE.to(CaseFormat.UPPER_CAMEL, value);
    }
    public static String toHtml(String s) {
        StringBuilder builder = new StringBuilder();
        boolean previousWasASpace = false;
        boolean ul = false;
        for( char c : s.toCharArray() ) {
            if( c == ' ' ) {
                if( previousWasASpace ) {
                    builder.append("&nbsp;");
                    previousWasASpace = false;
                    continue;
                }
                previousWasASpace = true;
            } else {
                previousWasASpace = false;
            }
            switch(c) {
                case '{': builder.append("<b>"); break;
                case '}': builder.append("</b>"); break;
//                case '[': builder.append("<ul><li>"); ul=true; break;
//                case ']': builder.append("</li></ul>"); ul=false; break;
                case '<': builder.append("&lt;"); break;
                case '>': builder.append("&gt;"); break;
//                case '&': builder.append("&amp;"); break;
                case '"': builder.append("&quot;"); break;
//                case '\n': builder.append(ul ? "</li><li>" : "<br>"); break;
                case '\n': builder.append("<br>"); break;
            // We need Tab support here, because we print StackTraces as HTML
                case '\t': builder.append("&nbsp; &nbsp; &nbsp;"); break;
                default:
                    if( c < 128 ) {
                        builder.append(c);
                    } else {
                        builder.append("&#").append((int)c).append(";");
                    }
            }
        }
        String result = builder.toString();

        //Formatando URLs como hyperlinks HTML
        result = result.replaceAll('(http://www|https://www)', 'www'); //remove o http:// de urls contendo www
        result = result.replaceAll('www', 'http://www'); //adiciona o http:// em urls contendo www
        result = result.replaceAll(PATTERN_URL, '<a href="$0" target="_blank">$0</a>'); //remove o http:// de urls contendo www

        //Trocando linhas em branco por um espaçamento de meia linha
//        result = result.replaceAll('(<br>\n){2,}','<span style="font-size: 50%;"><br></span>')

        return result;
    }
    public static String removeAcentos(String string) {
        if (string != null){
            string = Normalizer.normalize(string, Normalizer.Form.NFD);
            string = string.replaceAll("[^\\p{ASCII}]", "");
        }
        return string;
    }
    public static String htmlSpaces(int count) {
        String result = "";
        for (int i = 0; i < count; i++)
            result += "&nbsp;";
        return result;
    }
    public static boolean isNotBlank(String s) {
        return org.apache.commons.lang.StringUtils.isNotBlank(s)
    }

    /**
     * Converte de byte[] para String
     * http://www.codesolution.org/encode-a-file-into-base64-format/
     * @param file
     * @return
     */
    public static String encodeFileToBase64Binary(byte[] bytes){
        String encodedfile = null;
        try {
//            FileInputStream fileInputStreamReader = new FileInputStream(file);
//            byte[] bytes = new byte[(int)file.length()];
//            fileInputStreamReader.read(bytes);
            encodedfile = Base64.encodeBase64(bytes).toString();
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch <span id="IL_AD4" class="IL_AD">block</span>
            e.printStackTrace();
    } catch (IOException e) {
        // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return encodedfile;
    }

    public static String toHtmlNoEmptyLines(String s) {
        //Remove linhas em branco http://stackoverflow.com/a/4123485/1916198
//        s.replaceAll("\n[ \t]*\n", "\n");
        return toHtml(s.replaceAll("(?m)^[ \t]*\r?\n", ""))
    }
}
