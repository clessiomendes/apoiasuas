package org.apoiasuas.util

import org.apache.log4j.Level
import org.apache.log4j.PatternLayout
import org.apache.log4j.RollingFileAppender

/**
 * Created by clessio on 07/12/2016.
 */
class LogHelper {
    public static String getFileAppender(Level level) {
        String strLevel = level.toString().toLowerCase();
        return new RollingFileAppender(name: strLevel+'File', append: true, maxFileSize: '10000KB',
                file: AmbienteExecucao.getCaminhoRepositorioArquivos()+'/logs/'+strLevel+'.log',
//                fileNamePattern: AmbienteExecucao.getCaminhoRepositorioArquivos()+'/logs/'+strLevel+'%d{yyyy-MM-DD hh-mm}.gz',
                maxBackupIndex: 10, layout: new PatternLayout('(cc) %d{dd-MMM HH:mm:ss} %p %c{8} -> %m%n'),
                threshold: level);
    }

}
