#!/bin/bash
SRC=/var/apoiasuas/deploy

#if [ ! -d "$SRC" ]; then
#    mkdir "$SRC"
#    echo "$SRC" criado
#fi

cd "$SRC" || { printf "cd failed, exiting\n" >&2;  return 1; }

#Obtem o caminho para download dos fontes (default=master)
printf "tag/branch/commit: \n"
read -e -i "master" gitsrc
githuburl=https://github.com/clessiomendes/apoiasuas/archive/$gitsrc.zip
echo baixando de $githuburl

#faz o download dos fontes em um arquivo src.zip
wget -O $SRC/src.zip $githuburl

#descompacta src.zip em ./apoiasuas-.../
rm -r $SRC/apoiasuas-$gitsrc
unzip -q $SRC/src.zip -d $SRC
cd $SRC/apoiasuas-$gitsrc/todos
grails prod war --non-interactive --stacktrace -Dorg.apoiasuas.modo=prod -Dorg.apoiasuas.runtimeHospedagem=clevercloud -Dorg.apoiasuas.bancoDeDados=postgres -Dgrails.project.work.dir=$SRC/war


#git clone "$gitsrc"

#unset gitsrc
