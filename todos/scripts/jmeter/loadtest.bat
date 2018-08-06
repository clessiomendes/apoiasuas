c:
cd C:\java\apache-jmeter-3.2\bin
rmdir /s /q C:\java\apache-jmeter-3.2\bin\report-output
setlocal
set id=%RANDOM%
del jmeter-results%id%.jtl
jmeter -n -t "C:\Dropbox\develop\apoiasuas\scripts\jmeter\jmeter-login-familia-identidade.jmx" -l jmeter-results%id%.jtl -e -o results%id%
start C:\java\apache-jmeter-3.2\bin\report-output\index.html