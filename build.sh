pandoc -s -S --filter pandoc-citeproc --bibliography="conf/library.bib" --number-sections --template="conf/thesis.latex" --listings -o Nayrolles_PhD_S2018.pdf *.md