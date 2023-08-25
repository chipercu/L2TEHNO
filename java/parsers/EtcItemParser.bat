@echo off
title L2Open: EtcItem SQL to XML converter

java -cp bsf.jar;bsh-2.0.jar;javolution.jar;c3p0-0.9.1.2.jar;mysql-connector-java-bin.jar;l2openserver.jar;jython.jar;rrd4j-2.0.5.jar;jacksum.jar;dom4j-2.0.0-ALPHA-2.jar l2open.gameserver.xml.parser.EtcItemParser

pause