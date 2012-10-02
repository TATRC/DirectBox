#!/bin/sh

rm DirectBox.war
cd war
jar cvf ../DirectBox.war `find . -not -path "*/.svn/*" -not -type d`

